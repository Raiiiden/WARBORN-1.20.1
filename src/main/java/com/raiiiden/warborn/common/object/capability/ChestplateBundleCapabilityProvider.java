package com.raiiiden.warborn.common.object.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChestplateBundleCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    private final ChestplateBundleHandler handler;
    private final LazyOptional<IItemHandler> optional;
    private final ItemStack stack;

    public ChestplateBundleCapabilityProvider(ItemStack stack) {
        this.stack = stack;
        this.handler = new ChestplateBundleHandler(stack);
        this.optional = LazyOptional.of(() -> handler);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.handler.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.handler.deserializeNBT(nbt);
    }

    public void saveToChestplate() {
        handler.saveToItem(stack);
    }

    public void clearEmptySlots() {
        handler.clearEmptySlots();
        saveToChestplate();
    }
}
