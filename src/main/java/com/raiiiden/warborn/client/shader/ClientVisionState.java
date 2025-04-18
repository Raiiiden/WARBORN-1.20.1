package com.raiiiden.warborn.client.shader;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ClientVisionState {
    private static final Map<UUID, String> ACTIVE_VISION = new HashMap<>();

    public static void setActive(UUID uuid, String visionType) {
        if (visionType == null || visionType.isEmpty()) {
            ACTIVE_VISION.remove(uuid);
        } else {
            ACTIVE_VISION.put(uuid, visionType);
        }
    }

    public static String getActive(UUID uuid) {
        return ACTIVE_VISION.getOrDefault(uuid, "");
    }

    public static boolean isActive(UUID uuid, String visionType) {
        return visionType.equals(getActive(uuid));
    }

    public static void clear(UUID uuid) {
        ACTIVE_VISION.remove(uuid);
    }
}
