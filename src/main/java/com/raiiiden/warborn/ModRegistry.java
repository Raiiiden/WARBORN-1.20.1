package com.raiiiden.warborn;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.world.inventory.MenuType;


public class ModRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "warborn");

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WARBORN.MODID);

    public static final RegistryObject<WarbornArmorItem> SHTURMOVIK_RU_HELMET = ITEMS.register("shturmovik_ru_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "shturmovik_ru"));
    public static final RegistryObject<WarbornArmorItem> SHTURMOVIK_RU_CHESTPLATE = ITEMS.register("shturmovik_ru_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "shturmovik_ru"));
    public static final RegistryObject<WarbornArmorItem> SHTURMOVIKV2_HELMET = ITEMS.register("shturmovikv2_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "shturmovikv2"));
    public static final RegistryObject<WarbornArmorItem> SHTURMOVIKV2_CHESTPLATE = ITEMS.register("shturmovikv2_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "shturmovikv2"));
    public static final RegistryObject<WarbornArmorItem> RAZVETCHIK_HELMET = ITEMS.register("razvetchik_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "razvetchik"));
    public static final RegistryObject<WarbornArmorItem> RAZVETCHIK_CHESTPLATE = ITEMS.register("razvetchik_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "razvetchik"));
    public static final RegistryObject<WarbornArmorItem> MASHINEGUNNER_RU_HELMET = ITEMS.register("mashinegunner_ru_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "mashinegunner_ru"));
    public static final RegistryObject<WarbornArmorItem> MASHINEGUNNER_RU_CHESTPLATE = ITEMS.register("mashinegunner_ru_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "mashinegunner_ru"));
    public static final RegistryObject<WarbornArmorItem> SQUAD_LIDER_RU_HELMET = ITEMS.register("squad_lider_ru_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "squad_lider_ru"));
    public static final RegistryObject<WarbornArmorItem> SQUAD_LIDER_RU_CHESTPLATE = ITEMS.register("squad_lider_ru_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "squad_lider_ru"));
    public static final RegistryObject<WarbornArmorItem> SQUAD_LIDER_RU_BACKPACK = ITEMS.register("squad_lider_ru_backpack",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "squad_lider_ru_backpack"));


    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WARBORN.MODID);

    public static final RegistryObject<CreativeModeTab> WARBORN_TAB = CREATIVE_MODE_TABS.register("warborn_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.warborn_tab"))
                    .icon(() -> new ItemStack(SHTURMOVIK_RU_HELMET.get()))
                    .displayItems((enabledFeatures, entries) -> {
                        entries.accept(SHTURMOVIK_RU_HELMET.get());
                        entries.accept(SHTURMOVIK_RU_CHESTPLATE.get());
                        entries.accept(SHTURMOVIKV2_HELMET.get());
                        entries.accept(SHTURMOVIKV2_CHESTPLATE.get());
                        entries.accept(RAZVETCHIK_HELMET.get());
                        entries.accept(RAZVETCHIK_CHESTPLATE.get());
                        entries.accept(MASHINEGUNNER_RU_HELMET.get());
                        entries.accept(MASHINEGUNNER_RU_CHESTPLATE.get());
                        entries.accept(SQUAD_LIDER_RU_HELMET.get());
                        entries.accept(SQUAD_LIDER_RU_CHESTPLATE.get());
                        entries.accept(SQUAD_LIDER_RU_BACKPACK.get());
                    })
                    .build()
    );
}