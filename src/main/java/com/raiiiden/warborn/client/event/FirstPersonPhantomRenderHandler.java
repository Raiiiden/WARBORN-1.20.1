package com.raiiiden.warborn.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.renderer.PhantomNVGRenderManager;
import com.raiiiden.warborn.client.renderer.PhantomPlateRenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FirstPersonPhantomRenderHandler {

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        ItemStack phantomStack = getPhantomStack(event.getHand());
        if (phantomStack.isEmpty()) {
            return;
        }

        event.setCanceled(true);

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        int light = event.getPackedLight();

        poseStack.pushPose();

        if (event.getHand() == InteractionHand.MAIN_HAND) {
            poseStack.translate(0.56, -0.52, -0.72);
        } else {
            poseStack.translate(-0.56, -0.52, -0.72);
        }

        mc.getItemRenderer().renderStatic(
                player,
                phantomStack,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                event.getHand() == InteractionHand.OFF_HAND,
                poseStack,
                bufferSource,
                player.level(),
                light,
                OverlayTexture.NO_OVERLAY,
                player.getId()
        );

        poseStack.popPose();
    }

    private static ItemStack getPhantomStack(InteractionHand hand) {
        PhantomPlateRenderManager plateManager = PhantomPlateRenderManager.getInstance();
        if (plateManager.shouldRenderPhantom(hand)) {
            return plateManager.getPhantomStack();
        }

        PhantomNVGRenderManager nvgManager = PhantomNVGRenderManager.getInstance();
        if (nvgManager.shouldRenderPhantom(hand)) {
            return nvgManager.getPhantomStack();
        }

        return ItemStack.EMPTY;
    }
}
