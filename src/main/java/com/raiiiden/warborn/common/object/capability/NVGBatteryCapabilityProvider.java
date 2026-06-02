package com.raiiiden.warborn.common.object.capability;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NVGBatteryCapabilityProvider implements ICapabilityProvider {

    private final NVGBatteryStorage storage;
    private final LazyOptional<IEnergyStorage> optional;

    public NVGBatteryCapabilityProvider(ItemStack stack) {
        this.storage = new NVGBatteryStorage(stack);
        this.optional = LazyOptional.of(() -> storage);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == ForgeCapabilities.ENERGY ? optional.cast() : LazyOptional.empty();
    }
}
