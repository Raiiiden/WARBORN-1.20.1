package com.raiiiden.warborn.client.network;

import com.raiiiden.warborn.common.util.BatteryDisplayCache;

public class ClientBatteryUpdateHandler {

    public static void handle(int energy) {
        BatteryDisplayCache.set(energy);
    }

    public static void clear() {
        BatteryDisplayCache.clear();
    }

    public static int getCachedEnergy() {
        return BatteryDisplayCache.get();
    }
}
