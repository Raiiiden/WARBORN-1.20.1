package com.raiiiden.warborn.client.renderer.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.raiiiden.warborn.client.model.WarbornAttachmentModel;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

// Renders one bone out of a donor set as an attachment on a helmet that has no such bone itself.
public class WarbornGoggleMountRenderer extends GeoArmorRenderer<WBArmorItem> {

    // Keeps this renderer's animation state off the host helmet
    private static final long ATTACHMENT_SALT = 0x4E5647L;

    private final String mountBone;
    private final float offsetUp;
    private final float offsetBack;

    public WarbornGoggleMountRenderer(String assetName, String mountBone, float offsetUp, float offsetBack) {
        super(new WarbornAttachmentModel(assetName));
        this.mountBone = mountBone;
        this.offsetUp = offsetUp;
        this.offsetBack = offsetBack;
    }

    @Override
    public void renderRecursively(PoseStack poseStack, WBArmorItem animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        boolean isMountRoot = this.mountBone.equals(bone.getName());

        // Fitting the donor mount to a different helmet has to happen here, at the mount's own bone,
        // because by this point the stack already carries the head bone's rotation - so the offset
        // turns with the head. Applied any earlier it is a body-space nudge, and the mount shears off
        // the helmet the moment you look around. Bone space is plain Bedrock orientation: +Y up, +Z back.
        if (isMountRoot) {
            poseStack.pushPose();
            poseStack.translate(0, this.offsetUp / 16f, this.offsetBack / 16f);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        if (isMountRoot) poseStack.popPose();
    }

    // Every other bone still gets walked - cheaply, since only the mount's subtree owns any cubes to
    // draw - which keeps the donor's bone hierarchy intact for the animation to drive.
    @Override
    public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight,
                                  int packedOverlay, float red, float green, float blue, float alpha) {
        if (!isPartOfMount(bone)) return;

        super.renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private boolean isPartOfMount(GeoBone bone) {
        for (GeoBone current = bone; current != null; current = current.getParent()) {
            if (this.mountBone.equals(current.getName())) return true;
        }
        return false;
    }

    @Override
    public long getInstanceId(WBArmorItem animatable) {
        return super.getInstanceId(animatable) ^ ATTACHMENT_SALT;
    }
}
