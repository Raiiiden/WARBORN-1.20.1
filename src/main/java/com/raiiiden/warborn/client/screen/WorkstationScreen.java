package com.raiiiden.warborn.client.screen;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.block.WorkstationBlock.WorkstationKind;
import com.raiiiden.warborn.common.menu.WorkstationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

// One screen for both stations; each kind has its own background texture but they share the overlay sprites right of the 176x166 background.
public class WorkstationScreen extends AbstractContainerScreen<WorkstationMenu> {
    private static final ResourceLocation PRESS_TEXTURE =
            new ResourceLocation(WARBORN.MODID, "textures/gui/industrial_press.png");
    private static final ResourceLocation BENCH_TEXTURE =
            new ResourceLocation(WARBORN.MODID, "textures/gui/ballistics_bench.png");

    private static final int FLAME_U = 176;
    private static final int FLAME_V = 0;
    private static final int FLAME_SIZE = 14;

    private static final int ARROW_U = 176;
    private static final int ARROW_V = 14;
    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_HEIGHT = 17;

    private final ResourceLocation texture;
    private final int arrowX;
    private final int arrowY;

    public WorkstationScreen(WorkstationMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        // Nudged down from vanilla's -94 so the label clears the recessed station panel above it.
        this.inventoryLabelY = this.imageHeight - 91;

        boolean press = menu.getKind() == WorkstationKind.INDUSTRIAL_PRESS;
        this.texture = press ? PRESS_TEXTURE : BENCH_TEXTURE;
        this.arrowX = press ? 84 : 88;
        this.arrowY = 34;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(texture, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.getKind() == WorkstationKind.INDUSTRIAL_PRESS && menu.isBurning()) {
            // Flames burn down, so the sprite is drawn from its base upwards.
            int height = menu.getFuelScaled(FLAME_SIZE);
            graphics.blit(texture, leftPos + 45, topPos + 40 + FLAME_SIZE - height,
                    FLAME_U, FLAME_V + FLAME_SIZE - height, FLAME_SIZE, height);
        }

        int progress = menu.getProgressScaled(ARROW_WIDTH);
        if (progress > 0) {
            graphics.blit(texture, leftPos + arrowX, topPos + arrowY, ARROW_U, ARROW_V, progress, ARROW_HEIGHT);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);

        if (isHovering(arrowX, arrowY, ARROW_WIDTH, ARROW_HEIGHT, mouseX, mouseY)) {
            int percent = menu.getProgressScaled(100);
            graphics.renderTooltip(font, Component.translatable("gui.fracturepoint.progress", percent), mouseX, mouseY);
        }

        renderTooltip(graphics, mouseX, mouseY);
    }
}
