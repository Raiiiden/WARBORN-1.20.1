package com.raiiiden.warborn.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.raiiiden.warborn.common.item.HelmetSlotTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

// Renders exactly 2 fixed slots for the helmet goggle + battery; vanilla BundleTooltip pads out to a grid.
public class HelmetSlotTooltipComponent implements ClientTooltipComponent {

    // Each slot is SLOT_SIZE×SLOT_SIZE; item renders at a 2px inset (16×16 item fits inside).
    private static final int SLOT_SIZE = 20;
    private static final int SLOT_GAP  = 4;
    private static final int PAD       = 4;

    private final ItemStack goggles;
    private final ItemStack battery;

    public HelmetSlotTooltipComponent(HelmetSlotTooltip data) {
        this.goggles = data.goggles();
        this.battery = data.battery();
    }

    @Override
    public int getWidth(Font font) {
        // PAD + slot + GAP + slot + PAD
        return PAD * 2 + SLOT_SIZE * 2 + SLOT_GAP;
    }

    @Override
    public int getHeight() {
        // PAD + slot + PAD
        return PAD * 2 + SLOT_SIZE;
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        int slotY = y + PAD;
        renderSlot(guiGraphics, font, x + PAD, slotY, battery);
        renderSlot(guiGraphics, font, x + PAD + SLOT_SIZE + SLOT_GAP, slotY, goggles);
    }

    private static void renderSlot(GuiGraphics g, Font font, int x, int y, ItemStack stack) {
        int s = SLOT_SIZE;
        // Background
        g.fill(x, y, x + s, y + s, 0xFF404040);
        // Bevelled border — lighter top/left, darker bottom/right (inventory slot look)
        g.fill(x,         y,         x + s,     y + 1,     0xFF888888); // top
        g.fill(x,         y,         x + 1,     y + s,     0xFF888888); // left
        g.fill(x,         y + s - 1, x + s,     y + s,     0xFF1A1A1A); // bottom
        g.fill(x + s - 1, y,         x + s,     y + s,     0xFF1A1A1A); // right

        if (!stack.isEmpty()) {
            // Items are 16×16; centre them inside the 18×18 inner area (2px inset from border)
            RenderSystem.enableDepthTest();
            g.renderItem(stack, x + 2, y + 2);
            g.renderItemDecorations(font, stack, x + 2, y + 2);
        }
    }
}
