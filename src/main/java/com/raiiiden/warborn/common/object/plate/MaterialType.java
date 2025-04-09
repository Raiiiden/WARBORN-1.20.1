package com.raiiiden.warborn.common.object.plate;

import java.util.function.BiFunction;

public enum MaterialType {
    SOFT_KEVLAR(220, 0.05f,
            (damage, maxDurability) -> maxDurability - damage,
            (damagePercent, baseProtection) -> baseProtection * (1 - (damagePercent * 0.8f))
    ),

    STEEL(750, -0.15f,
            (damage, maxDurability) -> {
                float remainingPercent = 1 - (damage / maxDurability);
                return remainingPercent > 0.3f ? maxDurability * 0.7f + (remainingPercent - 0.3f) * maxDurability * 0.3f : remainingPercent * maxDurability;
            },
            (damagePercent, baseProtection) -> damagePercent < 0.7f ? baseProtection : baseProtection * (1 - ((damagePercent - 0.7f) * 3.0f))
    ),

    CERAMIC(600, -0.10f,
            (damage, maxDurability) -> {
                float damagePercent = damage / maxDurability;
                return maxDurability - (damage * (1 + damagePercent * 0.5f));
            },
            (damagePercent, baseProtection) -> {
                if (damagePercent < 0.3f) return baseProtection;
                else if (damagePercent < 0.5f) return baseProtection * 0.8f;
                else if (damagePercent < 0.7f) return baseProtection * 0.5f;
                else return baseProtection * 0.2f;
            }
    ),

    POLYETHYLENE(500, -0.05f,
            (damage, maxDurability) -> {
                float damagePercent = damage / maxDurability;
                return damagePercent < 0.4f ? maxDurability - (damage * 0.7f) : maxDurability - (damage * 1.2f);
            },
            (damagePercent, baseProtection) -> {
                if (damagePercent < 0.3f) return baseProtection;
                else if (damagePercent < 0.5f) return baseProtection * 0.9f;
                else if (damagePercent < 0.7f) return baseProtection * 0.7f;
                else return baseProtection * 0.5f;
            }
    ),

    COMPOSITE(1200, -0.12f,
            (damage, maxDurability) -> {
                float damagePercent = damage / maxDurability;
                if (damagePercent < 0.3f) {
                    return maxDurability - (damage * 1.2f);
                } else if (damagePercent < 0.7f) {
                    return maxDurability * 0.7f - ((damage - (maxDurability * 0.3f)) * 0.6f);
                } else {
                    return maxDurability * 0.3f - ((damage - (maxDurability * 0.7f)) * 0.3f);
                }
            },
            (damagePercent, baseProtection) -> {
                if (damagePercent < 0.3f) return baseProtection;
                else if (damagePercent < 0.7f) return baseProtection * (1 - ((damagePercent - 0.3f) * 0.25f));
                else return Math.max(0.4f, baseProtection * (0.85f - ((damagePercent - 0.7f) * 1.5f)));
            }
    );

    private final int baseDurability;
    private final float speedModifier;
    private final BiFunction<Float, Float, Float> durabilityConsumer;
    private final BiFunction<Float, Float, Float> protectionConsumer;

    MaterialType(int baseDurability, float speedModifier,
                 BiFunction<Float, Float, Float> durabilityConsumer,
                 BiFunction<Float, Float, Float> protectionConsumer) {
        this.baseDurability = baseDurability;
        this.speedModifier = speedModifier;
        this.durabilityConsumer = durabilityConsumer;
        this.protectionConsumer = protectionConsumer;
    }

    public int getBaseDurability() {
        return baseDurability;
    }

    public float getSpeedModifier() {
        return speedModifier;
    }

    public float calculateRemainingDurability(float damageAmount, float maxDurability) {
        return durabilityConsumer.apply(damageAmount, maxDurability);
    }

    public float calculateEffectiveProtection(float currentDurability, float maxDurability, float baseProtection) {
        float damagePercent = 1 - (currentDurability / maxDurability);
        return protectionConsumer.apply(damagePercent, baseProtection);
    }
}