package com.raiiiden.warborn.common.object.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlateHolderProvider implements ICapabilitySerializable<CompoundTag> {
    public static final Capability<PlateHolderCapability> CAP = CapabilityManager.get(new CapabilityToken<>() {});
    private final PlateHolderImpl backend;
    private final LazyOptional<PlateHolderCapability> optional;

    public PlateHolderProvider(ItemStack chestplate) {
        this.backend = new PlateHolderImpl(chestplate);
        this.optional = LazyOptional.of(() -> backend);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CAP ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        // Capability data is saved directly to the chestplate’s tag. Nothing extra needed.
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        // No-op: capability reads/writes directly from chestplate’s NBT.
    }
}
