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

public class BackpackCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    private BackpackItemStackHandler handler;
    private final LazyOptional<IItemHandler> optional = LazyOptional.of(this::getCachedInventory);

    public BackpackCapabilityProvider(ItemStack backpack) {
    }

    private BackpackItemStackHandler getCachedInventory() {
        if (handler == null) {
            handler = new BackpackItemStackHandler();
        }
        return handler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return optional.cast();
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.getCachedInventory().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.getCachedInventory().deserializeNBT(nbt);
    }
}
