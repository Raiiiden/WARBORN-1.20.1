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

    // Existing Rus Armor
    public static final RegistryObject<WarbornArmorItem> RUS_HELMET = ITEMS.register("rus_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "warborn"));

    public static final RegistryObject<WarbornArmorItem> RUS_CHESTPLATE = ITEMS.register("rus_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "warborn"));

    // New RusBron Armor
    public static final RegistryObject<WarbornArmorItem> RUSBRON_HELMET = ITEMS.register("rusbron_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "rusbron"));

    public static final RegistryObject<WarbornArmorItem> RUSBRON_CHESTPLATE = ITEMS.register("rusbron_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "rusbron"));

    // New RusBron Rof Liko Armor
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
                    })
                    .build()
    );
}