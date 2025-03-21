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
    private final BackpackItemStackHandler handler;
    private final LazyOptional<IItemHandler> optional;
    private final ItemStack stack;

    public BackpackCapabilityProvider(ItemStack stack) {
        this.stack = stack;
        this.handler = new BackpackItemStackHandler();

        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("BackpackCap")) {
            this.handler.deserializeNBT(tag.getCompound("BackpackCap"));
        }

        this.optional = LazyOptional.of(() -> this.handler);
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
}
