package com.raiiiden.warborn.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raiiiden.warborn.common.item.BackpackItem;
import com.raiiiden.warborn.common.item.BackpackTooltipData;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class BackpackTooltipComponent implements ClientTooltipComponent {

    private static final int SLOT_SIZE = 18;
    private static final int COLUMNS   = 9;
    private static final int PAD       = 4;

    private final List<ItemStack> items;
    private final int rows;

    public BackpackTooltipComponent(BackpackTooltipData data) {
        this.items = data.items();
        int totalSlots = BackpackItem.getSlotsForTier(data.tier());
        this.rows = totalSlots / COLUMNS;
    }

    @Override
    public int getWidth(Font font) {
        return PAD * 2 + COLUMNS * SLOT_SIZE;
    }

    @Override
    public int getHeight() {
        return PAD * 2 + rows * SLOT_SIZE;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics g) {
        int startX = x + PAD;
        int startY = y + PAD;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                int slotX = startX + c * SLOT_SIZE;
                int slotY = startY + r * SLOT_SIZE;
                int idx   = r * COLUMNS + c;
                ItemStack stack = idx < items.size() ? items.get(idx) : ItemStack.EMPTY;
                renderSlot(g, font, slotX, slotY, stack);
            }
        }
    }

    private static void renderSlot(GuiGraphics g, Font font, int x, int y, ItemStack stack) {
        int s = SLOT_SIZE;
        g.fill(x,         y,         x + s,     y + s,     0xFF404040);
        g.fill(x,         y,         x + s,     y + 1,     0xFF888888);
        g.fill(x,         y,         x + 1,     y + s,     0xFF888888);
        g.fill(x,         y + s - 1, x + s,     y + s,     0xFF1A1A1A);
        g.fill(x + s - 1, y,         x + s,     y + s,     0xFF1A1A1A);

        if (!stack.isEmpty()) {
            RenderSystem.enableDepthTest();
            g.renderItem(stack, x + 1, y + 1);
            g.renderItemDecorations(font, stack, x + 1, y + 1);
        }
    }
}
