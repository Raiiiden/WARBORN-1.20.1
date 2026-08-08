package com.raiiiden.warborn.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.Set;

public class WarbornFirstPersonHandRenderer<T extends Item & GeoItem> extends GeoItemRenderer<T> {
    private static final float SCALE_RECIPROCAL = 1f / 16f;
    private static final String LEFT_BONE = "left_hand";
    private static final String RIGHT_BONE = "right_hand";

    private final Set<String> handBones;
    @Nullable
    private final String firstPersonTag;

    protected ItemDisplayContext transformType;
    protected boolean renderArms = false;
    protected MultiBufferSource currentBuffer;
    protected RenderType renderType;
    protected T animatable;

    public WarbornFirstPersonHandRenderer(GeoModel<T> model, Set<String> handBones, @Nullable String firstPersonTag) {
        super(model);
        this.handBones = handBones;
        this.firstPersonTag = firstPersonTag;
    }

    @Override
    public RenderType getRenderType(T anim, ResourceLocation tex, MultiBufferSource buf, float pt) {
        return RenderType.entityTranslucent(getTextureLocation(anim));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose,
                             MultiBufferSource buf, int light, int overlay) {
        this.transformType = ctx;

        if (ctx.firstPerson()) {
            this.renderArms = true;
            if (firstPersonTag != null && (stack.getTag() == null || !stack.getTag().contains(firstPersonTag))) {
                stack.getOrCreateTag().putBoolean(firstPersonTag, true);
            }
        }

        super.renderByItem(stack, ctx, pose, buf, light, overlay);
    }

    @Override
    public void actuallyRender(PoseStack pose, T anim, BakedGeoModel model,
                               RenderType type, MultiBufferSource buf, VertexConsumer vc,
                               boolean isRenderer, float pt, int light, int overlay,
                               float r, float g, float b, float a) {
        this.currentBuffer = buf;
        this.renderType = type;
        this.animatable = anim;

        super.actuallyRender(pose, anim, model, type, buf, vc, isRenderer, pt, light, overlay, r, g, b, a);

        this.renderArms = true;
    }

    @Override
    public void renderRecursively(PoseStack stack, T anim, GeoBone bone,
                                  RenderType type, MultiBufferSource buf, VertexConsumer vc,
                                  boolean isReRender, float pt, int light, int overlay,
                                  float r, float g, float b, float a) {
        String name = bone.getName();
        boolean isArmProxy = handBones.contains(name);

        bone.setHidden(isArmProxy);

        if (this.transformType != null && this.transformType.firstPerson() && isArmProxy && renderArms) {
            renderArmOverBone(stack, bone, name, light);
        }

        super.renderRecursively(stack, anim, bone, type, buf, vc, isReRender, pt, light, overlay, r, g, b, a);
    }

    private void renderArmOverBone(PoseStack stack, GeoBone bone, String boneName, int light) {
        Minecraft mc = Minecraft.getInstance();
        AbstractClientPlayer player = mc.player;
        if (player == null) {
            return;
        }

        PlayerRenderer playerRenderer = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
        PlayerModel<AbstractClientPlayer> playerModel = playerRenderer.getModel();
        float alpha = player.isInvisible() ? 0.15f : 1f;

        stack.pushPose();
        RenderUtils.translateMatrixToBone(stack, bone);
        RenderUtils.translateToPivotPoint(stack, bone);
        RenderUtils.rotateMatrixAroundBone(stack, bone);
        RenderUtils.scaleMatrixForBone(stack, bone);
        RenderUtils.translateAwayFromPivotPoint(stack, bone);

        ResourceLocation skin = player.getSkinTextureLocation();
        VertexConsumer armBuffer = currentBuffer.getBuffer(RenderType.entitySolid(skin));
        VertexConsumer sleeveBuffer = currentBuffer.getBuffer(RenderType.entityTranslucent(skin));

        if (LEFT_BONE.equals(boneName)) {
            stack.translate(-SCALE_RECIPROCAL, 2 * SCALE_RECIPROCAL, 0);
            renderPartOverBone(playerModel.leftArm, bone, stack, armBuffer, light, OverlayTexture.NO_OVERLAY, alpha);
            renderPartOverBone(playerModel.leftSleeve, bone, stack, sleeveBuffer, light, OverlayTexture.NO_OVERLAY, alpha);
        } else if (RIGHT_BONE.equals(boneName)) {
            stack.translate(SCALE_RECIPROCAL, 2 * SCALE_RECIPROCAL, 0);
            renderPartOverBone(playerModel.rightArm, bone, stack, armBuffer, light, OverlayTexture.NO_OVERLAY, alpha);
            renderPartOverBone(playerModel.rightSleeve, bone, stack, sleeveBuffer, light, OverlayTexture.NO_OVERLAY, alpha);
        }

        currentBuffer.getBuffer(this.renderType);
        stack.popPose();
    }

    protected static void renderPartOverBone(ModelPart model, GeoBone bone, PoseStack stack,
                                             VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float alpha) {
        model.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
        model.xRot = 0.0f;
        model.yRot = 0.0f;
        model.zRot = 0.0f;
        model.render(stack, buffer, packedLightIn, packedOverlayIn, 1.0f, 1.0f, 1.0f, alpha);
    }
}
