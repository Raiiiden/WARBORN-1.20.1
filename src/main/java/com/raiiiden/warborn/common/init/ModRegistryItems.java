package com.raiiiden.warborn.common.init;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.ArmorPlateItem;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRegistryItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, WARBORN.MODID);

    // refactor this
    public static final RegistryObject<ArmorPlateItem> ARMOR_PLATE = ITEMS.register("armor_plate",
            () -> new ArmorPlateItem(ProtectionTier.LEVEL_III, MaterialType.STEEL, new Item.Properties()));

    public static final RegistryObject<ArmorPlateItem> CERAMIC_PLATE_LEVEL_IV = ITEMS.register("ceramic_plate_level_iv",
            () -> new ArmorPlateItem(ProtectionTier.LEVEL_IV, MaterialType.CERAMIC, new Item.Properties()));

    public static final RegistryObject<ArmorPlateItem> KEVLAR_PLATE_LEVEL_IIIA = ITEMS.register("kevlar_plate_level_iiia",
            () -> new ArmorPlateItem(ProtectionTier.LEVEL_IIIA, MaterialType.SOFT_KEVLAR, new Item.Properties()));

    public static final RegistryObject<ArmorPlateItem> COMPOSITE_PLATE_LEVEL_IV = ITEMS.register("composite_plate_level_iv",
            () -> new ArmorPlateItem(ProtectionTier.LEVEL_IV, MaterialType.COMPOSITE, new Item.Properties()));
} 