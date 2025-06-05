package com.raiiiden.warborn.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.config.WarbornCommonConfig;
import com.raiiiden.warborn.common.init.ModItemRegistry;
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
    private static final ResourceLocation FACEPLATE_DEFAULT =
            new ResourceLocation(WARBORN.MODID, "textures/overlay/beta.png");
    private static final ResourceLocation FACEPLATE_KILLA =
            new ResourceLocation(WARBORN.MODID, "textures/overlay/killas.png");
    private static final ResourceLocation FACEPLATE_TAGILLA =
            new ResourceLocation(WARBORN.MODID, "textures/overlay/tagillas.png");
    private static final ResourceLocation FACEPLATE_COMMANDERS =
            new ResourceLocation(WARBORN.MODID, "textures/overlay/commanders.png");

    private static final TagKey<Item> FACEPLATE_TAG =
            TagKey.create(Registries.ITEM, new ResourceLocation(WARBORN.MODID, "has_faceplate"));
    private static final TagKey<Item> BETA_7 =
            TagKey.create(Registries.ITEM, new ResourceLocation(WARBORN.MODID, "is_beta7"));

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onRenderOverlay(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !mc.options.getCameraType().isFirstPerson()) {
            return;
        }

        if (!WarbornCommonConfig.ENABLE_FACEPLATE_OVERLAY.get()) {
            return;
        }

        Player player = mc.player;
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HELMET.id())) {
            return;
        }


        if (helmet.isEmpty()) {
            return;
        }

        if (!(helmet.is(FACEPLATE_TAG) || helmet.is(BETA_7))) {
            return;
        }


        if (helmet.getItem() instanceof WBArmorItem WBArmorItem) {
            if (WBArmorItem.isTopOpen(helmet) && !helmet.is(BETA_7)) {
                return;
            }
        }
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        GuiGraphics guiGraphics = event.getGuiGraphics();
        if (helmet.getItem().equals(ModItemRegistry.KILLA_HELMET.get())) {
            guiGraphics.blit(FACEPLATE_KILLA, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
        } else if (helmet.getItem().equals(ModItemRegistry.TAGILLA_HELMET.get())) {
            guiGraphics.blit(FACEPLATE_TAGILLA, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
        } else if (helmet.getItem().equals(ModItemRegistry.INSURGENCY_COMMANDER_HELMET.get())) {
            guiGraphics.blit(FACEPLATE_COMMANDERS, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
        } else {
            guiGraphics.blit(FACEPLATE_DEFAULT, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
        }
        RenderSystem.disableBlend();
    }
} 