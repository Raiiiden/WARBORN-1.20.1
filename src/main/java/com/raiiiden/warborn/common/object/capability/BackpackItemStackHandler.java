package com.raiiiden.warborn.common.object.capability;

import com.raiiiden.warborn.common.item.WarbornArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class BackpackItemStackHandler extends ItemStackHandler {
    public BackpackItemStackHandler() {
        super(27); // 3x9 = 27 slots
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return !WarbornArmorItem.isBackpackItem(stack);
    }

    public boolean isUninitialized() {
        return this.getSlots() == 0;
    }
}
