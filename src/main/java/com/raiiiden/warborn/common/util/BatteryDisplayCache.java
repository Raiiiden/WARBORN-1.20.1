package com.raiiiden.warborn.common.util;

/**
 * Lightweight client-side cache for the live battery energy while NVG is active.
 * Populated by ClientboundBatteryUpdatePacket; -1 means NVG is inactive, fall back to item NBT.
 * Safe to reference from common code — contains no client-only imports.
 */
public class BatteryDisplayCache {

    private static int energy = -1;

    public static void set(int e)  { energy = e; }
    public static void clear()     { energy = -1; }
    public static int  get()       { return energy; }
    public static boolean isActive() { return energy >= 0; }
}
