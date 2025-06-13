package com.raiiiden.warborn.common.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class ServerNVGArmAnimationHandler {
    public static void broadcastArmAnimation(ServerPlayer sender) {
        // Broadcast to all clients tracking this player (including self)
        ModNetworking.PACKET_CHANNEL.send(
                PacketDistributor.TRACKING_ENTITY.with(() -> sender),
                new ClientboundNVGArmAnimationPacket(sender.getId(), true)
        );
        // Also send to the sender (for self-view in F5)
        ModNetworking.PACKET_CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> sender),
                new ClientboundNVGArmAnimationPacket(sender.getId(), true)
        );
    }
}