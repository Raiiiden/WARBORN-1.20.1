package com.raiiiden.warborn.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.raiiiden.warborn.WARBORN;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * GeckoLib-based renderer for plate insertion animations
 * Uses player arms and a blockbench model to create smooth animations
 * VERY WIP NOT IN USE
 */
public class PlateInsertRenderer<T extends Item & GeoAnimatable> extends GeoItemRenderer<T> {
    private static final float SCALE_RECIPROCAL = 1.0f / 16.0f;
    private static final ResourceLocation PLATE_MODEL = new ResourceLocation(WARBORN.MODID, "geo/item/armor/plate_insert.geo.json");
    private final Set<String> hiddenBones = new HashSet<>();
    public ItemDisplayContext transformType;
    public boolean renderArms = false;
    protected MultiBufferSource currentBuffer;
    protected RenderType renderType;

    public PlateInsertRenderer(GeoModel<T> model) {
        super(model);
    }

    /**
     * Enables arm rendering for the next render pass
     */
    public void enableArmRendering() {
        this.renderArms = true;
    }

    /**
     * Hides a specific bone by name
     */
    public void hideBone(String name) {
        this.hiddenBones.add(name);
    }

    /**
     * Shows a previously hidden bone
     */
    public void showBone(String name) {
        this.hiddenBones.remove(name);
    }

    @Override
    public void actuallyRender(PoseStack matrixStack, T animatable, BakedGeoModel model, RenderType type,
                               MultiBufferSource buffer, VertexConsumer builder, boolean isRenderer,
                               float partialTick, int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        this.currentBuffer = buffer;
        this.renderType = type;

        matrixStack.pushPose();


        super.actuallyRender(matrixStack, animatable, model, type, buffer, builder, isRenderer,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        matrixStack.popPose();
        if (this.renderArms) {
            this.renderArms = false;
        }
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack matrixStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        this.transformType = transformType;
        super.renderByItem(stack, transformType, matrixStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public void renderRecursively(PoseStack stack, T animatable, GeoBone bone, RenderType type,
                                  MultiBufferSource buffer, VertexConsumer builder, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        String boneName = bone.getName();

        boolean isArmBone = boneName.equals("left_arm") || boneName.equals("right_arm");

        if (isArmBone) {
            bone.setHidden(true);
        } else {
            bone.setHidden(this.hiddenBones.contains(boneName));
        }

        if (this.transformType.firstPerson() && isArmBone && this.renderArms) {
            AbstractClientPlayer player = mc.player;
            if (player == null) {
                super.renderRecursively(stack, animatable, bone, type, buffer, builder, isReRender,
                        partialTick, packedLight, packedOverlay, red, green, blue, alpha);
                return;
            }

            float armsAlpha = player.isInvisible() ? 0.15f : 1.0f;
            PlayerRenderer playerRenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
            PlayerModel<AbstractClientPlayer> model = playerRenderer.getModel();

            stack.pushPose();

            RenderUtils.translateMatrixToBone(stack, bone);
            RenderUtils.translateToPivotPoint(stack, bone);
            RenderUtils.rotateMatrixAroundBone(stack, bone);
            RenderUtils.scaleMatrixForBone(stack, bone);
            RenderUtils.translateAwayFromPivotPoint(stack, bone);

            ResourceLocation skinTexture = player.getSkinTextureLocation();
            VertexConsumer armBuilder = buffer.getBuffer(RenderType.entitySolid(skinTexture));
            VertexConsumer sleeveBuilder = buffer.getBuffer(RenderType.entityTranslucent(skinTexture));

            if (boneName.equals("left_arm")) {
                stack.translate(-1.0f * SCALE_RECIPROCAL, 2.0f * SCALE_RECIPROCAL, 0.0f);
                renderPlayerArm(model.leftArm, bone, stack, armBuilder, packedLight, OverlayTexture.NO_OVERLAY, armsAlpha);
                renderPlayerArm(model.leftSleeve, bone, stack, sleeveBuilder, packedLight, OverlayTexture.NO_OVERLAY, armsAlpha);
            } else if (boneName.equals("right_arm")) {
                stack.translate(SCALE_RECIPROCAL, 2.0f * SCALE_RECIPROCAL, 0.0f);
                renderPlayerArm(model.rightArm, bone, stack, armBuilder, packedLight, OverlayTexture.NO_OVERLAY, armsAlpha);
                renderPlayerArm(model.rightSleeve, bone, stack, sleeveBuilder, packedLight, OverlayTexture.NO_OVERLAY, armsAlpha);
            }

            stack.popPose();
        }
        super.renderRecursively(stack, animatable, bone, type, buffer, builder, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    /**
     * Renders a player model part over a bone
     */
    private void renderPlayerArm(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer,
                                 int packedLight, int packedOverlay, float alpha) {
        renderPlayerArm(model, bone, stack, buffer, packedLight, packedOverlay, 1.0f, 1.0f, 1.0f, alpha);
    }

    /**
     * Renders a player model part over a bone with custom coloring
     */
    private void renderPlayerArm(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer,
                                 int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        setupModelFromBone(model, bone);
        stack.pushPose();
        stack.translate(0, -0.62, 0);
        model.render(stack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        stack.popPose();
    }

    /**
     * Sets up a model part using a bone's transforms
     */
    private void setupModelFromBone(ModelPart model, GeoBone bone) {
        model.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
        model.xRot = 0.0f;
        model.yRot = 0.0f;
        model.zRot = 0.0f;
    }
} 