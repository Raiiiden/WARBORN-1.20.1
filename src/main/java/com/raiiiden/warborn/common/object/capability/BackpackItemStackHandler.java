package com.raiiiden.warborn.common.object.capability;

import com.raiiiden.warborn.common.item.BackpackItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class BackpackItemStackHandler extends ItemStackHandler {

    public static final int COLUMNS    = 9;
    public static final int TOTAL_ROWS = 6; // Tier 3 max = 6×9 = 54

    public BackpackItemStackHandler() {
        super(COLUMNS * TOTAL_ROWS); // Always allocate max capacity; tier controls visibility
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return !BackpackItem.isBackpackItem(stack);
    }
}
