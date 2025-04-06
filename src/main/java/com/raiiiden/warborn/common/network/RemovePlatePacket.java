package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.object.capability.PlateHolderImpl;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.item.ArmorPlateItem;
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

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.front);
    }

    public static void handle(RemovePlatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chest.isEmpty()) return;

            chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
                if (cap instanceof PlateHolderImpl impl) {
                    if (packet.front && impl.hasFrontPlate()) {
                        int hits = impl.getFrontDurability();
                        impl.removeFrontPlate();
                        player.getInventory().placeItemBackInInventory(ArmorPlateItem.createPlateWithHitsRemaining(hits));
                        player.displayClientMessage(Component.literal("Removed front plate."), true);
                        LOGGER.info("Server removed front plate with {} hits remaining.", hits);
                    } else if (!packet.front && impl.hasBackPlate()) {
                        int hits = impl.getBackDurability();
                        impl.removeBackPlate();
                        player.getInventory().placeItemBackInInventory(ArmorPlateItem.createPlateWithHitsRemaining(hits));
                        player.displayClientMessage(Component.literal("Removed back plate."), true);
                        LOGGER.info("Server removed back plate with {} hits remaining.", hits);
                    }

                    // Force NBT update and sync
                    chest.setTag(chest.getOrCreateTag());
                }
            });
        });

        ctx.get().setPacketHandled(true);
    }
}