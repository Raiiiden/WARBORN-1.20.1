package com.raiiiden.warborn.common.object.slot;

import com.raiiiden.warborn.common.item.BackpackItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

// Read-only display slot for the backpack upgrade; insertion and ejection go through BackpackItem's right-click mechanic.
public class BackpackUpgradeSlot extends Slot {

    private final ItemStack backpackStack;

    public BackpackUpgradeSlot(ItemStack backpackStack, int x, int y) {
        // DummyContainer(1): size >= 1 so vanilla tooltip dispatch doesn't skip this slot.
        super(new DummyContainer(1), 0, x, y);
        this.backpackStack = backpackStack;
    }

    @Override public ItemStack getItem()              { return BackpackItem.getInsertedUpgrade(backpackStack); }
    @Override public boolean hasItem()                { return !BackpackItem.getInsertedUpgrade(backpackStack).isEmpty(); }
    @Override public void set(ItemStack stack)        { }
    @Override public ItemStack remove(int amount)     { return ItemStack.EMPTY; }
    @Override public boolean mayPlace(ItemStack stack){ return false; }
    @Override public boolean mayPickup(Player player) { return false; }
    @Override public int getMaxStackSize()            { return 1; }
    @Override public void setChanged()               { }
}