package com.raiiiden.warborn.common.item;

import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

/**
 * Tooltip data carrier for the helmet goggle + battery slot display.
 * Always renders exactly 2 fixed slots regardless of whether items are present.
 */
public record HelmetSlotTooltip(ItemStack goggles, ItemStack battery) implements TooltipComponent {
}
