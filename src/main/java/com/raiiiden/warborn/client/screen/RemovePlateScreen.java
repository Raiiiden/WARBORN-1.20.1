package com.raiiiden.warborn.client.screen;

import com.raiiiden.warborn.common.item.ArmorPlateItem;
import com.raiiiden.warborn.common.network.ModNetworking;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.animatable.GeoItem;

public class RemovePlateScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger();

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
        if (chest.isEmpty()) {
            player.displayClientMessage(Component.literal("You must wear a chestplate."), true);
            return;
        }

        boolean canRemove = false;
        var capOpt = chest.getCapability(PlateHolderProvider.CAP);
        if (capOpt.isPresent()) {
            var cap = capOpt.orElse(null);
            if (cap != null) {
                if (front && cap.hasFrontPlate()) {
                    canRemove = true;
                } else if (!front && cap.hasBackPlate()) {
                    canRemove = true;
                }
            }
        }

        if (canRemove) {
            LOGGER.info("Sending remove plate packet to server: {}", front ? "front" : "back");
            ModNetworking.sendRemovePlatePacket(front);

            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();

            if (main.getItem() instanceof ArmorPlateItem plateItem) {
                ItemStack held = main;
                CompoundTag tag = held.getOrCreateTag();

                tag.putBoolean("warborn_pending_remove", true);
                tag.putInt("warborn_remove_delay", 62);

                long id = GeoItem.getId(held);
                plateItem.triggerAnim(player, id, ArmorPlateItem.CONTROLLER, "remove");
            } else if (off.getItem() instanceof ArmorPlateItem plateItem) {
                ItemStack held = off;
                CompoundTag tag = held.getOrCreateTag();

                tag.putBoolean("warborn_pending_remove", true);
                tag.putInt("warborn_remove_delay", 62);

                long id = GeoItem.getId(held);
                plateItem.triggerAnim(player, id, ArmorPlateItem.CONTROLLER, "remove");
            }

        } else {
            String plateType = front ? "front" : "back";
            player.displayClientMessage(Component.literal("No " + plateType + " plate to remove."), true);
        }
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