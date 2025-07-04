package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.WARBORN;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ModNetworking {
    private static final String VER = "1";
    public static final SimpleChannel PACKET_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(WARBORN.MODID, "main"),
            () -> VER, VER::equals, VER::equals
    );

    public static void openBackpack(ItemStack backpackItem) {
        sendToServer(new OpenBackpackPacket(backpackItem));
    }

    public static void sendRemovePlatePacket(boolean front) {
        sendToServer(new RemovePlatePacket(front));
    }

    public static void registerPackets() {
        var id = 0;
        PACKET_CHANNEL.registerMessage(id++, OpenBackpackPacket.class, OpenBackpackPacket::encode, OpenBackpackPacket::new, OpenBackpackPacket::handle);
        PACKET_CHANNEL.registerMessage(id++, RemovePlatePacket.class, RemovePlatePacket::encode, RemovePlatePacket::new, RemovePlatePacket::handle);
        PACKET_CHANNEL.registerMessage(id++, ToggleHelmetTopPacket.class, ToggleHelmetTopPacket::encode, ToggleHelmetTopPacket::new, ToggleHelmetTopPacket::handle);
        PACKET_CHANNEL.registerMessage(id++, ServerboundNVGArmAnimationPacket.class, ServerboundNVGArmAnimationPacket::encode, ServerboundNVGArmAnimationPacket::new, ServerboundNVGArmAnimationPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        PACKET_CHANNEL.registerMessage(id++, ClientboundNVGArmAnimationPacket.class, ClientboundNVGArmAnimationPacket::encode, ClientboundNVGArmAnimationPacket::new, ClientboundNVGArmAnimationPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
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

    public static void sendToggleHelmetTop(boolean open) {
        sendToServer(new ToggleHelmetTopPacket(open));
    }
}