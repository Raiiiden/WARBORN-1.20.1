package com.raiiiden.warborn.client;

import com.raiiiden.warborn.common.object.BackpackMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class BackpackScreen extends AbstractContainerScreen<BackpackMenu> {
    private static final ResourceLocation BACKPACK_TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/shulker_box.png");

    public BackpackScreen(BackpackMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;  // Total width for 9 slots + inventory (18px * 9)
        this.imageHeight = 222; // Total height for 3 rows of backpack slots + player's inventory
        this.inventoryLabelY = this.imageHeight - 150;  // Position of the inventory label
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render the background (backpack texture)
        this.renderBackground(guiGraphics);

        // Render the backpack grid (using custom slots)
        int slotSize = 18;  // Slot size
        int startX = (this.width - this.imageWidth) / 2 + 10;  // Start X position for grid
        int startY = (this.height - this.imageHeight) / 2 + 20;  // Start Y position for grid

        // Draw the 9x3 grid of slots (9 columns, 3 rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int x = startX + col * slotSize;
                int y = startY + row * slotSize;
                guiGraphics.fill(x, y, x + slotSize, y + slotSize, 0xFF555555);  // Slot border color
            }
        }

        // Render the player's inventory slots using default rendering
        super.render(guiGraphics, mouseX, mouseY, partialTick);  // Calls the parent method to render the inventory
        this.renderTooltip(guiGraphics, mouseX, mouseY);  // Renders the tooltips
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // Draw the background texture here (backpack texture)
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BACKPACK_TEXTURE, i, j, 0, 0, this.imageWidth, 180);  // Draw the backpack background
    }
}
