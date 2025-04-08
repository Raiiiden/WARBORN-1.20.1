package com.raiiiden.warborn.client.gui;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
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

    private static final int MAX_DURABILITY = 10;
    private static final ResourceLocation PLATE_ICON = new ResourceLocation(WARBORN.MODID, "textures/gui/plate_icon.png");
    private static final int BAR_COLOR = 0xFF007BFF;
    private static final int BAR_BACKGROUND = 0xFF1A1A2A;
    //TODO adapt to new system

    @SubscribeEvent
    public static void renderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

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

            // Dynamic height logic
            int absorption = player.getAbsorptionAmount() > 0 ? 1 : 0;
            int armor = player.getArmorValue() > 0 ? 1 : 0;
            int baseY = screenHeight - 49;
            int offset = 9 * (absorption + armor); // each bar pushes height up
            int barY = baseY - offset;

            int leftBarX = centerX - barWidth - spacing - 50;
            int rightBarX = centerX + spacing - 50;

            long time = System.currentTimeMillis();

            if (cap.hasFrontPlate()) {
                drawPlateBar(gui, leftBarX, barY, cap.getFrontDurability(), BAR_COLOR, time);
            }

            if (cap.hasBackPlate()) {
                drawPlateBar(gui, rightBarX, barY, cap.getBackDurability(), BAR_COLOR, time);
            }
        });
    }

    private static void drawPlateBar(GuiGraphics gui, int x, int y, int durability, int fillColor, long timeMillis) {
        int width = 36;
        int height = 2;
        float ratio = durability / (float) MAX_DURABILITY;
        int filled = (int) (ratio * width);

        gui.fill(x, y, x + width, y + height, BAR_BACKGROUND); // always draw background

        boolean shouldBlink = durability <= 2 && (timeMillis / 300) % 2 == 0;
        if (!shouldBlink) {
            gui.fill(x, y, x + filled, y + height, fillColor); // draw fill only if not blinking
        }
    }
}
