package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.WARBORN;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetworking {
    private static final String VER = "1";
    public static final SimpleChannel PACKET_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(WARBORN.MODID, "main"),
            () -> VER, VER::equals, VER::equals
    );

    public static void openBackpack(ItemStack backpackItem) {
        sendToServer(new OpenBackpackPacket(backpackItem));
    }

    public static void registerPackets() {
        var id = 0;
        PACKET_CHANNEL.registerMessage(id++, OpenBackpackPacket.class, OpenBackpackPacket::encode, OpenBackpackPacket::new, OpenBackpackPacket::handle);
    }

    public static <MSG> void sendToServer(MSG message) {
        PACKET_CHANNEL.sendToServer(message);
    }

    protected static <MSG> void sendToPlayer(MSG message, ServerPlayer serverPlayer) {
        PACKET_CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
    }

    protected static <MSG> void sendToClients(MSG message) {
        PACKET_CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }
}
