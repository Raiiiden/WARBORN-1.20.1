package com.raiiiden.warborn.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;

@Mixin(GunHudOverlay.class)
public interface GunHudOverlayAccessor {
    @Accessor("cacheInventoryAmmoCount")
    static int getCacheInventoryAmmoCount() {
        throw new AssertionError();
    }

    @Accessor("cacheInventoryAmmoCount")
    static void setCacheInventoryAmmoCount(int value) {
        throw new AssertionError();
    }
}
