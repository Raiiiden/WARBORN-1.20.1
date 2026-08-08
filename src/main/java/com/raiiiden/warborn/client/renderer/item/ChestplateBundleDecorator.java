package com.raiiiden.warborn.client.renderer.item;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.raiiiden.warborn.common.object.capability.ChestplateBundleHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

// Draws a bundle-capacity bar above the durability bar when a chestplate holds items; returns false so vanilla still draws below it.
public class ChestplateBundleDecorator implements IItemDecorator {

    private static final int BAR_COLOR = 0x6666FF;
    private static final int MAX = ChestplateBundleHandler.MAX_SLOTS * ChestplateBundleHandler.MAX_STACK_SIZE;

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (!WBArmorItem.isChestplateItem(stack)) return false;

        int total = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
            int sum = 0;
            for (int i = 0; i < handler.getSlots(); i++) {
                sum += handler.getStackInSlot(i).getCount();
            }
            return sum;
        }).orElse(0);

        if (total <= 0) return false; // nothing stored, skip

        int barWidth = Math.max(1, 1 + (int) (12f * ((float) total / MAX)));
        barWidth = Math.min(13, barWidth);

        // Background bar (black), above vanilla bar
        guiGraphics.fill(RenderType.guiOverlay(),
                xOffset + 2, yOffset + 11, xOffset + 15, yOffset + 13, 0xFF000000);
        // Blue fill
        guiGraphics.fill(RenderType.guiOverlay(),
                xOffset + 2, yOffset + 11, xOffset + 2 + barWidth, yOffset + 12,
                0xFF000000 | BAR_COLOR);

        return false; // do not suppress the vanilla durability bar
    }
}
