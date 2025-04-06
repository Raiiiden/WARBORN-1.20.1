package com.raiiiden.warborn.common.object.capability;

public interface PlateHolderCapability {
    boolean hasFrontPlate();
    boolean hasBackPlate();

    int getFrontDurability();
    int getBackDurability();

    void insertFrontPlateWithDurability(int hitsLeft);
    void insertBackPlateWithDurability(int hitsLeft);

    void damageFront(int amount);
    void damageBack(int amount);

    void removeFrontPlate();
    void removeBackPlate();
}
