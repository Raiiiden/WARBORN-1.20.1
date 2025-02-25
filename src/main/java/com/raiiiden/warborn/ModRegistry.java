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

public class ModRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WARBORN.MODID);

    public static final RegistryObject<WarbornArmorItem> RUS_HELMET = ITEMS.register("rus_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "rus_armor"));

    public static final RegistryObject<WarbornArmorItem> RUS_CHESTPLATE = ITEMS.register("rus_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "rus_armor"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_HELMET = ITEMS.register("rusbron_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "rusbron"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_CHESTPLATE = ITEMS.register("rusbron_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "rusbron"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_ROF_LIKO_HELMET = ITEMS.register("rusbron_rof_liko_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "rusbron_rof_liko"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_ROF_LIKO_CHESTPLATE = ITEMS.register("rusbron_rof_liko_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "rusbron_rof_liko"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_FOR_LIKO_HELMET = ITEMS.register("rusbron_for_liko_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "rusbron_for_liko"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_FOR_LIKO_CHESTPLATE = ITEMS.register("rusbron_for_liko_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "rusbron_for_liko"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_PULEMETCHIK_HELMET = ITEMS.register("rusbron_pulemetchik_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "rusbron_pulemetchik"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_PULEMETCHIK_CHESTPLATE = ITEMS.register("rusbron_pulemetchik_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "rusbron_pulemetchik"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_PULEMETCHIK_DISGUISE_HELMET = ITEMS.register("rusbron_pulemetchik_disguise_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "rusbron_pulemetchik_disguise"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_PULEMETCHIK_DISGUISE_CHESTPLATE = ITEMS.register("rusbron_pulemetchik_disguise_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "rusbron_pulemetchik_disguise"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_PULEMETCHIK_NET_NAPLEKHNIKOV_CHESTPLATE = ITEMS.register("rusbron_pulemetchik_net_naplekhnikov_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "rusbron_pulemetchik_net_naplekhnikov"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_SHTURMOVIK_CHESTPLATE = ITEMS.register("rusbron_shturmovik_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "rusbron_shturmovik"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_SHTURMOVIK_DISGUISE_HELMET = ITEMS.register("rusbron_shturmovik_disguise_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "rusbron_shturmovik_disguise"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_SHTURMOVIK_DISGUISE_CHESTPLATE = ITEMS.register("rusbron_shturmovik_disguise_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "rusbron_shturmovik_disguise"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_SHTURMOVIK_NET_NAPLEKHNIKOV_CHESTPLATE = ITEMS.register("rusbron_shturmovik_net_naplekhnikov_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "rusbron_shturmovik_net_naplekhnikov"));

    public static final RegistryObject<WarbornArmorItem> UKRBRON_PULEMETCHIK_HELMET = ITEMS.register("ukrbron_pulemetchik_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "ukrbron_pulemetchik"));

    public static final RegistryObject<WarbornArmorItem> UKRBRON_PULEMETCHIK_CHESTPLATE = ITEMS.register("ukrbron_pulemetchik_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "ukrbron_pulemetchik"));

    public static final RegistryObject<WarbornArmorItem> UKRBRON_PULEMETCHIK_LEGGINGS = ITEMS.register("ukrbron_pulemetchik_leggings",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, new Item.Properties(), "ukrbron_pulemetchik"));

    public static final RegistryObject<WarbornArmorItem> UKRBRON_PULEMETCHIK_DISGUISE_HELMET = ITEMS.register("ukrbron_pulemetchik_disguise_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "ukrbron_pulemetchik_disguise"));

    public static final RegistryObject<WarbornArmorItem> UKRBRON_PULEMETCHIK_DISGUISE_CHESTPLATE = ITEMS.register("ukrbron_pulemetchik_disguise_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "ukrbron_pulemetchik_disguise"));

    public static final RegistryObject<WarbornArmorItem> UKRBRON_PULEMETCHIK_DISGUISE_LEGGINGS = ITEMS.register("ukrbron_pulemetchik_disguise_leggings",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, new Item.Properties(), "ukrbron_pulemetchik_disguise"));

    public static final RegistryObject<WarbornArmorItem> UKRBRON_PULEMETCHIK_NET_NAPLEKHNIKOV_CHESTPLATE = ITEMS.register("ukrbron_pulemetchik_net_naplekhnikov_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "ukrbron_pulemetchik_net_naplekhnikov"));

    public static final RegistryObject<WarbornArmorItem> UKRBRON_HELMET = ITEMS.register("ukrbron_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "ukrbron"));

    public static final RegistryObject<WarbornArmorItem> UKRBRON_CHESTPLATE = ITEMS.register("ukrbron_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "ukrbron"));

    public static final RegistryObject<WarbornArmorItem> UKRBRON_LEGGINGS = ITEMS.register("ukrbron_leggings",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, new Item.Properties(), "ukrbron"));


    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WARBORN.MODID);

    public static final RegistryObject<CreativeModeTab> WARBORN_TAB = CREATIVE_MODE_TABS.register("warborn_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.warborn_tab"))
                    .icon(() -> new ItemStack(RUS_HELMET.get()))
                    .displayItems((enabledFeatures, entries) -> {
                        entries.accept(RUS_HELMET.get());
                        entries.accept(RUS_CHESTPLATE.get());
                        entries.accept(RUSBRON_HELMET.get());
                        entries.accept(RUSBRON_CHESTPLATE.get());
                        entries.accept(RUSBRON_ROF_LIKO_HELMET.get());
                        entries.accept(RUSBRON_ROF_LIKO_CHESTPLATE.get());
                        entries.accept(RUSBRON_FOR_LIKO_HELMET.get());
                        entries.accept(RUSBRON_FOR_LIKO_CHESTPLATE.get());
                        entries.accept(RUSBRON_PULEMETCHIK_HELMET.get());
                        entries.accept(RUSBRON_PULEMETCHIK_CHESTPLATE.get());
                        entries.accept(RUSBRON_PULEMETCHIK_DISGUISE_HELMET.get());
                        entries.accept(RUSBRON_PULEMETCHIK_DISGUISE_CHESTPLATE.get());
                        entries.accept(RUSBRON_PULEMETCHIK_NET_NAPLEKHNIKOV_CHESTPLATE.get());
                        entries.accept(RUSBRON_SHTURMOVIK_CHESTPLATE.get());
                        entries.accept(RUSBRON_SHTURMOVIK_DISGUISE_HELMET.get());
                        entries.accept(RUSBRON_SHTURMOVIK_DISGUISE_CHESTPLATE.get());
                        entries.accept(RUSBRON_SHTURMOVIK_NET_NAPLEKHNIKOV_CHESTPLATE.get());
                        entries.accept(UKRBRON_PULEMETCHIK_HELMET.get());
                        entries.accept(UKRBRON_PULEMETCHIK_CHESTPLATE.get());
                        entries.accept(UKRBRON_PULEMETCHIK_LEGGINGS.get());
                        entries.accept(UKRBRON_PULEMETCHIK_DISGUISE_HELMET.get());
                        entries.accept(UKRBRON_PULEMETCHIK_DISGUISE_CHESTPLATE.get());
                        entries.accept(UKRBRON_PULEMETCHIK_DISGUISE_LEGGINGS.get());
                        entries.accept(UKRBRON_PULEMETCHIK_NET_NAPLEKHNIKOV_CHESTPLATE.get());
                        entries.accept(UKRBRON_HELMET.get());
                        entries.accept(UKRBRON_CHESTPLATE.get());
                        entries.accept(UKRBRON_LEGGINGS.get());
                    })
                    .build()
    );
}