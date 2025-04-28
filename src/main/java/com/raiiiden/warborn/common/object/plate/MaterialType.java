package com.raiiiden.warborn.common.object.plate;

import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.util.Color;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class MaterialType {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, MaterialType> REGISTRY = new HashMap<>();
    private static final String KEY_PREFIX_MATERIAL = "material.warborn.";

    public static final MaterialType DEFAULT = register("default", KEY_PREFIX_MATERIAL + "default", 0, 0.0f,
            (durability, damage) -> durability - damage,
            (damagePercent, baseProtection) -> baseProtection,
            Color.DEFAULT
    );
    public static final MaterialType SOFT_KEVLAR = register("soft_kevlar", KEY_PREFIX_MATERIAL + "soft_kevlar", 220, 0.05f,
            (durability, damage) -> durability - damage,
            (damagePercent, baseProtection) -> baseProtection * (1 - (damagePercent * 0.8f)),
            Color.SOFT_KEVLAR
    );
    public static final MaterialType STEEL = register("steel", KEY_PREFIX_MATERIAL + "steel", 750, -0.15f,
            (durability, damage) -> durability - (damage * 0.1f),
            (damagePercent, baseProtection) -> damagePercent < 0.7f
                    ? baseProtection
                    : baseProtection * (1 - ((damagePercent - 0.7f) * 3.0f)),
            Color.STEEL
    );
    public static final MaterialType CERAMIC = register("ceramic", KEY_PREFIX_MATERIAL + "ceramic", 600, -0.10f,
            (durability, damage) -> {
                float damagePercent = 1 - (durability / 600f);
                return durability - (damage * (1.0f + damagePercent * 0.5f));
            },
            (damagePercent, baseProtection) -> {
                if (damagePercent < 0.3f) return baseProtection;
                else if (damagePercent < 0.5f) return baseProtection * 0.8f;
                else if (damagePercent < 0.7f) return baseProtection * 0.5f;
                else return baseProtection * 0.2f;
            },
            Color.CERAMIC
    );
    public static final MaterialType POLYETHYLENE = register("polyethylene", KEY_PREFIX_MATERIAL + "polyethylene", 500, -0.05f,
            (durability, damage) -> {
                float damagePercent = 1 - (durability / 500f);
                float multiplier = damagePercent < 0.4f ? 0.7f : 1.2f;
                return durability - (damage * multiplier);
            },
            (damagePercent, baseProtection) -> {
                if (damagePercent < 0.3f) return baseProtection;
                else if (damagePercent < 0.5f) return baseProtection * 0.9f;
                else if (damagePercent < 0.7f) return baseProtection * 0.7f;
                else return baseProtection * 0.5f;
            },
            Color.POLYETHYLENE
    );
    public static final MaterialType COMPOSITE = register("composite", KEY_PREFIX_MATERIAL + "composite", 1200, -0.12f,
            (durability, damage) -> {
                float percent = 1 - (durability / 1200f);
                if (percent < 0.3f) return durability - (damage * 1.2f);
                else if (percent < 0.7f) return durability - (damage * 0.6f);
                else return durability - (damage * 0.3f);
            },
            (damagePercent, baseProtection) -> {
                if (damagePercent < 0.3f) return baseProtection;
                else if (damagePercent < 0.7f) return baseProtection * (1 - ((damagePercent - 0.3f) * 0.25f));
                else return Math.max(0.4f, baseProtection * (0.85f - ((damagePercent - 0.7f) * 1.5f)));
            },
            Color.COMPOSITE
    );

    private final String internalName;
    private final String translationKey;
    private final int baseDurability;
    private final float speedModifier;
    private final BiFunction<Float, Float, Float> durabilityConsumer;
    private final BiFunction<Float, Float, Float> protectionConsumer;
    private final Color color;

    // Updated Constructor
    private MaterialType(String internalName, String translationKey, int baseDurability, float speedModifier,
                         BiFunction<Float, Float, Float> durabilityConsumer,
                         BiFunction<Float, Float, Float> protectionConsumer,
                         Color color) {
        this.internalName = internalName;
        this.translationKey = translationKey;
        this.baseDurability = baseDurability;
        this.speedModifier = speedModifier;
        this.durabilityConsumer = durabilityConsumer;
        this.protectionConsumer = protectionConsumer;
        this.color = color;
    }

    public static MaterialType valueOf(String name) {
        MaterialType material = REGISTRY.get(name.toLowerCase());
        if (material == null) {
            LOGGER.warn("Null material: {}, defaulting to STEEL", name);
            return STEEL;
        }
        return material;
    }

    public static MaterialType register(String internalName, String translationKey, int baseDurability, float speedModifier,
                                        BiFunction<Float, Float, Float> durabilityConsumer,
                                        BiFunction<Float, Float, Float> protectionConsumer,
                                        Color color) {
        String key = internalName.toLowerCase();
        if (REGISTRY.containsKey(key)) {
            throw new IllegalArgumentException("Already registered material with internal name: " + internalName + ", dummy...");
        }

        MaterialType material = new MaterialType(internalName, translationKey, baseDurability, speedModifier,
                durabilityConsumer, protectionConsumer, color);
        REGISTRY.put(key, material);
        return material;
    }

    public int getBaseDurability() {
        return baseDurability;
    }

    public float getSpeedModifier() {
        return speedModifier;
    }


    /**
     * Gets the internal registry name (e.g., "soft_kevlar").
     * Use getDisplayName() for user-facing text.
     * @return The internal name.
     */
    public String getInternalName() {
        return internalName;
    }

    /**
     * Gets the translatable component representing the user-facing name of this material.
     * @return A translatable component.
     */
    public Component getDisplayName() {
        Component textComponent = Component.translatable(this.translationKey);
        int rgbColor = this.color.getRGB();
        return textComponent.copy().withStyle(style -> style.withColor(TextColor.fromRgb(rgbColor)));
    }

    /**
     * Deprecated for external use if display name is needed. Use getInternalName() for logic/serialization.
     * @deprecated Use {@link #getDisplayName()} for display or {@link #getInternalName()} for registry key.
     */
    @Deprecated
    public String name() {
        return internalName.toUpperCase();
    }

    public Color getColor() {
        return color;
    }

    public float calculateRemainingDurability(float currentDurability, float damageAmount) {
        return durabilityConsumer.apply(currentDurability, damageAmount);
    }

    public float calculateEffectiveProtection(float currentDurability, float maxDurability, float baseProtection) {
        float damagePercent = 1 - (currentDurability / maxDurability);
        return protectionConsumer.apply(damagePercent, baseProtection);
    }
}