package com.raiiiden.warborn.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.UUID;

// Armor for gear worn in a curios slot, re-derived from the material since Curios' default grants nothing; armor only, no toughness or knockback.
public final class CurioArmorAttributes {

    // The curios slots this mod's gear lives in. Membership here means the item is curios gear.
    private static final List<TagKey<Item>> CURIO_SLOTS = List.of(
            tag("uniform"), tag("mask"), tag("shoulderpads"), tag("backpack"), tag("back"));

    private CurioArmorAttributes() {}

    private static TagKey<Item> tag(String slot) {
        return ItemTags.create(new ResourceLocation("curios", slot));
    }

    // True for gear whose only real home is a curios slot, which would otherwise pay out armor twice and show two modifier blocks in the tooltip.
    public static boolean isCuriosGear(ItemStack stack) {
        for (TagKey<Item> slot : CURIO_SLOTS) {
            if (stack.is(slot)) return true;
        }
        return false;
    }

    public static Multimap<Attribute, AttributeModifier> forSlot(ArmorItem item, UUID uuid, String identifier) {
        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        int defense = item.getDefense();

        if (defense != 0) {
            modifiers.put(Attributes.ARMOR, new AttributeModifier(
                    uuid, "warborn:curio_" + identifier, defense, AttributeModifier.Operation.ADDITION));
        }
        return modifiers;
    }
}
