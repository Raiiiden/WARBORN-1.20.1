package com.raiiiden.warborn.common.object.plate;

import net.minecraft.nbt.CompoundTag;

public class Plate {
    private final ProtectionTier tier;
    private final MaterialType material;
    private final float maxDurability;
    private float currentDurability;

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

    public ProtectionTier getTier() {
        return tier;
    }

    public MaterialType getMaterial() {
        return material;
    }

    public float getCurrentDurability() {
        return currentDurability;
    }

    public void setCurrentDurability(float durability) {
        this.currentDurability = Math.max(0, Math.min(durability, maxDurability));
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

        float durabilityRatio = getCurrentDurability() / (float) getMaxDurability();
        float baseProtection;

        // Get protection level based on thresholds
        if (incomingDamage < tier.getLowerThreshold()) {
            baseProtection = tier.getThresholdProtection();
        } else if (incomingDamage < tier.getUpperThreshold()) {
            baseProtection = tier.getPartialProtection();
        } else {
            baseProtection = tier.getMinimalProtection();
        }

        // Full protection until plate is almost broken
        if (durabilityRatio >= 0.05f) {
            return baseProtection;
        } else {
            // At <5% durability, scale down protection (e.g., 20% effectiveness)
            return baseProtection * 0.2f;
        }
    }

    public void damage(float damageAmount) {
        if (isBroken()) return;

        this.currentDurability = Math.max(0, material.calculateRemainingDurability(currentDurability, damageAmount));
    }

    public float getSpeedModifier() {
        return material.getSpeedModifier();
    }
}