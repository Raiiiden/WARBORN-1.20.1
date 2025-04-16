package com.raiiiden.warborn.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.raiiiden.warborn.client.model.WarbornPlateModel;
import com.raiiiden.warborn.common.item.WarbornPlateItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

/**
 * Renderer for the Warborn Plate item with player arm integration
 */
public class WarbornPlateRenderer extends GeoItemRenderer<WarbornPlateItem> {
    private static final float SCALE_RECIPROCAL = 1.0f / 16.0f;

    private static final String LEFT_ARM_BONE = "left_arm";
    private static final String RIGHT_ARM_BONE = "right_arm";

    private boolean renderArms = false;
    private ItemDisplayContext currentTransform;

    public WarbornPlateRenderer() {
        super(new WarbornPlateModel());
    }

    /**
     * Enable arm rendering for the next render pass
     */
    public void enableArmRendering() {
        this.renderArms = true;
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        this.currentTransform = transformType;

        if (transformType.firstPerson() && stack.getOrCreateTag().getBoolean("inserting")) {
            this.renderArms = true;
        }

        super.renderByItem(stack, transformType, poseStack, buffer, packedLight, packedOverlay);
        this.renderArms = false;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, WarbornPlateItem animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource,
                                  VertexConsumer buffer, boolean isReRender, float partialTick,
                                  int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        boolean isArmBone = bone.getName().equals(LEFT_ARM_BONE) || bone.getName().equals(RIGHT_ARM_BONE);

        if (isArmBone) {
            bone.setHidden(true);
            if (this.currentTransform.firstPerson() && this.renderArms) {
                renderPlayerArm(bone, poseStack, bufferSource, packedLight);
            }
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    /**
     * Renders the player's arm at the position of a bone
     */
    private void renderPlayerArm(GeoBone bone, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer player = mc.player;

        if (player == null) return;

        PlayerRenderer playerRenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
        PlayerModel<AbstractClientPlayer> playerModel = playerRenderer.getModel();

        boolean isLeftArm = bone.getName().equals(LEFT_ARM_BONE);
        HumanoidArm arm = isLeftArm ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
        ModelPart armPart = isLeftArm ? playerModel.leftArm : playerModel.rightArm;
        ModelPart sleevePart = isLeftArm ? playerModel.leftSleeve : playerModel.rightSleeve;

        ResourceLocation skinTexture = player.getSkinTextureLocation();
        VertexConsumer armBuilder = bufferSource.getBuffer(RenderType.entitySolid(skinTexture));
        VertexConsumer sleeveBuilder = bufferSource.getBuffer(RenderType.entityTranslucent(skinTexture));

        float armAlpha = player.isInvisible() ? 0.15f : 1.0f;

        poseStack.pushPose();

        RenderUtils.translateMatrixToBone(poseStack, bone);
        RenderUtils.translateToPivotPoint(poseStack, bone);
        RenderUtils.rotateMatrixAroundBone(poseStack, bone);
        RenderUtils.scaleMatrixForBone(poseStack, bone);
        RenderUtils.translateAwayFromPivotPoint(poseStack, bone);

        if (isLeftArm) {
            poseStack.translate(-1.0f * SCALE_RECIPROCAL, 2.0f * SCALE_RECIPROCAL, 0.0f);
        } else {
            poseStack.translate(SCALE_RECIPROCAL, 2.0f * SCALE_RECIPROCAL, 0.0f);
        }

        setupModelPart(armPart, bone);
        poseStack.pushPose();
        poseStack.translate(0, -0.62, 0);
        armPart.render(poseStack, armBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, armAlpha);
        sleevePart.render(poseStack, sleeveBuilder, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, armAlpha);
        poseStack.popPose();

        poseStack.popPose();
    }

    /**
     * Sets up a model part using a bone's position
     */
    private void setupModelPart(ModelPart modelPart, GeoBone bone) {
        modelPart.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
        modelPart.xRot = 0.0f;
        modelPart.yRot = 0.0f;
        modelPart.zRot = 0.0f;
    }
} 