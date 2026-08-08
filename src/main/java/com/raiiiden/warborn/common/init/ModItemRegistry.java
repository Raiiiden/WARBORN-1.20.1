package com.raiiiden.warborn.common.init;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.*;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import com.raiiiden.warborn.common.pack.ArmorPackRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;


public class ModItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WARBORN.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WARBORN.MODID);

    // Crafting materials
    public static final RegistryObject<Item> RAW_FIBER = ITEMS.register("raw_fiber", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RESIN = ITEMS.register("resin", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STEEL_BLEND = ITEMS.register("steel_blend", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STEEL_PLATE = ITEMS.register("steel_plate", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> KEVLAR_FIBER = ITEMS.register("kevlar_fiber", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> WOVEN_KEVLAR = ITEMS.register("woven_kevlar", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CERAMIC_COMPOUND = ITEMS.register("ceramic_compound", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CERAMIC_TILE = ITEMS.register("ceramic_tile", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> POLYMER_COMPOUND = ITEMS.register("polymer_compound", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> POLYETHYLENE_SHEET = ITEMS.register("polyethylene_sheet", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> COMPOSITE_MATERIAL = ITEMS.register("composite_material", () -> new Item(new Item.Properties()));

    // Backpack upgrades
    public static final RegistryObject<BackpackUpgradeItem> BACKPACK_UPGRADE_TIER2 = ITEMS.register("backpack_upgrade_tier2",
            () -> new BackpackUpgradeItem(2, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BackpackUpgradeItem> BACKPACK_UPGRADE_TIER3 = ITEMS.register("backpack_upgrade_tier3",
            () -> new BackpackUpgradeItem(3, new Item.Properties().stacksTo(1)));

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
    public static final RegistryObject<GogglesItem> THERMAL_WHITE_GOGGLES = ITEMS.register("thermal_white_goggles",
            () -> new GogglesItem(WBArmorItem.TAG_THERMAL_WHITE));
    public static final RegistryObject<GogglesItem> THERMAL_BLACK_GOGGLES = ITEMS.register("thermal_black_goggles",
            () -> new GogglesItem(WBArmorItem.TAG_THERMAL_BLACK));
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

    public static void registerArmorPacks() {
        ArmorPackRegistry.registerBundledPacks();
    }

    public static void addGeneralCreativeItems(CreativeModeTab.Output entries) {
        entries.accept(ModBlockRegistry.INDUSTRIAL_PRESS.get());
        entries.accept(ModBlockRegistry.BALLISTICS_BENCH.get());

        entries.accept(RAW_FIBER.get());
        entries.accept(RESIN.get());
        entries.accept(STEEL_BLEND.get());
        entries.accept(STEEL_PLATE.get());
        entries.accept(KEVLAR_FIBER.get());
        entries.accept(WOVEN_KEVLAR.get());
        entries.accept(CERAMIC_COMPOUND.get());
        entries.accept(CERAMIC_TILE.get());
        entries.accept(POLYMER_COMPOUND.get());
        entries.accept(POLYETHYLENE_SHEET.get());
        entries.accept(COMPOSITE_MATERIAL.get());
        entries.accept(BACKPACK_UPGRADE_TIER2.get());
        entries.accept(BACKPACK_UPGRADE_TIER3.get());

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
        entries.accept(THERMAL_WHITE_GOGGLES.get());
        entries.accept(THERMAL_BLACK_GOGGLES.get());
        entries.accept(DIGITAL_GOGGLES.get());
    }

    // Plate lookup
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
