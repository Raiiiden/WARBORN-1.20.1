package com.raiiiden.warborn.common.init;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.*;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;


public class ModItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WARBORN.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WARBORN.MODID);

    // Backpack upgrades
    public static final RegistryObject<BackpackUpgradeItem> BACKPACK_UPGRADE_TIER2 = ITEMS.register("backpack_upgrade_tier2",
            () -> new BackpackUpgradeItem(2, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BackpackUpgradeItem> BACKPACK_UPGRADE_TIER3 = ITEMS.register("backpack_upgrade_tier3",
            () -> new BackpackUpgradeItem(3, new Item.Properties().stacksTo(1)));

    //RU Armor
    public static final RegistryObject<WBArmorItem> RU_HELMET = ITEMS.register("ru_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "shturmovik_ru"));
    public static final RegistryObject<WBArmorItem> SHTURMOVIK_RU_CHESTPLATE = ITEMS.register("shturmovik_ru_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "shturmovik_ru"));
    public static final RegistryObject<WBArmorItem> SHTURMOVIKV2_CHESTPLATE = ITEMS.register("shturmovikv2_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "shturmovikv2"));
    public static final RegistryObject<WBArmorItem> RAZVETCHIK_CHESTPLATE = ITEMS.register("razvetchik_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "razvetchik"));
    public static final RegistryObject<WBArmorItem> MASHINEGUNNER_RU_CHESTPLATE = ITEMS.register("mashinegunner_ru_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "mashinegunner_ru"));
    public static final RegistryObject<WBArmorItem> RU_SHOULDERPADS = ITEMS.register("ru_shoulderpads",
            () -> new WBArmorItem(Materials.WARBORN_SHOULDERPADS, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "mashinegunner_ru_shoulderpads"));
    public static final RegistryObject<WBArmorItem> SQUAD_LIDER_RU_CHESTPLATE = ITEMS.register("squad_lider_ru_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "squad_lider_ru"));
    public static final RegistryObject<BackpackItem> SQUAD_LIDER_RU_BACKPACK = ITEMS.register("squad_lider_ru_backpack",
            () -> new BackpackItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "squad_lider_ru_backpack"));
    public static final RegistryObject<BackpackItem> SHTURMOVIK_RU_BACKPACK = ITEMS.register("shturmovik_ru_backpack",
            () -> new BackpackItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "shturmovik_ru_backpack"));

    //NATO
    public static final RegistryObject<WBArmorItem> NATO_SQAD_LEADER_HELMET = ITEMS.register("nato_sqad_leader_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "nato_sqad_leader"));
    public static final RegistryObject<WBArmorItem> NATO_SQAD_LEADER_CHESTPLATE = ITEMS.register("nato_sqad_leader_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_sqad_leader"));
    public static final RegistryObject<BackpackItem> NATO_SQAD_LEADER_BACKPACK = ITEMS.register("nato_sqad_leader_backpack",
            () -> new BackpackItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "nato_sqad_leader_backpack"));
    public static final RegistryObject<WBArmorItem> NATO_SHOULDERPADS = ITEMS.register("nato_shoulderpads",
            () -> new WBArmorItem(Materials.WARBORN_SHOULDERPADS, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "nato_sqad_leader_shoulderpads"));
    public static final RegistryObject<WBArmorItem> NATO_UKR_HELMET = ITEMS.register("nato_ukr_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "nato_ukr"));
    public static final RegistryObject<WBArmorItem> NATO_UKR_CHESTPLATE = ITEMS.register("nato_ukr_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_ukr"));
    public static final RegistryObject<WBArmorItem> NATO_SHTURMOVIK2_CHESTPLATE = ITEMS.register("nato_shturmovik2_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_shturmovik2"));
    public static final RegistryObject<WBArmorItem> NATO_HELMET = ITEMS.register("nato_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "nato_shturmovik"));
    public static final RegistryObject<WBArmorItem> NATO_SHTURMOVIK_CHESTPLATE = ITEMS.register("nato_shturmovik_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_shturmovik"));
    public static final RegistryObject<WBArmorItem> NATO_MG_CHESTPLATE = ITEMS.register("nato_mg_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_mg"));

    //NATO Woodland
    public static final RegistryObject<WBArmorItem> NATO_SQAD_LEADER_HELMET_WOODLAND = ITEMS.register("nato_sqad_leader_helmet_woodland",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "nato_sqad_leader_woodland"));
    public static final RegistryObject<WBArmorItem> NATO_SQAD_LEADER_CHESTPLATE_WOODLAND = ITEMS.register("nato_sqad_leader_chestplate_woodland",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_sqad_leader_woodland"));
    public static final RegistryObject<BackpackItem> NATO_SQAD_LEADER_BACKPACK_WOODLAND = ITEMS.register("nato_sqad_leader_backpack_woodland",
            () -> new BackpackItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "nato_sqad_leader_backpack_woodland"));
    public static final RegistryObject<WBArmorItem> NATO_SHOULDERPADS_WOODLAND = ITEMS.register("nato_shoulderpads_woodland",
            () -> new WBArmorItem(Materials.WARBORN_SHOULDERPADS, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "nato_sqad_leader_shoulderpads_woodland"));
    public static final RegistryObject<WBArmorItem> NATO_UKR_HELMET_WOODLAND = ITEMS.register("nato_ukr_helmet_woodland",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "nato_ukr_woodland"));
    public static final RegistryObject<WBArmorItem> NATO_UKR_CHESTPLATE_WOODLAND = ITEMS.register("nato_ukr_chestplate_woodland",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_ukr_woodland"));
    public static final RegistryObject<WBArmorItem> NATO_SHTURMOVIK2_CHESTPLATE_WOODLAND = ITEMS.register("nato_shturmovik2_chestplate_woodland",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_shturmovik2_woodland"));
    public static final RegistryObject<WBArmorItem> NATO_HELMET_WOODLAND = ITEMS.register("nato_helmet_woodland",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "nato_shturmovik_woodland"));
    public static final RegistryObject<WBArmorItem> NATO_SHTURMOVIK_CHESTPLATE_WOODLAND = ITEMS.register("nato_shturmovik_chestplate_woodland",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_shturmovik_woodland"));
    public static final RegistryObject<WBArmorItem> NATO_MG_CHESTPLATE_WOODLAND = ITEMS.register("nato_mg_chestplate_woodland",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "nato_mg_woodland"));

    //Beta7
    public static final RegistryObject<WBArmorItem> BETA7_NVG_HELMET = ITEMS.register("beta7_nvg_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "beta7_nvg"));
    public static final RegistryObject<WBArmorItem> BETA7_HELMET = ITEMS.register("beta7_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "beta7"));
    public static final RegistryObject<WBArmorItem> BETA7_CHESTPLATE = ITEMS.register("beta7_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "beta7"));
    public static final RegistryObject<WBArmorItem> BETA7_UNIFORM = ITEMS.register("beta7_uniform",
            () -> new WBArmorItem(Materials.WARBORN_UNIFORM, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "beta7_uniform"));
    public static final RegistryObject<WBArmorItem> BETA7_LEGGINGS = ITEMS.register("beta7_leggings",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(), "beta7"));
    public static final RegistryObject<WBArmorItem> BETA7_BOOTS = ITEMS.register("beta7_boots",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.BOOTS, new Item.Properties(), "beta7"));
    public static final RegistryObject<WBArmorItem> BETA7_SHOULDERPADS = ITEMS.register("beta7_shoulderpads",
            () -> new WBArmorItem(Materials.WARBORN_SHOULDERPADS, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "beta7_shoulderpads"));
    public static final RegistryObject<WBArmorItem> BETA7_NVG_HELMET_SLATE = ITEMS.register("beta7_nvg_helmet_slate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "beta7_nvg_slate"));
    public static final RegistryObject<WBArmorItem> BETA7_HELMET_SLATE = ITEMS.register("beta7_helmet_slate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "beta7_slate"));
    public static final RegistryObject<WBArmorItem> BETA7_CHESTPLATE_SLATE = ITEMS.register("beta7_chestplate_slate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "beta7_slate"));
    public static final RegistryObject<WBArmorItem> BETA7_LEGGINGS_SLATE = ITEMS.register("beta7_leggings_slate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(), "beta7_slate"));
    public static final RegistryObject<WBArmorItem> BETA7_BOOTS_SLATE = ITEMS.register("beta7_boots_slate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(), "beta7_slate"));
    public static final RegistryObject<WBArmorItem> BETA7_SHOULDERPADS_SLATE = ITEMS.register("beta7_shoulderpads_slate",
            () -> new WBArmorItem(Materials.WARBORN_SHOULDERPADS, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "beta7_shoulderpads_slate"));
    public static final RegistryObject<WBArmorItem> BETA7_NVG_HELMET_ASH = ITEMS.register("beta7_nvg_helmet_ash",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "beta7_nvg_ash"));
    public static final RegistryObject<WBArmorItem> BETA7_HELMET_ASH = ITEMS.register("beta7_helmet_ash",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "beta7_ash"));
    public static final RegistryObject<WBArmorItem> BETA7_CHESTPLATE_ASH = ITEMS.register("beta7_chestplate_ash",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "beta7_ash"));
    public static final RegistryObject<WBArmorItem> BETA7_LEGGINGS_ASH = ITEMS.register("beta7_leggings_ash",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(), "beta7_ash"));
    public static final RegistryObject<WBArmorItem> BETA7_BOOTS_ASH = ITEMS.register("beta7_boots_ash",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(), "beta7_ash"));
    public static final RegistryObject<WBArmorItem> BETA7_SHOULDERPADS_ASH = ITEMS.register("beta7_shoulderpads_ash",
            () -> new WBArmorItem(Materials.WARBORN_SHOULDERPADS, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "beta7_shoulderpads_ash"));

    //Killa
    public static final RegistryObject<Item> KILLA_HELMET = ITEMS.register("killa_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "killa"));
    public static final RegistryObject<Item> KILLA_CHESTPLATE = ITEMS.register("killa_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "killa"));

    //Tagilla
    public static final RegistryObject<Item> TAGILLA_HELMET = ITEMS.register("tagilla_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "tagilla"));
    public static final RegistryObject<Item> TAGILLA_CHESTPLATE = ITEMS.register("tagilla_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "tagilla"));
    public static final RegistryObject<Item> TAGILLA_LEGGINGS = ITEMS.register("tagilla_leggings",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(), "tagilla"));

    //Insurgency Commander
    public static final RegistryObject<Item> INSURGENCY_COMMANDER_HELMET = ITEMS.register("insurgency_commander_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "insurgency_commander"));
    public static final RegistryObject<Item> INSURGENCY_COMMANDER_CHESTPLATE = ITEMS.register("insurgency_commander_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "insurgency_commander"));
    public static final RegistryObject<Item> INSURGENCY_COMMANDER_SHOULDERPADS = ITEMS.register("insurgency_commander_shoulderpads",
            () -> new WBArmorItem(Materials.WARBORN_SHOULDERPADS, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "insurgency_commander_shoulderpads"));
    public static final RegistryObject<Item> INSURGENCY_COMMANDER_LEGGINGS = ITEMS.register("insurgency_commander_leggings",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(), "insurgency_commander"));

    //Insurgency Shturmovik
    public static final RegistryObject<Item> INSURGENCY_SHTURMOVIK_HELMET = ITEMS.register("insurgency_shturmovik_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "insurgency_shturmovik"));
    public static final RegistryObject<Item> INSURGENCY_SHTURMOVIK_CHESTPLATE = ITEMS.register("insurgency_shturmovik_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "insurgency_shturmovik"));
    public static final RegistryObject<BackpackItem> INSURGENCY_SHTURMOVIK_BACKPACK = ITEMS.register("insurgency_shturmovik_backpack",
            () -> new BackpackItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "insurgency_shturmovik_backpack"));

    //Soviet
    public static final RegistryObject<Item> SOVIET_SOLDIER_HELMET = ITEMS.register("soviet_soldier_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "soviet_soldier"));
    public static final RegistryObject<Item> SOVIET_UNIFORM = ITEMS.register("soviet_uniform",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "soviet_uniform"));
    public static final RegistryObject<Item> SOVIET_SOLDIER_CHESTPLATE = ITEMS.register("soviet_soldier_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "soviet_soldier"));
    public static final RegistryObject<Item> SOVIET_LEGGINGS = ITEMS.register("soviet_leggings",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(), "soviet_soldier"));
    public static final RegistryObject<BackpackItem> SOVIET_SOLDIER_BACKPACK = ITEMS.register("soviet_soldier_backpack",
            () -> new BackpackItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "soviet_soldier_backpack"));
    public static final RegistryObject<Item> SOVIET_OFFICER_HELMET = ITEMS.register("soviet_officer_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(), "soviet_officer"));
    public static final RegistryObject<Item> SOVIET_OFFICER_CHESTPLATE = ITEMS.register("soviet_officer_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(), "soviet_officer"));

    //SSO
    public static final RegistryObject<Item> SSO_SQUAD_LEADER_HELMET = ITEMS.register("sso_squad_leader_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(),"sso_squad_leader"));
    public static final RegistryObject<Item> SSO_SQUAD_LEADER_CHESTPLATE = ITEMS.register("sso_squad_leader_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(),"sso_squad_leader"));
    public static final RegistryObject<Item> SSO_SQUAD_LEADER_LEGGINGS = ITEMS.register("sso_squad_leader_leggings",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(),"sso_squad_leader"));

    public static final RegistryObject<Item> SSO_SOLDIER_HELMET = ITEMS.register("sso_soldier_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(),"sso_soldier"));
    public static final RegistryObject<Item> SSO_SOLDIER_CHESTPLATE = ITEMS.register("sso_soldier_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(),"sso_soldier"));
    public static final RegistryObject<Item> SSO_SOLDIER_LEGGINGS = ITEMS.register("sso_soldier_leggings",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(),"sso_soldier"));
    public static final RegistryObject<BackpackItem> SSO_SOLDIER_BACKPACK = ITEMS.register("sso_soldier_backpack",
            () -> new BackpackItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "sso_soldier_backpack"));

    public static final RegistryObject<Item> SSO_MACHINE_GUNNER_HELMET = ITEMS.register("sso_machine_gunner_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(),"sso_machine_gunner"));
    public static final RegistryObject<Item> SSO_MACHINE_GUNNER_CHESTPLATE = ITEMS.register("sso_machine_gunner_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(),"sso_machine_gunner"));
    public static final RegistryObject<Item> SSO_MACHINE_GUNNER_LEGGINGS = ITEMS.register("sso_machine_gunner_leggings",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(),"sso_machine_gunner"));
    public static final RegistryObject<BackpackItem> SSO_MACHINE_GUNNER_BACKPACK = ITEMS.register("sso_machine_gunner_backpack",
            () -> new BackpackItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "sso_machine_gunner_backpack"));
    public static final RegistryObject<Item> SSO_SHOULDERPADS = ITEMS.register("sso_shoulderpads",
            () -> new WBArmorItem(Materials.WARBORN_SHOULDERPADS, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "sso_shoulderpads"));

    public static final RegistryObject<Item> FSB_SOLDIER_HELMET = ITEMS.register("fsb_soldier_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(),"fsb_soldier"));
    public static final RegistryObject<Item> FSB_UNIFORM = ITEMS.register("fsb_uniform",
            () -> new WBArmorItem(Materials.WARBORN_UNIFORM, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(),"fsb_uniform"));
    public static final RegistryObject<Item> FSB_SOLDIER_CHESTPLATE = ITEMS.register("fsb_soldier_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(),"fsb_soldier"));
    public static final RegistryObject<Item> FSB_LEGGINGS = ITEMS.register("fsb_leggings",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(),"fsb_soldier"));
    public static final RegistryObject<Item> FSB_SQUAD_LEADER_HELMET = ITEMS.register("fsb_squad_leader_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(),"fsb_squad_leader"));
    public static final RegistryObject<Item> FSB_SQUAD_LEADER_CHESTPLATE = ITEMS.register("fsb_squad_leader_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(),"fsb_squad_leader"));
    public static final RegistryObject<Item> FSB_MACHINE_GUNNER_HELMET = ITEMS.register("fsb_machine_gunner_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(),"fsb_machine_gunner"));
    public static final RegistryObject<Item> FSB_MACHINE_GUNNER_CHESTPLATE = ITEMS.register("fsb_machine_gunner_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(),"fsb_machine_gunner"));
    public static final RegistryObject<Item> FSB_VKPO_CAP = ITEMS.register("fsb_vkpo_cap",
            () -> new WBArmorItem(Materials.WARBORN_HEADGEAR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(),"fsb_vkpo_cap"));
    public static final RegistryObject<Item> FSB_PANAMA = ITEMS.register("fsb_panama",
            () -> new WBArmorItem(Materials.WARBORN_HEADGEAR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(),"fsb_panama"));

    public static final RegistryObject<Item> RATNIK_HELMET = ITEMS.register("ratnik_helmet",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(),"ratnik"));
    public static final RegistryObject<Item> RATNIK_PMK4 = ITEMS.register("ratnik_pmk-4",
            () -> new WBArmorItem(Materials.WARBORN_HEADGEAR, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties(),"ratnik_pmk-4"));
    public static final RegistryObject<Item> RATNIK_UNIFORM = ITEMS.register("ratnik_uniform",
            () -> new WBArmorItem(Materials.WARBORN_UNIFORM, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(),"ratnik_uniform"));
    public static final RegistryObject<Item> RATNIK_UNIFORM_VARIANT = ITEMS.register("ratnik_uniform_variant",
            () -> new WBArmorItem(Materials.WARBORN_UNIFORM, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(),"ratnik_uniform_variant"));
    public static final RegistryObject<Item> RATNIK_CHESTPLATE = ITEMS.register("ratnik_chestplate",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(),"ratnik"));
    public static final RegistryObject<Item> RATNIK_CHESTPLATE_VARIANT = ITEMS.register("ratnik_chestplate_variant",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties(),"ratnik_variant"));
    public static final RegistryObject<Item> RATNIK_LEGGINGS = ITEMS.register("ratnik_leggings",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties(),"ratnik_uniform"));
    public static final RegistryObject<BackpackItem> RATNIK_BACKPACK = ITEMS.register("ratnik_backpack",
            () -> new BackpackItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().stacksTo(1), "ratnik_backpack"));
    public static final RegistryObject<Item> RATNIK_BOOTS = ITEMS.register("ratnik_boots",
            () -> new WBArmorItem(Materials.WARBORN_ARMOR, net.minecraft.world.item.ArmorItem.Type.BOOTS, new Item.Properties(),"ratnik_uniform"));

    // NVG Battery
    public static final RegistryObject<NVGBatteryItem> NVG_BATTERY = ITEMS.register("nvg_battery",
            NVGBatteryItem::new);

    // Goggles
    public static final RegistryObject<GogglesItem> NVG_GOGGLES = ITEMS.register("nvg_goggles",
            () -> new GogglesItem(WBArmorItem.TAG_NVG));
    public static final RegistryObject<GogglesItem> SIMPLE_NVG_GOGGLES = ITEMS.register("simple_nvg_goggles",
            () -> new GogglesItem(WBArmorItem.TAG_SIMPLE_NVG));
    public static final RegistryObject<GogglesItem> THERMAL_GOGGLES = ITEMS.register("thermal_goggles",
            () -> new GogglesItem(WBArmorItem.TAG_THERMAL));
    public static final RegistryObject<GogglesItem> DIGITAL_GOGGLES = ITEMS.register("digital_goggles",
            () -> new GogglesItem(WBArmorItem.TAG_DIGITAL));

    // Weapons
    public static final RegistryObject<Item> TAGILLA_MOLOT = ITEMS.register("tagilla_molot",
            () -> new WeaponItem(new Item.Properties().stacksTo(1).durability(500)));

    // Armor Plates
    public static final RegistryObject<ArmorPlateItem> STEEL_PLATE_LEVEL_III = ITEMS.register("steel_plate_level_iii",
            () -> new ArmorPlateItem(ProtectionTier.LEVEL_III, MaterialType.STEEL, new Item.Properties()));
    public static final RegistryObject<ArmorPlateItem> CERAMIC_PLATE_LEVEL_IV = ITEMS.register("ceramic_plate_level_iv",
            () -> new ArmorPlateItem(ProtectionTier.LEVEL_IV, MaterialType.CERAMIC, new Item.Properties()));
    public static final RegistryObject<ArmorPlateItem> KEVLAR_PLATE_LEVEL_IIIA = ITEMS.register("kevlar_plate_level_iiia",
            () -> new ArmorPlateItem(ProtectionTier.LEVEL_IIIA, MaterialType.SOFT_KEVLAR, new Item.Properties()));
    public static final RegistryObject<ArmorPlateItem> COMPOSITE_PLATE_LEVEL_IV = ITEMS.register("composite_plate_level_iv",
            () -> new ArmorPlateItem(ProtectionTier.LEVEL_IV, MaterialType.COMPOSITE, new Item.Properties()));
    public static final RegistryObject<ArmorPlateItem> POLYETHYLENE_PLATE_LEVEL_III = ITEMS.register("polyethylene_plate_level_iii",
            () -> new ArmorPlateItem(ProtectionTier.LEVEL_III, MaterialType.POLYETHYLENE, new Item.Properties()));

    // Syringes
    public static final RegistryObject<SyringeItem> PROPITAL = ITEMS.register("propital",
            () -> new SyringeItem(new Item.Properties(), SyringeType.PROPITAL));
    public static final RegistryObject<SyringeItem> CURE = ITEMS.register("cure",
            () -> new SyringeItem(new Item.Properties(), SyringeType.CURE));

    // Phantom items (never in player inventory, used only for first-person animations)
    public static final RegistryObject<NVGHandItem> NVG_HAND_ITEM = ITEMS.register("nvg_hand",
            () -> new NVGHandItem(new Item.Properties()));

    public static final RegistryObject<CreativeModeTab> WARBORN_TAB = CREATIVE_MODE_TABS.register("warborn_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.warborn_tab"))
                    .icon(() -> new ItemStack(RU_HELMET.get()))
                    .displayItems((enabledFeatures, entries) -> {
                        entries.accept(RU_HELMET.get());
                        entries.accept(SHTURMOVIK_RU_CHESTPLATE.get());
                        entries.accept(SHTURMOVIK_RU_BACKPACK.get());
                        entries.accept(RU_SHOULDERPADS.get());
                        entries.accept(SHTURMOVIKV2_CHESTPLATE.get());
                        entries.accept(RAZVETCHIK_CHESTPLATE.get());
                        entries.accept(MASHINEGUNNER_RU_CHESTPLATE.get());
                        entries.accept(SQUAD_LIDER_RU_CHESTPLATE.get());
                        entries.accept(SQUAD_LIDER_RU_BACKPACK.get());

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

                        entries.accept(BETA7_NVG_HELMET.get());
                        entries.accept(BETA7_HELMET.get());
                        entries.accept(BETA7_CHESTPLATE.get());
                        entries.accept(BETA7_UNIFORM.get());
                        entries.accept(BETA7_LEGGINGS.get());
                        entries.accept(BETA7_BOOTS.get());
                        entries.accept(BETA7_SHOULDERPADS.get());
                        entries.accept(BETA7_NVG_HELMET_SLATE.get());
                        entries.accept(BETA7_HELMET_SLATE.get());
                        entries.accept(BETA7_CHESTPLATE_SLATE.get());
                        entries.accept(BETA7_LEGGINGS_SLATE.get());
                        entries.accept(BETA7_BOOTS_SLATE.get());
                        entries.accept(BETA7_SHOULDERPADS_SLATE.get());
                        entries.accept(BETA7_NVG_HELMET_ASH.get());
                        entries.accept(BETA7_HELMET_ASH.get());
                        entries.accept(BETA7_CHESTPLATE_ASH.get());
                        entries.accept(BETA7_LEGGINGS_ASH.get());
                        entries.accept(BETA7_BOOTS_ASH.get());
                        entries.accept(BETA7_SHOULDERPADS_ASH.get());

                        entries.accept(KILLA_HELMET.get());
                        entries.accept(KILLA_CHESTPLATE.get());

                        entries.accept(TAGILLA_HELMET.get());
                        entries.accept(TAGILLA_CHESTPLATE.get());
                        entries.accept(TAGILLA_LEGGINGS.get());

                        entries.accept(INSURGENCY_COMMANDER_HELMET.get());
                        entries.accept(INSURGENCY_COMMANDER_CHESTPLATE.get());
                        entries.accept(INSURGENCY_COMMANDER_SHOULDERPADS.get());
                        entries.accept(INSURGENCY_COMMANDER_LEGGINGS.get());

                        entries.accept(INSURGENCY_SHTURMOVIK_HELMET.get());
                        entries.accept(INSURGENCY_SHTURMOVIK_CHESTPLATE.get());
                        entries.accept(INSURGENCY_SHTURMOVIK_BACKPACK.get());

                        entries.accept(SOVIET_SOLDIER_HELMET.get());
                        entries.accept(SOVIET_UNIFORM.get());
                        entries.accept(SOVIET_SOLDIER_CHESTPLATE.get());
                        entries.accept(SOVIET_LEGGINGS.get());
                        entries.accept(SOVIET_SOLDIER_BACKPACK.get());
                        entries.accept(SOVIET_OFFICER_HELMET.get());
                        entries.accept(SOVIET_OFFICER_CHESTPLATE.get());

                        entries.accept(SSO_SQUAD_LEADER_HELMET.get());
                        entries.accept(SSO_SQUAD_LEADER_CHESTPLATE.get());
                        entries.accept(SSO_SQUAD_LEADER_LEGGINGS.get());
                        entries.accept(SSO_SOLDIER_HELMET.get());
                        entries.accept(SSO_SOLDIER_CHESTPLATE.get());
                        entries.accept(SSO_SOLDIER_LEGGINGS.get());
                        entries.accept(SSO_SOLDIER_BACKPACK.get());
                        entries.accept(SSO_MACHINE_GUNNER_HELMET.get());
                        entries.accept(SSO_MACHINE_GUNNER_CHESTPLATE.get());
                        entries.accept(SSO_MACHINE_GUNNER_LEGGINGS.get());
                        entries.accept(SSO_SHOULDERPADS.get());
                        entries.accept(SSO_MACHINE_GUNNER_BACKPACK.get());

                        entries.accept(FSB_SOLDIER_HELMET.get());
                        entries.accept(FSB_UNIFORM.get());
                        entries.accept(FSB_SOLDIER_CHESTPLATE.get());
                        entries.accept(FSB_LEGGINGS.get());
                        entries.accept(FSB_SQUAD_LEADER_HELMET.get());
                        entries.accept(FSB_SQUAD_LEADER_CHESTPLATE.get());
                        entries.accept(FSB_MACHINE_GUNNER_HELMET.get());
                        entries.accept(FSB_MACHINE_GUNNER_CHESTPLATE.get());
                        entries.accept(FSB_VKPO_CAP.get());
                        entries.accept(FSB_PANAMA.get());

                        entries.accept(RATNIK_HELMET.get());
                        entries.accept(RATNIK_PMK4.get());
                        entries.accept(RATNIK_UNIFORM.get());
                        entries.accept(RATNIK_UNIFORM_VARIANT.get());
                        entries.accept(RATNIK_CHESTPLATE.get());
                        entries.accept(RATNIK_CHESTPLATE_VARIANT.get());
                        entries.accept(RATNIK_BACKPACK.get());
                        entries.accept(RATNIK_LEGGINGS.get());
                        entries.accept(RATNIK_BOOTS.get());

                        entries.accept(TAGILLA_MOLOT.get());

                        entries.accept(STEEL_PLATE_LEVEL_III.get());
                        entries.accept(CERAMIC_PLATE_LEVEL_IV.get());
                        entries.accept(KEVLAR_PLATE_LEVEL_IIIA.get());
                        entries.accept(COMPOSITE_PLATE_LEVEL_IV.get());
                        entries.accept(POLYETHYLENE_PLATE_LEVEL_III.get());

                        entries.accept(PROPITAL.get());
                        entries.accept(CURE.get());

                        entries.accept(NVG_BATTERY.get());
                        entries.accept(NVG_GOGGLES.get());
                        entries.accept(SIMPLE_NVG_GOGGLES.get());
                        entries.accept(THERMAL_GOGGLES.get());
                        entries.accept(DIGITAL_GOGGLES.get());
                    })
                    .build()
    );

    private static final Map<String, RegistryObject<ArmorPlateItem>> PLATE_REGISTRY = new HashMap<>();

    public static Item getPlateItem(ProtectionTier tier, MaterialType material) {
        if (PLATE_REGISTRY.isEmpty()) {
            PLATE_REGISTRY.put(ProtectionTier.LEVEL_III.name() + "_" + MaterialType.STEEL.name(), STEEL_PLATE_LEVEL_III);
            PLATE_REGISTRY.put(ProtectionTier.LEVEL_IV.name() + "_" + MaterialType.CERAMIC.name(), CERAMIC_PLATE_LEVEL_IV);
            PLATE_REGISTRY.put(ProtectionTier.LEVEL_IIIA.name() + "_" + MaterialType.SOFT_KEVLAR.name(), KEVLAR_PLATE_LEVEL_IIIA);
            PLATE_REGISTRY.put(ProtectionTier.LEVEL_IV.name() + "_" + MaterialType.COMPOSITE.name(), COMPOSITE_PLATE_LEVEL_IV);
            PLATE_REGISTRY.put(ProtectionTier.LEVEL_III.name() + "_" + MaterialType.POLYETHYLENE.name(), POLYETHYLENE_PLATE_LEVEL_III);
        }

        String key = tier.name() + "_" + material.name();
        if (PLATE_REGISTRY.containsKey(key)) {
            return PLATE_REGISTRY.get(key).get();
        }

        return STEEL_PLATE_LEVEL_III.get();
    }
}