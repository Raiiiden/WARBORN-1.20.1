package com.raiiiden.warborn.client.screen;

import com.raiiiden.warborn.common.network.BackpackScrollPacket;
import com.raiiiden.warborn.common.network.ModNetworking;
import com.raiiiden.warborn.common.object.BackpackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    private float smoothScroll = 0.0F;

    public BackpackScreen(BackpackMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth  = 176;
        this.imageHeight = 114 + menu.getVisibleRows() * 18;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(gfx);
        super.render(gfx, mouseX, mouseY, partialTick);
        this.renderTooltip(gfx, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int slotsTop    = topPos + 18;
        int slotsBottom = slotsTop + menu.getVisibleRows() * 18;

        if (mouseY < slotsTop || mouseY > slotsBottom)
            return super.mouseScrolled(mouseX, mouseY, delta);

        int newRow = menu.getScrollRow() - (int) Math.signum(delta);
        menu.setScrollRow(newRow);
        ModNetworking.sendToServer(new BackpackScrollPacket(menu.getScrollRow()));
        return true;
    }

    @Override
    protected void renderBg(GuiGraphics gfx, float partialTick, int mouseX, int mouseY) {
        int rows = menu.getVisibleRows();

        gfx.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, rows * 18 + 18);
        gfx.blit(TEXTURE, leftPos, topPos + rows * 18 + 18, 0, 126, imageWidth, 97);

        int max = menu.getMaxScrollRow();
        if (max > 0) {
            int barX      = leftPos + imageWidth - 7;
            int barY      = topPos + 18;
            int barHeight = rows * 18;

            smoothScroll += ((float) menu.getScrollRow() - smoothScroll) * 0.35F;

            float progress = menu.getScrollRow() == max
                    ? 1.0F
                    : smoothScroll / max;
            progress = Math.max(0.0F, Math.min(1.0F, progress));

            int knobY = barY + (int) ((barHeight - 15) * progress);
            gfx.fill(barX, barY,  barX + 7, barY + barHeight, 0xFF202020);
            gfx.fill(barX, knobY, barX + 7, knobY + 15,       0xFFAAAAAA);
        }
    }
}