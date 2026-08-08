package com.raiiiden.warborn.common.util;

// Client-side cache of live battery energy while NVG is active; -1 means inactive, fall back to item NBT. No client-only imports.
public class BatteryDisplayCache {

    private static int energy = -1;

    public static void set(int e)  { energy = e; }
    public static void clear()     { energy = -1; }
    public static int  get()       { return energy; }
    public static boolean isActive() { return energy >= 0; }
}
