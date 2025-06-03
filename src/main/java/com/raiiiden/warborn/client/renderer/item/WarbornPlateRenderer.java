package com.raiiiden.warborn.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.model.WarbornPlateModel;
import com.raiiiden.warborn.common.item.ArmorPlateItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.util.RenderUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * First-person plate-insertion renderer that matches the "new" gun-renderer pattern.
 */

// TODO??? Add abstract class for this or make it extendable for future use? also smd
public class WarbornPlateRenderer extends GeoItemRenderer<ArmorPlateItem> {

    private static final float SCALE_RECIPROCAL = 1f / 16f;
    private static final String LEFT_BONE = "left_hand";
    private static final String RIGHT_BONE = "right_hand";
    private static final String NBT_INSERT = "inserting";
    private final Set<String> hiddenBones = new HashSet<>();
    public ItemDisplayContext transformType;
    protected boolean renderArms = false;
    protected MultiBufferSource currentBuffer;
    protected RenderType renderType;
    protected ArmorPlateItem animatable;

    public WarbornPlateRenderer() {
        super(new WarbornPlateModel());
    }

    public static void renderPartOverBone(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float alpha) {
        renderPartOverBone(model, bone, stack, buffer, packedLightIn, packedOverlayIn, 1.0f, 1.0f, 1.0f, alpha);
    }

    public static void renderPartOverBone(ModelPart model, GeoBone bone, PoseStack stack, VertexConsumer buffer, int packedLightIn, int packedOverlayIn, float r, float g, float b, float a) {
        setupModelFromBone(model, bone);
        model.render(stack, buffer, packedLightIn, packedOverlayIn, r, g, b, a);
    }

    public static void setupModelFromBone(ModelPart model, GeoBone bone) {
        model.setPos(bone.getPivotX(), bone.getPivotY(), bone.getPivotZ());
        model.xRot = 0.0f;
        model.yRot = 0.0f;
        model.zRot = 0.0f;
    }

    @Override
    public RenderType getRenderType(ArmorPlateItem anim, ResourceLocation tex,
                                    MultiBufferSource buf, float pt) {
        return RenderType.entityTranslucent(getTextureLocation(anim));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx, PoseStack pose,
                             MultiBufferSource buf, int light, int overlay) {
        this.transformType = ctx;

        if (ctx.firstPerson()) {
            this.renderArms = true;

            if (stack.getTag() == null || !stack.getTag().contains(NBT_INSERT)) {
                stack.getOrCreateTag().putBoolean(NBT_INSERT, true);
            }

        }

        super.renderByItem(stack, ctx, pose, buf, light, overlay);
    }

    @Override
    public void actuallyRender(PoseStack pose, ArmorPlateItem anim, BakedGeoModel model,
                               RenderType type, MultiBufferSource buf, VertexConsumer vc,
                               boolean isRenderer, float pt, int light, int overlay,
                               float r, float g, float b, float a) {
        this.currentBuffer = buf;
        this.renderType = type;
        this.animatable = anim;

        super.actuallyRender(pose, anim, model, type, buf, vc,
                isRenderer, pt, light, overlay, r, g, b, a);

        this.renderArms = true;
    }

    @Override
    public void renderRecursively(PoseStack stack, ArmorPlateItem anim, GeoBone bone,
                                  RenderType type, MultiBufferSource buf, VertexConsumer vc,
                                  boolean isReRender, float pt, int light, int overlay,
                                  float r, float g, float b, float a) {

        String name = bone.getName();
        boolean isArmProxy = name.equals(LEFT_BONE) || name.equals(RIGHT_BONE);

        bone.setHidden(isArmProxy || hiddenBones.contains(name));

        if (this.transformType != null && this.transformType.firstPerson() && isArmProxy && renderArms) {
            Minecraft mc = Minecraft.getInstance();
            AbstractClientPlayer player = mc.player;
            if (player != null) {
                PlayerRenderer pr = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(player);
                PlayerModel<AbstractClientPlayer> model = pr.getModel();

                float alpha = player.isInvisible() ? 0.15f : 1f;

                stack.pushPose();
                RenderUtils.translateMatrixToBone(stack, bone);
                RenderUtils.translateToPivotPoint(stack, bone);
                RenderUtils.rotateMatrixAroundBone(stack, bone);
                RenderUtils.scaleMatrixForBone(stack, bone);
                RenderUtils.translateAwayFromPivotPoint(stack, bone);

                ResourceLocation skin = player.getSkinTextureLocation();
                VertexConsumer armVC = currentBuffer.getBuffer(RenderType.entitySolid(skin));
                VertexConsumer sleeveVC = currentBuffer.getBuffer(RenderType.entityTranslucent(skin));

                if (name.equals(LEFT_BONE)) {
                    stack.translate(-SCALE_RECIPROCAL, 2 * SCALE_RECIPROCAL, 0);
                    renderPartOverBone(model.leftArm, bone, stack, armVC, light, OverlayTexture.NO_OVERLAY, alpha);
                    renderPartOverBone(model.leftSleeve, bone, stack, sleeveVC, light, OverlayTexture.NO_OVERLAY, alpha);
                } else {
                    stack.translate(SCALE_RECIPROCAL, 2 * SCALE_RECIPROCAL, 0);
                    renderPartOverBone(model.rightArm, bone, stack, armVC, light, OverlayTexture.NO_OVERLAY, alpha);
                    renderPartOverBone(model.rightSleeve, bone, stack, sleeveVC, light, OverlayTexture.NO_OVERLAY, alpha);
                }

                currentBuffer.getBuffer(this.renderType);
                stack.popPose();
            }
        }

        super.renderRecursively(stack, anim, bone, type, buf, vc,
                isReRender, pt, light, overlay, r, g, b, a);
    }

    @Override
    public ResourceLocation getTextureLocation(ArmorPlateItem inst) {
        return super.getTextureLocation(inst);
    }
}