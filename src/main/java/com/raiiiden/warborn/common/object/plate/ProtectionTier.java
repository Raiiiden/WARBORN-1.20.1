package com.raiiiden.warborn.common.object.plate;

public enum ProtectionTier {
    LEVEL_IIA(1, 3, 0.70f, 0.20f, 0.05f),
    LEVEL_II(3, 5, 0.80f, 0.35f, 0.12f),
    LEVEL_IIIA(5, 7, 0.88f, 0.60f, 0.22f),
    LEVEL_III(7, 10, 0.95f, 0.80f, 0.40f),
    LEVEL_IV(10, 15, 1.00f, 0.95f, 0.50f);
    
    private final int lowerThreshold;
    private final int upperThreshold;
    private final float thresholdProtection;
    private final float partialProtection;
    private final float minimalProtection;
    
    ProtectionTier(int lowerThreshold, int upperThreshold, 
                  float thresholdProtection, float partialProtection, 
                  float minimalProtection) {
        this.lowerThreshold = lowerThreshold;
        this.upperThreshold = upperThreshold;
        this.thresholdProtection = thresholdProtection;
        this.partialProtection = partialProtection;
        this.minimalProtection = minimalProtection;
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