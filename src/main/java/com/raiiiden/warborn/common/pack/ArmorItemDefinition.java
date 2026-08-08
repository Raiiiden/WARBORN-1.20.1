package com.raiiiden.warborn.common.pack;

import com.raiiiden.warborn.common.item.BackpackItem;
import com.raiiiden.warborn.common.item.Materials;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

// Immutable startup definition for an armor or wearable pack item.
public record ArmorItemDefinition(
        String id,
        Kind kind,
        Material material,
        ArmorItem.Type slot,
        String model,
        String texture,
        int armorModifier
) {
    public Item createItem() {
        Item.Properties properties = new Item.Properties();
        if (kind == Kind.BACKPACK || material == Material.SHOULDERPADS) {
            properties.stacksTo(1);
        }

        ArmorMaterial armorMaterial = material.resolve();
        if (armorModifier != 0) {
            armorMaterial = new ModifiedArmorMaterial(armorMaterial, slot, armorModifier);
        }
        return kind == Kind.BACKPACK
                ? new BackpackItem(armorMaterial, slot, properties, model, texture)
                : new WBArmorItem(armorMaterial, slot, properties, model, texture);
    }

    public enum Kind {
        ARMOR,
        BACKPACK
    }

    public enum Material {
        ARMOR,
        SHOULDERPADS,
        UNIFORM,
        HEADGEAR,
        BACKPACK;

        ArmorMaterial resolve() {
            return switch (this) {
                case ARMOR -> Materials.WARBORN_ARMOR;
                case SHOULDERPADS -> Materials.WARBORN_SHOULDERPADS;
                case UNIFORM -> Materials.WARBORN_UNIFORM;
                case HEADGEAR -> Materials.WARBORN_HEADGEAR;
                case BACKPACK -> Materials.WARBORN_BACKPACK;
            };
        }
    }

    // Delegates every material property except this item's slot-specific armor value.
    private record ModifiedArmorMaterial(
            ArmorMaterial delegate,
            ArmorItem.Type modifiedSlot,
            int modifier
    ) implements ArmorMaterial {
        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return delegate.getDurabilityForType(type);
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            int defense = delegate.getDefenseForType(type);
            return type == modifiedSlot ? defense + modifier : defense;
        }

        @Override
        public int getEnchantmentValue() {
            return delegate.getEnchantmentValue();
        }

        @Override
        public SoundEvent getEquipSound() {
            return delegate.getEquipSound();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return delegate.getRepairIngredient();
        }

        @Override
        public String getName() {
            return delegate.getName();
        }

        @Override
        public float getToughness() {
            return delegate.getToughness();
        }

        @Override
        public float getKnockbackResistance() {
            return delegate.getKnockbackResistance();
        }
    }
}
