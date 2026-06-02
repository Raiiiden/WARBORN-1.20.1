package com.raiiiden.warborn.common.object.slot;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class DummyContainer implements Container {

    private final ItemStackHandler handler;
    private final int fixedSize;

    /** Use for slots backed by a real handler (e.g. DynamicBackpackSlot). */
    public DummyContainer(ItemStackHandler handler) {
        this.handler   = handler;
        this.fixedSize = -1;
    }

    /**
     * Use for read-only display slots with no backing handler (e.g. BackpackUpgradeSlot).
     * size should be >= 1 so vanilla tooltip dispatch doesn't skip the slot.
     */
    public DummyContainer(int size) {
        this.handler   = null;
        this.fixedSize = size;
    }

    @Override
    public int getContainerSize() {
        return handler != null ? handler.getSlots() : fixedSize;
    }

    @Override public boolean isEmpty()   { return true; }
    @Override public ItemStack getItem(int slot) { return ItemStack.EMPTY; }
    @Override public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }
    @Override public ItemStack removeItemNoUpdate(int slot)     { return ItemStack.EMPTY; }
    @Override public void setItem(int slot, ItemStack stack)    {}
    @Override public void setChanged()                          {}
    @Override public boolean stillValid(net.minecraft.world.entity.player.Player player) { return true; }
    @Override public void clearContent()                        {}
}