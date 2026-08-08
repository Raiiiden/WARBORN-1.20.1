package com.raiiiden.warborn.client.icon;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// Lets the icon system drive our GeoArmorRenderers without an entity, since prepForRender bails when the entity or base model is null.
public interface IconPreparable {
    void prepForIcon(ItemStack stack, EquipmentSlot slot);

    // Animation-state key for a piece being drawn as an icon.
    //
    // GeckoLib 4.8 changed getInstanceId: it now keys on currentEntity.getId() whenever the
    // stack carries no id of its own, and a freshly built ItemStack never does. Icon mode has
    // no entity, so that call NPEs, measuring fails, and the slot renders nothing at all.
    // 4.7 derived the id from the stack alone and never touched the entity, which is why this
    // only appeared on the newer GeckoLib.
    //
    // Negative so it cannot collide with the positive ids GeckoLib assigns to real stacks, and
    // per item so two pieces never share animation state.
    static long iconInstanceId(Item item) {
        return -1L - BuiltInRegistries.ITEM.getId(item);
    }
}
