package com.raiiiden.warborn.common.object.plate;

import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.util.Color;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ProtectionTier {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, ProtectionTier> REGISTRY = new HashMap<>();
    private static final String KEY_PREFIX_TIER = "tier.warborn.";

    // Updated Registration Calls with Translation Keys
    public static final ProtectionTier DEFAULT = register("default", KEY_PREFIX_TIER + "default",
            0, 0, 0.00f, 0.00f, 0.00f, Color.DEFAULT);
    public static final ProtectionTier LEVEL_IIA = register("level_iia", KEY_PREFIX_TIER + "level_iia",
            1, 3, 0.70f, 0.20f, 0.05f, Color.TIER_IIA);
    public static final ProtectionTier LEVEL_II = register("level_ii", KEY_PREFIX_TIER + "level_ii",
            3, 5, 0.80f, 0.35f, 0.12f, Color.TIER_II);
    public static final ProtectionTier LEVEL_IIIA = register("level_iiia", KEY_PREFIX_TIER + "level_iiia",
            5, 7, 0.88f, 0.60f, 0.22f, Color.TIER_IIIA);
    public static final ProtectionTier LEVEL_III = register("level_iii", KEY_PREFIX_TIER + "level_iii",
            7, 10, 0.95f, 0.80f, 0.40f, Color.TIER_III);
    public static final ProtectionTier LEVEL_IV = register("level_iv", KEY_PREFIX_TIER + "level_iv",
            10, 15, 1.00f, 0.95f, 0.50f, Color.TIER_IV);

    private final String internalName; // Renamed from 'name'
    private final String translationKey; // Added field
    private final int lowerThreshold;
    private final int upperThreshold;
    private final float thresholdProtection;
    private final float partialProtection;
    private final float minimalProtection;
    private final Color color; // Your custom Color class

    // Updated Constructor
    private ProtectionTier(String internalName, String translationKey, int lowerThreshold, int upperThreshold,
                           float thresholdProtection, float partialProtection,
                           float minimalProtection, Color color) {
        this.internalName = internalName;
        this.translationKey = translationKey; // Assign translation key
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
        this.thresholdProtection = thresholdProtection;
        this.partialProtection = partialProtection;
        this.minimalProtection = minimalProtection;
        this.color = color;
    }


    public static ProtectionTier valueOf(String name) {
        ProtectionTier tier = REGISTRY.get(name.toLowerCase());
        if (tier == null) {
            // Defaulting might hide issues, consider logging more or throwing an error
            LOGGER.warn("Unknown ProtectionTier name: {}, defaulting to LEVEL_III", name);
            return LEVEL_III;
        }
        return tier;
    }

    public static ProtectionTier register(String internalName, String translationKey, int lowerThreshold, int upperThreshold,
                                          float thresholdProtection, float partialProtection,
                                          float minimalProtection, Color color) {
        String key = internalName.toLowerCase();
        if (REGISTRY.containsKey(key)) {
            throw new IllegalArgumentException("Already registered tier with internal name: " + internalName);
        }

        // Pass translationKey to the constructor
        ProtectionTier tier = new ProtectionTier(internalName, translationKey, lowerThreshold, upperThreshold,
                thresholdProtection, partialProtection, minimalProtection, color);
        REGISTRY.put(key, tier);
        return tier;
    }

    /**
     * Gets the internal registry name (e.g., "level_iia").
     * Use getDisplayName() for user-facing text.
     * @return The internal name.
     */
    public String getInternalName() {
        return internalName;
    }

    /**
     * Deprecated for external use if display name is needed. Use getInternalName() for logic/serialization.
     * @deprecated Use {@link #getDisplayName()} for display or {@link #getInternalName()} for registry key.
     */
    @Deprecated
    public String name() {
        return internalName.toUpperCase();
    }

    // Updated getDisplayName to use translation key and apply color
    public Component getDisplayName() {
        // Create the translatable component
        Component textComponent = Component.translatable(this.translationKey);
        int rgbColor = this.color.getRGB();
        return textComponent.copy().withStyle(style -> style.withColor(TextColor.fromRgb(rgbColor)));
    }

    public Color getColor() {
        return color;
    }

    public int getLowerThreshold() {
        return lowerThreshold;
    }

    public int getUpperThreshold() {
        return upperThreshold;
    }

    public float getThresholdProtection() {
        return thresholdProtection;
    }

    public float getPartialProtection() {
        return partialProtection;
    }

    public float getMinimalProtection() {
        return minimalProtection;
    }
}