package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.common.object.capability.NVGBatteryCapabilityProvider;
import com.raiiiden.warborn.common.object.capability.NVGBatteryStorage;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NVGBatteryItem extends Item {

    public NVGBatteryItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new NVGBatteryCapabilityProvider(stack);
    }

    // Cannot go into chestplate bundles or vanilla bundles
    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    // Show a green-to-red charge bar on the item
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return NVGBatteryStorage.readEnergy(stack) < NVGBatteryStorage.MAX_CAPACITY;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        return Math.round(13f * NVGBatteryStorage.readEnergy(stack) / NVGBatteryStorage.MAX_CAPACITY);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        float f = (float) NVGBatteryStorage.readEnergy(stack) / NVGBatteryStorage.MAX_CAPACITY;
        int r = (int) (255f * Math.min(1f, 2f * (1f - f)));
        int g = (int) (255f * Math.min(1f, 2f * f));
        return (r << 16) | (g << 8);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        int energy = NVGBatteryStorage.readEnergy(stack);
        int max = NVGBatteryStorage.MAX_CAPACITY;
        ChatFormatting color = energy > max / 4 ? ChatFormatting.GREEN : ChatFormatting.RED;
        tooltip.add(Component.literal(energy + " / " + max + " RF").withStyle(color));
    }
}
