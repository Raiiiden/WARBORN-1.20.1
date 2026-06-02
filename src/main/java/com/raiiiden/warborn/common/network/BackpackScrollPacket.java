package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.object.BackpackMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BackpackScrollPacket {

    private final int scrollRow;

    public BackpackScrollPacket(int scrollRow) {
        this.scrollRow = scrollRow;
    }

    public BackpackScrollPacket(FriendlyByteBuf buf) {
        this.scrollRow = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(scrollRow);
    }

    public static void handle(BackpackScrollPacket pkt, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null && player.containerMenu instanceof BackpackMenu menu) {
                menu.setScrollRowServer(pkt.scrollRow);
                menu.broadcastFullState();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
