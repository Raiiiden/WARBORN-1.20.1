package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.init.ModSoundEvents;
import net.minecraft.Util;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.function.Supplier;

public class Materials {
    public static final WarbornArmorMaterial WARBORN_ARMOR = new WarbornArmorMaterial(
            "fracturepoint",
            40,
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 3);
                map.put(ArmorItem.Type.LEGGINGS, 6);
                map.put(ArmorItem.Type.CHESTPLATE, 8);
                map.put(ArmorItem.Type.HELMET, 3);
            }),
            15,
            ModSoundEvents.WARBORN_ARMOR_EQUIP,
            ModSoundEvents.WARBORN_ARMOR_BREAK,
            3.0F,
            0.1F,
            () -> Ingredient.of(Items.NETHERITE_INGOT)
    );

    public static final WarbornArmorMaterial WARBORN_PLATE = new WarbornArmorMaterial(
            "warborn_plate",
            40,
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.LEGGINGS, 3);
                map.put(ArmorItem.Type.CHESTPLATE, 6);
                map.put(ArmorItem.Type.HELMET, 3);
                map.put(ArmorItem.Type.BOOTS, 3);
            }),
            15,
            ModSoundEvents.WARBORN_ARMOR_EQUIP,
            ModSoundEvents.WARBORN_ARMOR_BREAK,
            3.0F,
            0.1F,
            () -> Ingredient.of(Items.NETHERITE_INGOT)
    );

    public static final WarbornArmorMaterial WARBORN_SHOULDERPADS = new WarbornArmorMaterial(
            "warborn_shoulderpads",
            40,
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 0);
                map.put(ArmorItem.Type.LEGGINGS, 0);
                map.put(ArmorItem.Type.CHESTPLATE, 4);
                map.put(ArmorItem.Type.HELMET, 0);
            }),
            15,
            ModSoundEvents.WARBORN_ARMOR_EQUIP,
            ModSoundEvents.WARBORN_ARMOR_BREAK,
            3.0F,
            0.1F,
            () -> Ingredient.of(Items.NETHERITE_INGOT)
    );
    public static final WarbornArmorMaterial WARBORN_UNIFORM = new WarbornArmorMaterial(
            "warborn_uniform",
            40,
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 0);
                map.put(ArmorItem.Type.LEGGINGS, 0);
                map.put(ArmorItem.Type.CHESTPLATE, 1);
                map.put(ArmorItem.Type.HELMET, 0);
            }),
            15,
            ModSoundEvents.WARBORN_ARMOR_EQUIP,
            ModSoundEvents.WARBORN_ARMOR_BREAK,
            3.0F,
            0.1F,
            () -> Ingredient.of(Items.NETHERITE_INGOT)
    );

    public static class WarbornArmorMaterial implements ArmorMaterial {
        private static final EnumMap<ArmorItem.Type, Integer> HEALTH_FUNCTION_FOR_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, 13);
            map.put(ArmorItem.Type.LEGGINGS, 15);
            map.put(ArmorItem.Type.CHESTPLATE, 16);
            map.put(ArmorItem.Type.HELMET, 11);
        });

        private final String name;
        private final int durabilityMultiplier;
        private final EnumMap<ArmorItem.Type, Integer> protectionFunctionForType;
        private final int enchantmentValue;
        private final Supplier<SoundEvent> sound;
        private final Supplier<SoundEvent> breakSound;
        private final float toughness;
        private final float knockbackResistance;
        private final LazyLoadedValue<Ingredient> repairIngredient;

        public WarbornArmorMaterial(String name, int durabilityMultiplier, EnumMap<ArmorItem.Type, Integer> protectionFunctionForType,
                                    int enchantmentValue, Supplier<SoundEvent> sound, Supplier<SoundEvent> breakSound, float toughness, float knockbackResistance,
                                    Supplier<Ingredient> repairIngredient) {
            this.name = name;
            this.durabilityMultiplier = durabilityMultiplier;
            this.protectionFunctionForType = protectionFunctionForType;
            this.enchantmentValue = enchantmentValue;
            this.sound = sound;
            this.breakSound = breakSound;
            this.toughness = toughness;
            this.knockbackResistance = knockbackResistance;
            this.repairIngredient = new LazyLoadedValue<>(repairIngredient);
        }

        @Override
        public int getDurabilityForType(ArmorItem.Type type) {
            return HEALTH_FUNCTION_FOR_TYPE.get(type) * this.durabilityMultiplier;
        }

        @Override
        public int getDefenseForType(ArmorItem.Type type) {
            return this.protectionFunctionForType.get(type);
        }

        @Override
        public int getEnchantmentValue() {
            return this.enchantmentValue;
        }

        @Override
        public SoundEvent getEquipSound() {
            return this.sound.get();
        }

        public SoundEvent getBreakSound() {
            return this.breakSound.get();
        }

        @Override
        public Ingredient getRepairIngredient() {
            return this.repairIngredient.get();
        }

        @Override
        public String getName() {
            return WARBORN.MODID + ":" + this.name;
        }

        @Override
        public float getToughness() {
            return this.toughness;
        }

        @Override
        public float getKnockbackResistance() {
            return this.knockbackResistance;
        }
    }
}