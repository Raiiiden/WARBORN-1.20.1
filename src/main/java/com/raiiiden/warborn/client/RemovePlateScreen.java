package com.raiiiden.warborn.client;

import com.raiiiden.warborn.common.object.capability.PlateHolderImpl;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.item.ArmorPlateItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class RemovePlateScreen extends Screen {
    public RemovePlateScreen() {
        super(Component.literal("Remove Plates"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(Component.literal("Remove Front Plate"), btn -> tryRemove(true))
                .bounds(centerX - 75, centerY - 20, 150, 20)
                .build());

        this.addRenderableWidget(Button.builder(Component.literal("Remove Back Plate"), btn -> tryRemove(false))
                .bounds(centerX - 75, centerY + 10, 150, 20)
                .build());
    }

    private void tryRemove(boolean front) {
        Player player = this.minecraft.player;
        if (player == null) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty()) return;

        chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            if (cap instanceof PlateHolderImpl impl) {
                if (front && impl.hasFrontPlate()) {
                    int hits = impl.getFrontDurability();
                    impl.removeFrontPlate();
                    player.getInventory().placeItemBackInInventory(ArmorPlateItem.createPlateWithHitsRemaining(hits));
                    player.displayClientMessage(Component.literal("Removed front plate."), true);
                } else if (!front && impl.hasBackPlate()) {
                    int hits = impl.getBackDurability();
                    impl.removeBackPlate();
                    player.getInventory().placeItemBackInInventory(ArmorPlateItem.createPlateWithHitsRemaining(hits));
                    player.displayClientMessage(Component.literal("Removed back plate."), true);
                }

                // Force chestplate NBT to update and sync
                chest.setTag(chest.getOrCreateTag());
                player.setItemSlot(EquipmentSlot.CHEST, chest);
            }
        });
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.drawCenteredString(this.font, "Plate Removal", this.width / 2, this.height / 2 - 40, 0xFFFFFF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
