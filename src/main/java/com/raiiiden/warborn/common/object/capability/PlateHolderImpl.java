package com.raiiiden.warborn.common.object.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlateHolderImpl implements PlateHolderCapability {
    public static final int MAX_DURABILITY = 10;
    private static final Logger LOGGER = LogManager.getLogger();

    private final ItemStack chestplate;

    public PlateHolderImpl(ItemStack chestplate) {
        this.chestplate = chestplate;
    }

    private CompoundTag getOrCreatePlateTag() {
        CompoundTag tag = chestplate.getOrCreateTag();
        if (!tag.contains("PlateData")) tag.put("PlateData", new CompoundTag());
        return tag.getCompound("PlateData");
    }

    private void saveTag(CompoundTag data) {
        CompoundTag tag = chestplate.getOrCreateTag();
        tag.put("PlateData", data);
    }

    @Override
    public boolean hasFrontPlate() {
        return getFrontDurability() > 0;
    }

    @Override
    public boolean hasBackPlate() {
        return getBackDurability() > 0;
    }

    @Override
    public int getFrontDurability() {
        CompoundTag tag = getOrCreatePlateTag();
        return tag.getInt("FrontDurability");
    }

    @Override
    public int getBackDurability() {
        CompoundTag tag = getOrCreatePlateTag();
        return tag.getInt("BackDurability");
    }

    @Override
    public void insertFrontPlateWithDurability(int hitsLeft) {
        CompoundTag tag = getOrCreatePlateTag();
        tag.putInt("FrontDurability", Math.min(MAX_DURABILITY, Math.max(0, hitsLeft)));
        saveTag(tag);
        LOGGER.info("Inserted front plate with {} durability.", hitsLeft);
    }

    @Override
    public void insertBackPlateWithDurability(int hitsLeft) {
        CompoundTag tag = getOrCreatePlateTag();
        tag.putInt("BackDurability", Math.min(MAX_DURABILITY, Math.max(0, hitsLeft)));
        saveTag(tag);
        LOGGER.info("Inserted back plate with {} durability.", hitsLeft);
    }

    @Override
    public void damageFront(int amount) {
        CompoundTag tag = getOrCreatePlateTag();
        int durability = Math.max(0, tag.getInt("FrontDurability") - amount);
        tag.putInt("FrontDurability", durability);
        saveTag(tag);
        LOGGER.info("Damaged front plate. Remaining: {}", durability);
    }

    @Override
    public void damageBack(int amount) {
        CompoundTag tag = getOrCreatePlateTag();
        int durability = Math.max(0, tag.getInt("BackDurability") - amount);
        tag.putInt("BackDurability", durability);
        saveTag(tag);
        LOGGER.info("Damaged back plate. Remaining: {}", durability);
    }

    @Override
    public void removeFrontPlate() {
        CompoundTag tag = getOrCreatePlateTag();
        tag.putInt("FrontDurability", 0);
        saveTag(tag);
        LOGGER.info("Front plate removed.");
    }

    @Override
    public void removeBackPlate() {
        CompoundTag tag = getOrCreatePlateTag();
        tag.putInt("BackDurability", 0);
        saveTag(tag);
        LOGGER.info("Back plate removed.");
    }
}
