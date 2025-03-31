package com.raiiiden.warborn;

import com.raiiiden.warborn.common.object.BackpackMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.world.inventory.MenuType;


public class ModRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "warborn");

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WARBORN.MODID);

    //backpack menu
    public static final RegistryObject<MenuType<BackpackMenu>> BACKPACK_MENU =
            MENU_TYPES.register("backpack", () -> IForgeMenuType.create(BackpackMenu::new));

//RU Armor
    public static final RegistryObject<WarbornArmorItem> RU_HELMET = ITEMS.register("ru_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "shturmovik_ru"));
    public static final RegistryObject<WarbornArmorItem> SHTURMOVIK_RU_CHESTPLATE = ITEMS.register("shturmovik_ru_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "shturmovik_ru"));
    public static final RegistryObject<WarbornArmorItem> SHTURMOVIKV2_CHESTPLATE = ITEMS.register("shturmovikv2_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "shturmovikv2"));
    public static final RegistryObject<WarbornArmorItem> RAZVETCHIK_CHESTPLATE = ITEMS.register("razvetchik_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "razvetchik"));
    public static final RegistryObject<WarbornArmorItem> MASHINEGUNNER_RU_CHESTPLATE = ITEMS.register("mashinegunner_ru_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "mashinegunner_ru"));
    public static final RegistryObject<WarbornArmorItem> RU_SHOULDERPADS = ITEMS.register("ru_shoulderpads",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "mashinegunner_ru_shoulderpads"));
    public static final RegistryObject<WarbornArmorItem> SQUAD_LIDER_RU_CHESTPLATE = ITEMS.register("squad_lider_ru_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "squad_lider_ru"));
    public static final RegistryObject<WarbornArmorItem> SQUAD_LIDER_RU_BACKPACK = ITEMS.register("squad_lider_ru_backpack",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "squad_lider_ru_backpack"));
    public static final RegistryObject<WarbornArmorItem> SHTURMOVIK_RU_BACKPACK = ITEMS.register("shturmovik_ru_backpack",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "shturmovik_ru_backpack"));
//nato
    public static final RegistryObject<WarbornArmorItem> NATO_SQAD_LEADER_HELMET = ITEMS.register("nato_sqad_leader_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "nato_sqad_leader"));
    public static final RegistryObject<WarbornArmorItem> NATO_SQAD_LEADER_CHESTPLATE = ITEMS.register("nato_sqad_leader_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_sqad_leader"));
    public static final RegistryObject<WarbornArmorItem> NATO_SQAD_LEADER_BACKPACK = ITEMS.register("nato_sqad_leader_backpack",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "nato_sqad_leader_backpack"));
    public static final RegistryObject<WarbornArmorItem> NATO_SHOULDERPADS = ITEMS.register("nato_shoulderpads",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "nato_sqad_leader_shoulderpads"));
    public static final RegistryObject<WarbornArmorItem> NATO_UKR_HELMET = ITEMS.register("nato_ukr_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "nato_ukr"));
    public static final RegistryObject<WarbornArmorItem> NATO_UKR_CHESTPLATE = ITEMS.register("nato_ukr_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_ukr"));
    public static final RegistryObject<WarbornArmorItem> NATO_SHTURMOVIK2_CHESTPLATE = ITEMS.register("nato_shturmovik2_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_shturmovik2"));
    public static final RegistryObject<WarbornArmorItem> NATO_HELMET = ITEMS.register("nato_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "nato_shturmovik"));
    public static final RegistryObject<WarbornArmorItem> NATO_SHTURMOVIK_CHESTPLATE = ITEMS.register("nato_shturmovik_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_shturmovik"));
    public static final RegistryObject<WarbornArmorItem> NATO_MG_CHESTPLATE = ITEMS.register("nato_mg_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_mg"));
