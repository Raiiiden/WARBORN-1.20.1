package com.raiiiden.warborn.common.object.plate;

import net.minecraft.nbt.CompoundTag;

public class Plate {
    private final ProtectionTier tier;
    private final MaterialType material;
    private float currentDurability;
    private final float maxDurability;

    public Plate(ProtectionTier tier, MaterialType material) {
        this.tier = tier;
        this.material = material;
        this.maxDurability = material.getBaseDurability();
        this.currentDurability = maxDurability;
    }

    public Plate(CompoundTag tag) {
        this.tier = ProtectionTier.valueOf(tag.getString("Tier"));
        this.material = MaterialType.valueOf(tag.getString("Material"));
        this.maxDurability = tag.getFloat("MaxDurability");
        this.currentDurability = tag.getFloat("CurrentDurability");
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Tier", tier.name());
        tag.putString("Material", material.name());
        tag.putFloat("MaxDurability", maxDurability);
        tag.putFloat("CurrentDurability", currentDurability);
        return tag;
    }

    public void setCurrentDurability(float durability) {
        this.currentDurability = Math.max(0, Math.min(durability, maxDurability));
    }

    public ProtectionTier getTier() {
        return tier;
    }

    public MaterialType getMaterial() {
        return material;
    }

    public float getCurrentDurability() {
        return currentDurability;
    }

    public float getMaxDurability() {
        return maxDurability;
    }

    public float getIntegrity() {
        return currentDurability / maxDurability;
    }

    public boolean isBroken() {
        return currentDurability <= 0;
    }

    public float calculateDamageReduction(float incomingDamage) {
        if (isBroken()) return 0;

        // Get base protection based on tier thresholds
        float baseProtection;
        if (incomingDamage < tier.getLowerThreshold()) {
            baseProtection = tier.getThresholdProtection();
        } else if (incomingDamage < tier.getUpperThreshold()) {
            baseProtection = tier.getPartialProtection();
        } else {
            baseProtection = tier.getMinimalProtection();
        }

        // Apply material-specific protection behavior
        return material.calculateEffectiveProtection(currentDurability, maxDurability, baseProtection);
    }

    public void damage(float damageAmount) {
        if (isBroken()) return;

        this.currentDurability = Math.max(0, material.calculateRemainingDurability(currentDurability, damageAmount));
    }

    public float getSpeedModifier() {
        return material.getSpeedModifier();
    }
}