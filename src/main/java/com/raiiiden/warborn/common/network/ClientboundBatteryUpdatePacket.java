package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.client.network.ClientBatteryUpdateHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Sent server → client every tick while NVG is active to keep the battery display in sync. */
public class ClientboundBatteryUpdatePacket {

    private final int energy;

    public ClientboundBatteryUpdatePacket(int energy) { this.energy = energy; }

    public ClientboundBatteryUpdatePacket(FriendlyByteBuf buf) { this.energy = buf.readVarInt(); }

    public void encode(FriendlyByteBuf buf) { buf.writeVarInt(energy); }

    public static void handle(ClientboundBatteryUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientBatteryUpdateHandler.handle(msg.energy));
        ctx.get().setPacketHandled(true);
    }
}
