package com.raiiiden.warborn.common.util;

import com.raiiiden.warborn.common.config.WarbornCommonConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

public class HelmetVisionHandler {

    private static final String NVG_TAG = "CanUseNVG";

    public static boolean isAllowedHelmet(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;

        if (stack.hasTag() && stack.getTag().getBoolean(NVG_TAG)) {
            return true;
        }

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null && WarbornCommonConfig.HELMETS_WITH_NVG.get().contains(id.toString())) {
            stack.getOrCreateTag().putBoolean(NVG_TAG, true);
            return true;
        }

        return false;
    }
}
