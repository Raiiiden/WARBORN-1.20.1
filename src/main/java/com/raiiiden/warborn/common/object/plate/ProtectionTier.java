package com.raiiiden.warborn.common.object.plate;

import com.raiiiden.warborn.common.util.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProtectionTier {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, ProtectionTier> REGISTRY = new HashMap<>();
    
    private final String name;
    private final int lowerThreshold;
    private final int upperThreshold;
    private final float thresholdProtection;
    private final float partialProtection;
    private final float minimalProtection;
    private final Color color;

    public static final ProtectionTier LEVEL_IIA = register("level_iia", 
            1, 3, 0.70f, 0.20f, 0.05f, Color.TIER_IIA);
            
    public static final ProtectionTier LEVEL_II = register("level_ii", 
            3, 5, 0.80f, 0.35f, 0.12f, Color.TIER_II);
            
    public static final ProtectionTier LEVEL_IIIA = register("level_iiia", 
            5, 7, 0.88f, 0.60f, 0.22f, Color.TIER_IIIA);
            
    public static final ProtectionTier LEVEL_III = register("level_iii", 
            7, 10, 0.95f, 0.80f, 0.40f, Color.TIER_III);
            
    public static final ProtectionTier LEVEL_IV = register("level_iv",
            10, 15, 1.00f, 0.95f, 0.50f, Color.TIER_IV);

    private ProtectionTier(String name, int lowerThreshold, int upperThreshold,
                   float thresholdProtection, float partialProtection,
                   float minimalProtection, Color color) {
        this.name = name;
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
            LOGGER.warn("Null tier (how did we get here): {}, defaulting to LEVEL_III", name);
            return LEVEL_III;
        }
        return tier;
    }

    public static ProtectionTier register(String name, int lowerThreshold, int upperThreshold,
                                        float thresholdProtection, float partialProtection,
                                        float minimalProtection, Color color) {
        String key = name.toLowerCase();
        if (REGISTRY.containsKey(key)) {
            throw new IllegalArgumentException("Already registered tier: " + name);
        }
        
        ProtectionTier tier = new ProtectionTier(name, lowerThreshold, upperThreshold, 
                thresholdProtection, partialProtection, minimalProtection, color);
        REGISTRY.put(key, tier);
        return tier;
    }

    public String name() {
        return name.toUpperCase();
    }

    public Color getColor() {
        return color;
    }

    // we can adjust this later if we just want to have it display the tier
    public Component getDisplayName() {
        String formatted = name().replace("LEVEL_", "");
        return color.colorize("NIJ " + formatted);
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