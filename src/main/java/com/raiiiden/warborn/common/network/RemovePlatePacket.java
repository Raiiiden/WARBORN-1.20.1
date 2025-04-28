package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.item.ArmorPlateItem;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class RemovePlatePacket {
    private static final Logger LOGGER = LogManager.getLogger();
    private final boolean front;

    public RemovePlatePacket(boolean front) {
        this.front = front;
    }

    public RemovePlatePacket(FriendlyByteBuf buf) {
        this.front = buf.readBoolean();
    }

    public static void handle(RemovePlatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chest.isEmpty()) return;

            chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
                if (packet.front && cap.hasFrontPlate()) {
                    Plate frontPlate = cap.getFrontPlate();
                    if (frontPlate != null) {
                        int durability = (int) frontPlate.getCurrentDurability();
                        ProtectionTier tier = frontPlate.getTier();
                        MaterialType material = frontPlate.getMaterial();

                        cap.removeFrontPlate();
                        player.getInventory().placeItemBackInInventory(
                                ArmorPlateItem.createPlateWithHitsRemaining(tier, material, durability));
                        player.displayClientMessage(Component.literal("Removed front plate."), true);
                        LOGGER.info("Server removed front plate: {} {}, {} durability remaining.",
                                tier, material, durability);
                    }
                } else if (!packet.front && cap.hasBackPlate()) {
                    Plate backPlate = cap.getBackPlate();
                    if (backPlate != null) {
                        int durability = (int) backPlate.getCurrentDurability();
                        ProtectionTier tier = backPlate.getTier();
                        MaterialType material = backPlate.getMaterial();

                        cap.removeBackPlate();
                        player.getInventory().placeItemBackInInventory(
                                ArmorPlateItem.createPlateWithHitsRemaining(tier, material, durability));
                        player.displayClientMessage(Component.literal("Removed back plate."), true);
                        LOGGER.info("Server removed back plate: {} {}, {} durability remaining.",
                                tier, material, durability);
                    }
                }

                // Force NBT update and sync
                chest.setTag(chest.getOrCreateTag());
            });
        });

        ctx.get().setPacketHandled(true);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.front);
    }
}