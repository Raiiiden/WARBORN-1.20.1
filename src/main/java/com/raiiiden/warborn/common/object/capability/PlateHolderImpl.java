package com.raiiiden.warborn.common.object.capability;

import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlateHolderImpl implements PlateHolderCapability {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ItemStack chestplate;

    public PlateHolderImpl(ItemStack chestplate) {
        this.chestplate = chestplate;
    }

    private CompoundTag getPlateData() {
        CompoundTag tag = chestplate.getOrCreateTag();
        if (!tag.contains("PlateData")) {
            tag.put("PlateData", new CompoundTag());
        }
        return tag.getCompound("PlateData");
    }

    private void setPlateData(CompoundTag plateData) {
        chestplate.getOrCreateTag().put("PlateData", plateData);
    }

    private Plate readPlate(String key) {
        CompoundTag plateData = getPlateData();
        if (plateData.contains(key)) {
            return new Plate(plateData.getCompound(key));
        }
        return null;
    }

    private void writePlate(String key, Plate plate) {
        CompoundTag plateData = getPlateData();
        if (plate != null) {
            plateData.put(key, plate.save());
        } else {
            plateData.remove(key);
        }
        setPlateData(plateData);
    }

    private void damagePlate(String key, float amount) {
        Plate plate = readPlate(key);
        if (plate != null && !plate.isBroken()) {
            plate.damage(amount);
            writePlate(key, plate);
            LOGGER.info("Damaged {}. Remaining: {}/{}", key, plate.getCurrentDurability(), plate.getMaxDurability());
        }
    }

    @Override
    public boolean hasFrontPlate() {
        Plate plate = readPlate("FrontPlate");
        return plate != null && !plate.isBroken();
    }

    @Override
    public boolean hasBackPlate() {
        Plate plate = readPlate("BackPlate");
        return plate != null && !plate.isBroken();
    }

    @Override
    public Plate getFrontPlate() {
        return readPlate("FrontPlate");
    }

    @Override
    public Plate getBackPlate() {
        return readPlate("BackPlate");
    }

    @Override
    public void insertFrontPlate(Plate plate) {
        writePlate("FrontPlate", plate);
        // LOGGER.info("Inserted front plate: {} {}", plate.getTier(), plate.getMaterial());
    }

    @Override
    public void insertBackPlate(Plate plate) {
        writePlate("BackPlate", plate);
        // LOGGER.info("Inserted back plate: {} {}", plate.getTier(), plate.getMaterial());
    }

    @Override
    public void insertFrontPlate(ProtectionTier tier, MaterialType material) {
        insertFrontPlate(new Plate(tier, material));
    }

    @Override
    public void insertBackPlate(ProtectionTier tier, MaterialType material) {
        insertBackPlate(new Plate(tier, material));
    }

    @Override
    public void damageFrontPlate(float damageAmount) {
        damagePlate("FrontPlate", damageAmount);
    }

    @Override
    public void damageBackPlate(float damageAmount) {
        damagePlate("BackPlate", damageAmount);
    }

    @Override
    public void removeFrontPlate() {
        writePlate("FrontPlate", null);
        // LOGGER.info("Front plate removed.");
    }

    @Override
    public void removeBackPlate() {
        writePlate("BackPlate", null);
        // LOGGER.info("Back plate removed.");
    }
}