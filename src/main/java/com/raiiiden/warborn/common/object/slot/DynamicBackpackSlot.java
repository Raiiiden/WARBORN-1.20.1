package com.raiiiden.warborn.common.object.slot;

import com.raiiiden.warborn.common.object.capability.BackpackItemStackHandler;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.IntSupplier;

public class DynamicBackpackSlot extends Slot {

    private final BackpackItemStackHandler handler;
    private final IntSupplier index;

    public DynamicBackpackSlot(
            BackpackItemStackHandler handler,
            IntSupplier index,
            int x,
            int y,
            int slotIndex
    ) {
        // FIX: pass handler into DummyContainer so getContainerSize() > 0,
        // which is required for vanilla tooltip dispatch to work.
        super(new DummyContainer(handler), slotIndex, x, y);
        this.handler = handler;
        this.index = index;
    }

    private int idx() { return index.getAsInt(); }

    private boolean isValid() {
        int i = idx();
        return i >= 0 && i < handler.getSlots();
    }

    @Override public void setChanged() {}

    @Override
    public boolean mayPickup(net.minecraft.world.entity.player.Player player) { return true; }

    @Override
    public ItemStack getItem() {
        return isValid() ? handler.getStackInSlot(idx()) : ItemStack.EMPTY;
    }

    @Override
    public boolean hasItem() {
        return isValid() && !handler.getStackInSlot(idx()).isEmpty();
    }

    @Override
    public void set(ItemStack stack) {
        if (isValid()) handler.setStackInSlot(idx(), stack);
    }

    @Override
    public ItemStack remove(int amount) {
        return isValid() ? handler.extractItem(idx(), amount, false) : ItemStack.EMPTY;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return isValid() && handler.isItemValid(idx(), stack);
    }

    @Override
    public int getMaxStackSize() {
        return isValid() ? handler.getSlotLimit(idx()) : 0;
    }
}