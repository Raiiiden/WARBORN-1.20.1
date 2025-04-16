package com.raiiiden.warborn.common.object.capability;

import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraftforge.registries.RegistryObject;

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

    default void insertFrontPlate(RegistryObject<ProtectionTier> tierRef, RegistryObject<MaterialType> materialRef) {
        insertFrontPlate(tierRef.get(), materialRef.get());
    }

    default void insertBackPlate(RegistryObject<ProtectionTier> tierRef, RegistryObject<MaterialType> materialRef) {
        insertBackPlate(tierRef.get(), materialRef.get());
    }
}