package com.raiiiden.warborn.common.item;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

// Tooltip data for the helmet goggle + battery display; always renders exactly 2 fixed slots.
public record HelmetSlotTooltip(ItemStack goggles, ItemStack battery) implements TooltipComponent {
}
