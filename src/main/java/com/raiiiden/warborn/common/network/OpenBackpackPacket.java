package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.object.BackpackMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class OpenBackpackPacket {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ItemStack backpackItem;

    public OpenBackpackPacket(ItemStack backpackItem) {
        this.backpackItem = backpackItem;
    }

    public OpenBackpackPacket(FriendlyByteBuf buf) {
        this.backpackItem = buf.readItem();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(this.backpackItem);
    }

    public static void handle(OpenBackpackPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                packet.backpackItem.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                });

                NetworkHooks.openScreen(player, new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("menu.warborn.backpack");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory inventory, Player player) {
                        return new BackpackMenu(containerId, inventory, packet.backpackItem);
                    }
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
