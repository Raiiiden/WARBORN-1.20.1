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
    // Replace all the MaterialType.register() calls with these halved values:

    public static final MaterialType SOFT_KEVLAR = register("soft_kevlar", KEY_PREFIX_MATERIAL + "soft_kevlar", 51, -0.015f,
            (durability, damage) -> durability - damage,
            (damagePercent, baseProtection) -> baseProtection,
            Color.SOFT_KEVLAR
    );

    public static final MaterialType STEEL = register("steel", KEY_PREFIX_MATERIAL + "steel", 90, -0.075f,
            (durability, damage) -> durability - damage,
            (damagePercent, baseProtection) -> baseProtection,
            Color.STEEL
    );

    public static final MaterialType CERAMIC = register("ceramic", KEY_PREFIX_MATERIAL + "ceramic", 61, -0.05f,
            (durability, damage) -> durability - damage,
            (damagePercent, baseProtection) -> baseProtection,
            Color.CERAMIC
    );

    public static final MaterialType POLYETHYLENE = register("polyethylene", KEY_PREFIX_MATERIAL + "polyethylene", 75, -0.025f,
            (durability, damage) -> durability - damage,
            (damagePercent, baseProtection) -> baseProtection,
            Color.POLYETHYLENE
    );

    public static final MaterialType COMPOSITE = register("composite", KEY_PREFIX_MATERIAL + "composite", 124, -0.06f,
            (durability, damage) -> durability - damage,
            (damagePercent, baseProtection) -> baseProtection,
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