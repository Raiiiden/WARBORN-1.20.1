package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.item.WarbornArmorItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ToggleHelmetTopPacket {
    private final boolean open;

    public ToggleHelmetTopPacket(boolean open) {
        this.open = open;
    }

    public ToggleHelmetTopPacket(FriendlyByteBuf buf) {
        this.open = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(open);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = ctx.get().getSender();
            if (player == null) return;

            var helmet = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
            if (helmet.getItem() instanceof WarbornArmorItem armorItem) {
                armorItem.setTopOpen(helmet, open); // You'll need this method in your item class
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
