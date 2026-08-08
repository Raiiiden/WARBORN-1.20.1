package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.client.network.ClientNVGBatteryEmptyHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

// Sent server → client when the helmet battery runs out while NVG is active.
public class ClientboundNVGBatteryEmptyPacket {

    public ClientboundNVGBatteryEmptyPacket() {}

    public ClientboundNVGBatteryEmptyPacket(FriendlyByteBuf buf) {}

    public void encode(FriendlyByteBuf buf) {}

    public static void handle(ClientboundNVGBatteryEmptyPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(ClientNVGBatteryEmptyHandler::handle);
        ctx.get().setPacketHandled(true);
    }
}
