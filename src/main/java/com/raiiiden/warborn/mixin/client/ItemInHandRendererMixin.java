package com.raiiiden.warborn.mixin.client;

import com.raiiiden.warborn.client.renderer.WBRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Inject(method = "renderArmWithItem", at = @At("HEAD"))
    private void warborn$allowFirstPersonArms(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack itemStack, float equipProgress, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        WBRenderState.IGNORE_HIDDEN = true; // <-- tell ModelPartMixin "do not cancel"
    }

    @Inject(method = "renderArmWithItem", at = @At("RETURN"))
    private void warborn$restoreHiddenArms(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack itemStack, float equipProgress, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        WBRenderState.IGNORE_HIDDEN = false; // <-- allow hiding again
    }
}
