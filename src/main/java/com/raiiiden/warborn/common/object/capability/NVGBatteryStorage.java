package com.raiiiden.warborn.common.object.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

// IEnergyStorage backed by the NVG battery item's own NBT.
public class NVGBatteryStorage implements IEnergyStorage {

    public static final int MAX_CAPACITY = 50_000;
    public static final String NBT_KEY = "BatteryEnergy";

    private final ItemStack batteryStack;

    public NVGBatteryStorage(ItemStack batteryStack) {
        this.batteryStack = batteryStack;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int current = getEnergyStored();
        int toReceive = Math.min(maxReceive, MAX_CAPACITY - current);
        if (toReceive > 0 && !simulate) {
            batteryStack.getOrCreateTag().putInt(NBT_KEY, current + toReceive);
        }
        return toReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0; // external extraction not allowed; drain via forceDrain
    }

    // Called by the tick handler to drain energy each tick while NVG is active.
    public void forceDrain(int amount) {
        int current = getEnergyStored();
        batteryStack.getOrCreateTag().putInt(NBT_KEY, Math.max(0, current - amount));
    }

    @Override
    public int getEnergyStored() {
        CompoundTag tag = batteryStack.getTag();
        if (tag == null || !tag.contains(NBT_KEY)) {
            return MAX_CAPACITY; // new batteries are fully charged
        }
        return Math.max(0, Math.min(MAX_CAPACITY, tag.getInt(NBT_KEY)));
    }

    @Override
    public int getMaxEnergyStored() {
        return MAX_CAPACITY;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }

    // Reads the stored energy directly from an ItemStack's NBT without going through the capability system.
    public static int readEnergy(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(NBT_KEY)) return MAX_CAPACITY;
        return Math.max(0, Math.min(MAX_CAPACITY, tag.getInt(NBT_KEY)));
    }
}
