package com.raiiiden.warborn.client.gui;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.Plate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, value = Dist.CLIENT)
public class PlateHudOverlay {

    private static final ResourceLocation PLATE_ICON = new ResourceLocation(WARBORN.MODID, "textures/gui/plate_icon.png");
    private static final int BAR_COLOR = 0xFF007BFF;
    private static final int BAR_BACKGROUND = 0xFF1A1A2A;

    @SubscribeEvent
    public static void renderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || player.isCreative()) return; // Skip if creative

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty()) return;

        chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            GuiGraphics gui = event.getGuiGraphics();
            int screenWidth = mc.getWindow().getGuiScaledWidth();
            int screenHeight = mc.getWindow().getGuiScaledHeight();

            // Plate icon (left of hotbar)
            int iconX = screenWidth / 2 - 110;
            int iconY = screenHeight - 20;
            gui.blit(PLATE_ICON, iconX, iconY, 0, 0, 18, 18, 18, 18);

            // Bars dynamically above health bar based on other overlays
            int barWidth = 36;
            int barHeight = 2;
            int spacing = 4;
            int centerX = screenWidth / 2;

            int absorption = player.getAbsorptionAmount() > 0 ? 1 : 0;
            int armor = player.getArmorValue() > 0 ? 1 : 0;
            int baseY = screenHeight - 45;
            int offset = 9 * (absorption + armor);
            int barY = baseY - offset;

            int leftBarX = centerX - barWidth - spacing - 50;
            int rightBarX = centerX + spacing - 50;

            if (cap.hasFrontPlate()) {
                Plate plate = cap.getFrontPlate();
                drawPlateBar(gui, leftBarX, barY, plate.getCurrentDurability(), plate.getMaxDurability(), BAR_COLOR);
            }

            if (cap.hasBackPlate()) {
                Plate plate = cap.getBackPlate();
                drawPlateBar(gui, rightBarX, barY, plate.getCurrentDurability(), plate.getMaxDurability(), BAR_COLOR);
            }
        });
    }

    private static void drawPlateBar(GuiGraphics gui, int x, int y, float currentDurability, float maxDurability, int fillColor) {
        int width = 36;
        int height = 2;
        int filled = (int) ((currentDurability / maxDurability) * width);

        gui.fill(x, y, x + width, y + height, BAR_BACKGROUND);  // background
        gui.fill(x, y, x + filled, y + height, fillColor);      // fill
    }
}
