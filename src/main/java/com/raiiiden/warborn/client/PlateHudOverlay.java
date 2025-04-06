package com.raiiiden.warborn.client;

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
            int y = mc.getWindow().getGuiScaledHeight() - 50;

            if (cap.hasFrontPlate()) {
                drawDurabilityBar(gui, x, y, cap.getFrontDurability(), MAX_DURABILITY);
            }

            if (cap.hasBackPlate()) {
                drawDurabilityBar(gui, x, y + 6, cap.getBackDurability(), MAX_DURABILITY);
            }
        });
    }

    private static void drawDurabilityBar(GuiGraphics gui, int x, int y, int durability, int maxDurability) {
        int width = 60;
        int filled = (int)((durability / (float) maxDurability) * width);

        gui.fill(x, y, x + width, y + 5, 0xFF222222); // background bar
        gui.fill(x, y, x + filled, y + 5, 0xFF00FF00); // filled portion
    }
}
