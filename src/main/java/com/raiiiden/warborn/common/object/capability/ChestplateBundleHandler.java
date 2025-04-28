package com.raiiiden.warborn.common.object.capability;

import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ChestplateBundleHandler extends ItemStackHandler {
    public static final int MAX_SLOTS = 4;
    public static final int MAX_STACK_SIZE = 100;

    private final ItemStack chestplate;

    public ChestplateBundleHandler(ItemStack chestplate) {
        super(MAX_SLOTS);
        this.chestplate = chestplate;
        loadFromItem(chestplate);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getItem().canFitInsideContainerItems()
                && !WBArmorItem.isArmor(stack);
    }

    @Override
    public int getStackLimit(int slot, @NotNull ItemStack stack) {
        int naturalMax = stack.getMaxStackSize();
        return naturalMax > 1 ? MAX_STACK_SIZE : 1;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        loadFromItem(chestplate);
        return super.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        ItemStack result = super.extractItem(slot, amount, simulate);
        if (!simulate && !result.isEmpty()) {
            saveToItem(chestplate);
        }
        return result;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        ItemStack result = super.insertItem(slot, stack, simulate);
        if (!simulate && !ItemStack.matches(result, stack)) {
            saveToItem(chestplate);
        }
        return result;
    }

    public void loadFromItem(ItemStack chestplate) {
        CompoundTag tag = chestplate.getOrCreateTag();
        if (tag.contains("Items", 9)) {
            CompoundTag container = new CompoundTag();
            container.put("Items", tag.getList("Items", 10));
            this.deserializeNBT(container);
        }
    }

    public void saveToItem(ItemStack chestplate) {
        CompoundTag container = this.serializeNBT();
        if (container.contains("Items", 9)) {
            chestplate.getOrCreateTag().put("Items", container.getList("Items", 10));
        }
    }

    public void clearEmptySlots() {
        for (int i = 0; i < this.getSlots(); i++) {
            if (this.getStackInSlot(i).isEmpty()) {
                this.setStackInSlot(i, ItemStack.EMPTY);
            }
        }
        saveToItem(chestplate);
    }
}
