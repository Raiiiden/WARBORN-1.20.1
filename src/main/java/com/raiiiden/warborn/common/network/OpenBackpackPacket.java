package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.object.BackpackMenu;
import com.raiiiden.warborn.common.object.capability.BackpackItemStackHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;

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
            if (player == null) return;

            ItemStack actualBackpack = findActualBackpack(player, packet.backpackItem);
            if (actualBackpack.isEmpty()) return;

            actualBackpack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                if (handler instanceof BackpackItemStackHandler backpackHandler) {
                    if (backpackHandler.isUninitialized()) {
                        LOGGER.warn("Backpack capability is uninitialized (0 slots). Open manually once to sync NBT.");
                    }
                }
            });

            NetworkHooks.openScreen(player, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("menu.warborn.backpack");
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory inventory, Player player) {
                    return new BackpackMenu(containerId, inventory, actualBackpack);
                }
            }, buf -> buf.writeItem(actualBackpack));
        });

        ctx.get().setPacketHandled(true);
    }

    private static ItemStack findActualBackpack(ServerPlayer player, ItemStack packetBackpack) {
        // Main inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameTags(stack, packetBackpack)) {
                return stack;
            }
        }

        // Armor slots
        for (ItemStack stack : player.getArmorSlots()) {
            if (ItemStack.isSameItemSameTags(stack, packetBackpack)) {
                return stack;
            }
        }

        // Curios slots
        LazyOptional<ICuriosItemHandler> curiosOpt = CuriosApi.getCuriosInventory(player);
        if (curiosOpt.isPresent()) {
            ICuriosItemHandler curios = curiosOpt.resolve().get();
            for (var entry : curios.getCurios().entrySet()) {
                var handler = entry.getValue();
                for (int i = 0; i < handler.getStacks().getSlots(); i++) {
                    ItemStack stack = handler.getStacks().getStackInSlot(i);
                    if (ItemStack.isSameItemSameTags(stack, packetBackpack)) {
                        return stack;
                    }
                }
            }
        }

        return ItemStack.EMPTY;
    }
}
