package com.raiiiden.warborn.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class ServerboundNVGArmAnimationPacket {
    // Data fields (e.g., boolean start)
    public final boolean start;

    public ServerboundNVGArmAnimationPacket(boolean start) {
        this.start = start;
    }
    public ServerboundNVGArmAnimationPacket(FriendlyByteBuf buf) {
        this.start = buf.readBoolean();
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(start);
    }
    public static void handle(ServerboundNVGArmAnimationPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var sender = ctx.get().getSender();
            if (sender != null && msg.start) {
                // Broadcast to clients: use ClientboundNVGArmAnimationPacket
                ModNetworking.sendToClients(new ClientboundNVGArmAnimationPacket(sender.getId(), true));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}