package com.raiiiden.warborn.common.object.capability;

import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;

public interface PlateHolderCapability {
    boolean hasFrontPlate();

    boolean hasBackPlate();

    Plate getFrontPlate();

    Plate getBackPlate();

    void insertFrontPlate(Plate plate);

    void insertBackPlate(Plate plate);

    void insertFrontPlate(ProtectionTier tier, MaterialType material);

    void insertBackPlate(ProtectionTier tier, MaterialType material);

    void damageFrontPlate(float damageAmount);

    void damageBackPlate(float damageAmount);

    void removeFrontPlate();

    void removeBackPlate();

    // Legacy methods for backward compatibility
    default int getFrontDurability() {
        return hasFrontPlate() ? (int)getFrontPlate().getCurrentDurability() : 0;
    }

    default int getBackDurability() {
        return hasBackPlate() ? (int)getBackPlate().getCurrentDurability() : 0;
    }

    default void insertFrontPlateWithDurability(int durability) {
        insertFrontPlate(ProtectionTier.LEVEL_III, MaterialType.STEEL);
    }

    default void insertBackPlateWithDurability(int durability) {
        insertBackPlate(ProtectionTier.LEVEL_III, MaterialType.STEEL);
    }

    default void damageFront(int amount) {
        damageFrontPlate(amount);
    }

    default void damageBack(int amount) {
        damageBackPlate(amount);
    }
}