//nato woodland
    public static final RegistryObject<WarbornArmorItem> NATO_SQAD_LEADER_HELMET_WOODLAND = ITEMS.register("nato_sqad_leader_helmet_woodland",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "nato_sqad_leader_woodland"));
    public static final RegistryObject<WarbornArmorItem> NATO_SQAD_LEADER_CHESTPLATE_WOODLAND = ITEMS.register("nato_sqad_leader_chestplate_woodland",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_sqad_leader_woodland"));
    public static final RegistryObject<WarbornArmorItem> NATO_SQAD_LEADER_BACKPACK_WOODLAND = ITEMS.register("nato_sqad_leader_backpack_woodland",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "nato_sqad_leader_backpack_woodland"));
    public static final RegistryObject<WarbornArmorItem> NATO_SHOULDERPADS_WOODLAND = ITEMS.register("nato_shoulderpads_woodland",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "nato_sqad_leader_shoulderpads_woodland"));
    public static final RegistryObject<WarbornArmorItem> NATO_UKR_HELMET_WOODLAND = ITEMS.register("nato_ukr_helmet_woodland",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "nato_ukr_woodland"));
    public static final RegistryObject<WarbornArmorItem> NATO_UKR_CHESTPLATE_WOODLAND = ITEMS.register("nato_ukr_chestplate_woodland",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_ukr_woodland"));
    public static final RegistryObject<WarbornArmorItem> NATO_SHTURMOVIK2_CHESTPLATE_WOODLAND = ITEMS.register("nato_shturmovik2_chestplate_woodland",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_shturmovik2_woodland"));
    public static final RegistryObject<WarbornArmorItem> NATO_HELMET_WOODLAND = ITEMS.register("nato_helmet_woodland",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties(), "nato_shturmovik_woodland"));
    public static final RegistryObject<WarbornArmorItem> NATO_SHTURMOVIK_CHESTPLATE_WOODLAND = ITEMS.register("nato_shturmovik_chestplate_woodland",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_shturmovik_woodland"));
    public static final RegistryObject<WarbornArmorItem> NATO_MG_CHESTPLATE_WOODLAND = ITEMS.register("nato_mg_chestplate_woodland",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_mg_woodland"));
//creative tab
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WARBORN.MODID);

    public static final RegistryObject<CreativeModeTab> WARBORN_TAB = CREATIVE_MODE_TABS.register("warborn_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.warborn_tab"))
                    .icon(() -> new ItemStack(RU_HELMET.get()))
                    .displayItems((enabledFeatures, entries) -> {
                        //ru
                        entries.accept(RU_HELMET.get());
                        entries.accept(SHTURMOVIK_RU_CHESTPLATE.get());
                        entries.accept(SHTURMOVIK_RU_BACKPACK.get());
                        entries.accept(RU_SHOULDERPADS.get());
                        entries.accept(SHTURMOVIKV2_CHESTPLATE.get());
                        entries.accept(RAZVETCHIK_CHESTPLATE.get());
                        entries.accept(MASHINEGUNNER_RU_CHESTPLATE.get());
                        entries.accept(SQUAD_LIDER_RU_CHESTPLATE.get());
                        entries.accept(SQUAD_LIDER_RU_BACKPACK.get());
                        //nato
                        entries.accept(NATO_SQAD_LEADER_HELMET.get());
                        entries.accept(NATO_SQAD_LEADER_CHESTPLATE.get());
                        entries.accept(NATO_SQAD_LEADER_BACKPACK.get());
                        entries.accept(NATO_SHOULDERPADS.get());
                        entries.accept(NATO_UKR_HELMET.get());
                        entries.accept(NATO_UKR_CHESTPLATE.get());
                        entries.accept(NATO_SHTURMOVIK2_CHESTPLATE.get());
                        entries.accept(NATO_HELMET.get());
                        entries.accept(NATO_SHTURMOVIK_CHESTPLATE.get());
                        entries.accept(NATO_MG_CHESTPLATE.get());
                        //nato woodland
                        entries.accept(NATO_SQAD_LEADER_HELMET_WOODLAND.get());
                        entries.accept(NATO_SQAD_LEADER_CHESTPLATE_WOODLAND.get());
                        entries.accept(NATO_SQAD_LEADER_BACKPACK_WOODLAND.get());
                        entries.accept(NATO_SHOULDERPADS_WOODLAND.get());
                        entries.accept(NATO_UKR_HELMET_WOODLAND.get());
                        entries.accept(NATO_UKR_CHESTPLATE_WOODLAND.get());
                        entries.accept(NATO_SHTURMOVIK2_CHESTPLATE_WOODLAND.get());
                        entries.accept(NATO_HELMET_WOODLAND.get());
                        entries.accept(NATO_SHTURMOVIK_CHESTPLATE_WOODLAND.get());
                        entries.accept(NATO_MG_CHESTPLATE_WOODLAND.get());
                        //beta7
                    })
                    .build()
    );
}