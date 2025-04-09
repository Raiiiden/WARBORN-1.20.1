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
    private Plate frontPlate;
    private Plate backPlate;

    public PlateHolderImpl(ItemStack chestplate) {
        this.chestplate = chestplate;
        loadPlatesFromNBT();
    }

    private void loadPlatesFromNBT() {
        CompoundTag tag = chestplate.getOrCreateTag();
        if (!tag.contains("PlateData")) return;

        CompoundTag plateData = tag.getCompound("PlateData");

        // Load front plate
        if (plateData.contains("FrontPlate")) {
            frontPlate = new Plate(plateData.getCompound("FrontPlate"));
        }
        // Legacy support
        else if (plateData.contains("FrontDurability") && plateData.getInt("FrontDurability") > 0) {
            frontPlate = new Plate(ProtectionTier.LEVEL_III, MaterialType.STEEL);
            frontPlate.damage(frontPlate.getMaxDurability() - plateData.getInt("FrontDurability"));
        }

        // Load back plate
        if (plateData.contains("BackPlate")) {
            backPlate = new Plate(plateData.getCompound("BackPlate"));
        }
        // Legacy support
        else if (plateData.contains("BackDurability") && plateData.getInt("BackDurability") > 0) {
            backPlate = new Plate(ProtectionTier.LEVEL_III, MaterialType.STEEL);
            backPlate.damage(backPlate.getMaxDurability() - plateData.getInt("BackDurability"));
        }
    }

    private void savePlatesToNBT() {
        CompoundTag tag = chestplate.getOrCreateTag();
        CompoundTag plateData = new CompoundTag();

        if (frontPlate != null) {
            plateData.put("FrontPlate", frontPlate.save());
            // For legacy support
            plateData.putInt("FrontDurability", (int) frontPlate.getCurrentDurability());
        } else {
            plateData.putInt("FrontDurability", 0);
        }

        if (backPlate != null) {
            plateData.put("BackPlate", backPlate.save());
            // For legacy support
            plateData.putInt("BackDurability", (int) backPlate.getCurrentDurability());
        } else {
            plateData.putInt("BackDurability", 0);
        }

        tag.put("PlateData", plateData);
    }

    @Override
    public boolean hasFrontPlate() {
        return frontPlate != null && !frontPlate.isBroken();
    }

    @Override
    public boolean hasBackPlate() {
        return backPlate != null && !backPlate.isBroken();
    }

    @Override
    public Plate getFrontPlate() {
        return frontPlate;
    }

    @Override
    public Plate getBackPlate() {
        return backPlate;
    }

    @Override
    public void insertFrontPlate(Plate plate) {
        this.frontPlate = plate;
        savePlatesToNBT();
        LOGGER.info("Inserted front plate: {} {}", plate.getTier(), plate.getMaterial());
    }

    @Override
    public void insertBackPlate(Plate plate) {
        this.backPlate = plate;
        savePlatesToNBT();
        LOGGER.info("Inserted back plate: {} {}", plate.getTier(), plate.getMaterial());
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
        if (!hasFrontPlate()) return;

        frontPlate.damage(damageAmount);
        savePlatesToNBT();
        LOGGER.info("Damaged front plate. Remaining: {}/{}",
                frontPlate.getCurrentDurability(), frontPlate.getMaxDurability());
    }

    @Override
    public void damageBackPlate(float damageAmount) {
        if (!hasBackPlate()) return;

        backPlate.damage(damageAmount);
        savePlatesToNBT();
        LOGGER.info("Damaged back plate. Remaining: {}/{}",
                backPlate.getCurrentDurability(), backPlate.getMaxDurability());
    }

    @Override
    public void removeFrontPlate() {
        frontPlate = null;
        savePlatesToNBT();
        LOGGER.info("Front plate removed.");
    }

    @Override
    public void removeBackPlate() {
        backPlate = null;
        savePlatesToNBT();
        LOGGER.info("Back plate removed.");
    }
}