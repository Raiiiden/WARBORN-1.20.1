package com.raiiiden.warborn.common.item;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record BackpackTooltipData(List<ItemStack> items, int tier) implements TooltipComponent {
}
