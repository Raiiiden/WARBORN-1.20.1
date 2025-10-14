package com.raiiiden.warborn.client.gui;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;

public class PlateHudOverlay {

    private static final ResourceLocation PLATE_ICON = new ResourceLocation(WARBORN.MODID, "textures/gui/plate_icon.png");
    private static final Color BAR_BACKGROUND = new Color(26, 26, 42);

    public static void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || player.isCreative() || player.isSpectator()) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty()) return;

        chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            int iconX = screenWidth / 2 - 110;
            int iconY = screenHeight - 20;
            guiGraphics.blit(PLATE_ICON, iconX, iconY, 0, 0, 18, 18, 18, 18);

            int barWidth = 36;
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
                drawPlateBar(guiGraphics, leftBarX, barY, plate.getCurrentDurability(), plate.getMaxDurability(), plate.getMaterial().getColor());
            }

            if (cap.hasBackPlate()) {
                Plate plate = cap.getBackPlate();
                drawPlateBar(guiGraphics, rightBarX, barY, plate.getCurrentDurability(), plate.getMaxDurability(), plate.getMaterial().getColor());
            }
        });
    }

    private static void drawPlateBar(GuiGraphics guiGraphics, int x, int y, float current, float max, Color color) {
        int width = 36;
        int height = 2;
        int filled = (int) ((current / max) * width);
        guiGraphics.fill(x, y, x + width, y + height, BAR_BACKGROUND.getARGB());
        guiGraphics.fill(x, y, x + filled, y + height, color.getARGB());
    }
}
