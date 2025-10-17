package com.raiiiden.warborn.client.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.renderer.PhantomPlateRenderManager;
import com.raiiiden.warborn.common.item.ArmorPlateItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Intercepts first-person hand rendering to inject phantom plate rendering
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FirstPersonPhantomRenderHandler {

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        PhantomPlateRenderManager manager = PhantomPlateRenderManager.getInstance();

        if (!manager.shouldRenderPhantom(event.getHand())) {
            return;
        }

        ItemStack phantomStack = manager.getPhantomStack();
        if (phantomStack.isEmpty()) {
            return;
        }

        // Cancel the normal hand rendering
        event.setCanceled(true);

        // Render the phantom plate instead
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();
        int light = event.getPackedLight();

        poseStack.pushPose();

        // Apply first-person transforms
        if (event.getHand() == InteractionHand.MAIN_HAND) {
            // Main hand positioning
            poseStack.translate(0.56, -0.52, -0.72);
        } else {
            // Off hand positioning
            poseStack.translate(-0.56, -0.52, -0.72);
        }

        // Render the phantom plate item
        mc.getItemRenderer().renderStatic(
                player,
                phantomStack,
                ItemDisplayContext.FIRST_PERSON_RIGHT_HAND,
                event.getHand() == InteractionHand.OFF_HAND,
                poseStack,
                bufferSource,
                player.level(),
                light,
                net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                player.getId()
        );

        poseStack.popPose();
    }
}