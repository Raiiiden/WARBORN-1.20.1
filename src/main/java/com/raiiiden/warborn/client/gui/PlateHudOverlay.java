package com.raiiiden.warborn.client.gui;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, value = Dist.CLIENT)
public class PlateHudOverlay {

    private static final int MAX_DURABILITY = 10;

    @SubscribeEvent
    public static void renderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty()) return;

        chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            GuiGraphics gui = event.getGuiGraphics();
            int x = mc.getWindow().getGuiScaledWidth() / 2 - 91;
            int yOffset = -20; // Shift the bar upward
            int y = mc.getWindow().getGuiScaledHeight() - 50 + yOffset;

            if (cap.hasFrontPlate()) {
                drawDurabilityBar(gui, x, y, cap.getFrontDurability(), MAX_DURABILITY, "Front");
            }

            if (cap.hasBackPlate()) {
                drawDurabilityBar(gui, x, y + 8, cap.getBackDurability(), MAX_DURABILITY, "Back");
            }
        });
    }

    private static void drawDurabilityBar(GuiGraphics gui, int x, int y, int durability, int maxDurability, String label) {
        float ratio = durability / (float) maxDurability;
        int width = 60;
        int filled = (int)(ratio * width);
        int color = getDurabilityColor(ratio);

        // Blink when critically low
        if (durability <= 2 && ((System.currentTimeMillis() / 300) % 2 == 0)) {
            return;
        }

        // Background
        gui.fill(x, y, x + width, y + 6, 0xFF222222);
        // Foreground
        gui.fill(x, y, x + filled, y + 6, color);
        // Text
        gui.drawString(Minecraft.getInstance().font, label + ": " + durability + "/" + maxDurability, x + width + 5, y, 0xFFFFFF);
    }

    private static int getDurabilityColor(float ratio) {
        if (ratio > 0.5f) return 0xFF00FF00;     // Green
        else if (ratio > 0.25f) return 0xFFFFA500; // Orange
        else return 0xFFFF0000;                  // Red
    }
}
