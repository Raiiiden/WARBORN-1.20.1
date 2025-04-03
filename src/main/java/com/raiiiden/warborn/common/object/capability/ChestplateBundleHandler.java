package com.raiiiden.warborn.common.object.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ChestplateBundleHandler extends ItemStackHandler {
    public static final int MAX_SLOTS = 4;
    public static final int MAX_STACK_SIZE = 100;

    public ChestplateBundleHandler() {
        super(MAX_SLOTS);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getItem().canFitInsideContainerItems() && !com.raiiiden.warborn.item.WarbornArmorItem.isArmor(stack);
    }

    @Override
    public int getStackLimit(int slot, @NotNull ItemStack stack) {
        return MAX_STACK_SIZE;
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
    }
}
