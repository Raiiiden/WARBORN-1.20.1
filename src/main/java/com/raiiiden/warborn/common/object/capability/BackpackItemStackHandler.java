package com.raiiiden.warborn.common.object.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class BackpackItemStackHandler extends ItemStackHandler {

    public BackpackItemStackHandler() {
        super(27); // Sets the inventory size for 3x9 grid (27 slots)
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag itemList = new ListTag();
        for (int i = 0; i < getSlots(); i++) {
            CompoundTag itemTag = new CompoundTag();
            this.getStackInSlot(i).save(itemTag);
            itemTag.putInt("Slot", i);
            itemList.add(itemTag);
        }
        tag.put("Items", itemList);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ListTag itemList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < itemList.size(); i++) {
            CompoundTag itemTag = itemList.getCompound(i);
            int slot = itemTag.getInt("Slot");
            if (slot >= 0 && slot < this.getSlots()) {
                this.setStackInSlot(slot, ItemStack.of(itemTag));
            }
        }
    }
}
