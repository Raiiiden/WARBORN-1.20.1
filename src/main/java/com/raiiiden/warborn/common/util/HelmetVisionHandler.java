package com.raiiiden.warborn.common.util;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class HelmetVisionHandler {

    public static final Set<ResourceLocation> ALLOWED_HELMETS = Set.of(
            new ResourceLocation("warborn", "nato_pidr_sqad_leader_helmet"),
            new ResourceLocation("warborn", "nato_pidr_ukr_helmet")
    );

    public static boolean isAllowedHelmet(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && ALLOWED_HELMETS.contains(id);
    }
}
