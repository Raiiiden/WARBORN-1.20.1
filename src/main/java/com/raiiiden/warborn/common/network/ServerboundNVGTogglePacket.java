package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.event.HelmetBatteryTickHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Sent client → server when the player toggles NVG on or off.
 * visionType is the active vision type ("nvg", "simple_nvg", "thermal", "digital"),
 * or an empty string when NVG is turned off.
 */
public class ServerboundNVGTogglePacket {

    private final String visionType;

    public ServerboundNVGTogglePacket(String visionType) {
        this.visionType = visionType;
    }

    public ServerboundNVGTogglePacket(FriendlyByteBuf buf) {
        this.visionType = buf.readUtf();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(visionType);
    }

    public static void handle(ServerboundNVGTogglePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                HelmetBatteryTickHandler.setNVGActive(player.getUUID(), msg.visionType, player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
