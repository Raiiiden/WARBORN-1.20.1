package com.raiiiden.warborn.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.config.WarbornCommonConfig;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, value = Dist.CLIENT)
public class FaceplateOverlay {
    private static final ResourceLocation FACEPLATE_TEXTURE =
            new ResourceLocation(WARBORN.MODID, "textures/overlay/faceplate_overlay2.png");

    private static final TagKey<Item> FACEPLATE_TAG =
            TagKey.create(Registries.ITEM, new ResourceLocation(WARBORN.MODID, "has_faceplate"));

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        if (!WarbornCommonConfig.ENABLE_FACEPLATE_OVERLAY.get()) {
            return;
        }

        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HELMET.id())) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || !mc.options.getCameraType().isFirstPerson()) {
            return;
        }

        Player player = mc.player;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty() || !helmet.is(FACEPLATE_TAG)) {
            return;
        }

        if (helmet.getItem() instanceof WBArmorItem WBArmorItem) {
            if (WBArmorItem.isTopOpen(helmet)) {
                return; // Helmet top open → don't render overlay
            }
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        guiGraphics.blit(FACEPLATE_TEXTURE, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
        RenderSystem.disableBlend();
    }
} 