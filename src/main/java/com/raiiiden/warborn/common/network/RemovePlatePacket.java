package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.item.ArmorPlateItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RemovePlatePacket {
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
            if (player == null || !(player.level() instanceof ServerLevel serverLevel)) return;

            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();

            if (main.getItem() instanceof ArmorPlateItem plateItem) {
                plateItem.startRemoveAnimation(player, main, packet.front, serverLevel);
            } else if (off.getItem() instanceof ArmorPlateItem plateItem) {
                plateItem.startRemoveAnimation(player, off, packet.front, serverLevel);
            }
        });

        ctx.get().setPacketHandled(true);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.front);
    }
}