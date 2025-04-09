package com.raiiiden.warborn.common.object;

import com.raiiiden.warborn.common.init.MenuTypeInit;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BackpackMenu extends AbstractContainerMenu {
    private final ItemStack itemStack;

    public BackpackMenu(int containerId, Inventory inventory, FriendlyByteBuf buf) {
        this(containerId, inventory, buf.readItem());
    }

    public BackpackMenu(int containerId, Inventory inventory, ItemStack itemStack) {
        super(MenuTypeInit.BACKPACK_MENU.get(), containerId);
        this.itemStack = itemStack;

        IItemHandler handler = itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if (handler == null) return;

        int startX = 8;
        int backpackStartY = 18;

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int index = col + row * 9;
                this.addSlot(new SlotItemHandler(handler, index, startX + col * 18, backpackStartY + row * 18));
            }
        }

        int inventoryStartY = backpackStartY + 66;

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, inventoryStartY + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, inventoryStartY + 58));
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
                if (cap instanceof INBTSerializable<?> serializable) {
                    CompoundTag tag = itemStack.getOrCreateTag();
                    tag.put("BackpackCap", serializable.serializeNBT());
                    itemStack.setTag(tag);
                }
            });
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        IItemHandler handler = this.itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        if (handler == null) return ItemStack.EMPTY;

        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack current = slot.getItem();
            itemstack = current.copy();

            if (index < handler.getSlots()) {
                if (!this.moveItemStackTo(current, handler.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(current, 0, handler.getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (current.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}