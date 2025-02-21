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

    public static final RegistryObject<WarbornArmorItem> WARBORN_HELMET = ITEMS.register("warborn_helmet",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<WarbornArmorItem> WARBORN_CHESTPLATE = ITEMS.register("warborn_chestplate",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<WarbornArmorItem> WARBORN_LEGGINGS = ITEMS.register("warborn_leggings",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<WarbornArmorItem> WARBORN_BOOTS = ITEMS.register("warborn_boots",
            () -> new WarbornArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, WARBORN.MODID);

    public static final RegistryObject<CreativeModeTab> WARBORN_TAB = CREATIVE_MODE_TABS.register("warborn_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.warborn_tab"))
                    .icon(() -> new ItemStack(WARBORN_HELMET.get()))
                    .displayItems((enabledFeatures, entries) -> {
                        entries.accept(WARBORN_HELMET.get());
                        entries.accept(WARBORN_CHESTPLATE.get());
                        entries.accept(WARBORN_LEGGINGS.get());
                        entries.accept(WARBORN_BOOTS.get());
                    })
                    .build()
    );
}