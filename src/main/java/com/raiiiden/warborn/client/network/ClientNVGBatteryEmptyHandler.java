package com.raiiiden.warborn.client.network;

import com.raiiiden.warborn.client.network.ClientBatteryUpdateHandler;
import com.raiiiden.warborn.common.util.HelmetVisionHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Handles the ClientboundNVGBatteryEmptyPacket on the client side.
 * Kept in a separate class to avoid classloading the Minecraft client on the server.
 */
public class ClientNVGBatteryEmptyHandler {

    public static void handle() {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        String activeVision = HelmetVisionHandler.getActiveVisionType(helmet);

        ClientBatteryUpdateHandler.clear();

        if (!activeVision.isEmpty()) {
            // toggleVision will detect it is active and disable it
            HelmetVisionHandler.toggleVision(player);
        }

        player.displayClientMessage(
                Component.literal("Battery depleted!").withStyle(ChatFormatting.RED), true
        );
    }
}
