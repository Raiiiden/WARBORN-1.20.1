package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.item.BackpackItem;
import com.raiiiden.warborn.common.object.BackpackMenu;
import com.raiiiden.warborn.common.object.capability.BackpackItemStackHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
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

    public static void handle(OpenBackpackPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;

            ItemStack actualBackpack = findActualBackpack(player, packet.backpackItem);
            if (actualBackpack.isEmpty()) return;

            actualBackpack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                if (handler instanceof BackpackItemStackHandler) {
                    // capability always initialized — no action needed
                }
            });

            NetworkHooks.openScreen(player, new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.translatable("menu.fracturepoint.backpack");
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, net.minecraft.world.entity.player.Inventory inventory, Player player) {
                    return new BackpackMenu(containerId, inventory, actualBackpack);
                }
            }, buf -> buf.writeItem(actualBackpack));
        });

        ctx.get().setPacketHandled(true);
    }

    private static ItemStack findActualBackpack(ServerPlayer player, ItemStack ignored) {

        // Chest slot
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (BackpackItem.isBackpackItem(chest)) {
            return chest;
        }

        // Inventory
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (BackpackItem.isBackpackItem(stack)) {
                return stack;
            }
        }

        // Curios
        CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
            // handled below
        });

        var curiosOpt = CuriosApi.getCuriosInventory(player);
        if (curiosOpt.isPresent()) {
            var curios = curiosOpt.resolve().get();
            for (var entry : curios.getCurios().values()) {
                var stacks = entry.getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    ItemStack stack = stacks.getStackInSlot(i);
                    if (BackpackItem.isBackpackItem(stack)) {
                        return stack;
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeItem(this.backpackItem);
    }
}