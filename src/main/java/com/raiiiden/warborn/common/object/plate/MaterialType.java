package com.raiiiden.warborn.common.object.plate;

import com.raiiiden.warborn.common.util.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MaterialType {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, MaterialType> REGISTRY = new HashMap<>();

    private final String name;
    private final int baseDurability;
    private final float speedModifier;
    private final BiFunction<Float, Float, Float> durabilityConsumer;
    private final BiFunction<Float, Float, Float> protectionConsumer;
    private final Color color;

    public static final MaterialType SOFT_KEVLAR = register("soft_kevlar", 220, 0.05f,
            (durability, damage) -> durability - damage,
            (damagePercent, baseProtection) -> baseProtection * (1 - (damagePercent * 0.8f)),
            Color.SOFT_KEVLAR
    );

    public static final MaterialType STEEL = register("steel", 750, -0.15f,
            (durability, damage) -> durability - (damage * 0.1f),
            (damagePercent, baseProtection) -> damagePercent < 0.7f
                    ? baseProtection
                    : baseProtection * (1 - ((damagePercent - 0.7f) * 3.0f)),
            Color.STEEL
    );

    public static final MaterialType CERAMIC = register("ceramic", 600, -0.10f,
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

    //TOOD make a plate with this material
    public static final MaterialType POLYETHYLENE = register("polyethylene", 500, -0.05f,
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

    public static final MaterialType COMPOSITE = register("composite", 1200, -0.12f,
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

    private MaterialType(String name, int baseDurability, float speedModifier,
                 BiFunction<Float, Float, Float> durabilityConsumer,
                 BiFunction<Float, Float, Float> protectionConsumer,
                 Color color) {
        this.name = name;
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

    public static MaterialType register(String name, int baseDurability, float speedModifier,
                                      BiFunction<Float, Float, Float> durabilityConsumer,
                                      BiFunction<Float, Float, Float> protectionConsumer,
                                      Color color) {
        String key = name.toLowerCase();
        if (REGISTRY.containsKey(key)) {
            throw new IllegalArgumentException("Already registered material dummy: " + name);
        }
        
        MaterialType material = new MaterialType(name, baseDurability, speedModifier, 
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

    public String name() {
        return name.toUpperCase();
    }
    
    public Color getColor() {
        return color;
    }
    

    // maybe not needed for all the additional stuff but it's just qol
    public Component getDisplayName() {
        String formattedName = name.replace("_", " ");
        
        StringBuilder formattedBuilder = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : formattedName.toCharArray()) {
            if (c == ' ') {
                capitalizeNext = true;
                formattedBuilder.append(c);
            } else if (capitalizeNext) {
                formattedBuilder.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                formattedBuilder.append(Character.toLowerCase(c));
            }
        }
        
        return color.colorize(formattedBuilder.toString());
    }

    public float calculateRemainingDurability(float currentDurability, float damageAmount) {
        return durabilityConsumer.apply(currentDurability, damageAmount);
    }

    public float calculateEffectiveProtection(float currentDurability, float maxDurability, float baseProtection) {
        float damagePercent = 1 - (currentDurability / maxDurability);
        return protectionConsumer.apply(damagePercent, baseProtection);
    }
}