package com.raiiiden.warborn.common.pack;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.init.ModItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Registers pack-defined armor and one creative tab per pack during mod construction.
public final class ArmorPackRegistry {
    private static final Map<String, RegistryObject<Item>> ITEMS = new LinkedHashMap<>();
    private static boolean registered;

    private ArmorPackRegistry() {}

    public static void registerBundledPacks() {
        if (registered) return;
        registered = true;

        List<ArmorPackDefinition> packs = ArmorPackLoader.loadBundledPacks();
        for (ArmorPackDefinition pack : packs) {
            for (ArmorItemDefinition definition : pack.items()) {
                RegistryObject<Item> item = ModItemRegistry.ITEMS.register(definition.id(), definition::createItem);
                ITEMS.put(definition.id(), item);
            }
        }

        // Chain each tab behind the one before it. Without an explicit constraint Forge is
        // free to interleave other mods' tabs between ours, so with a big mod list the pack
        // tabs drifted apart; naming the previous tab keeps them in one contiguous run, in
        // index order, however many other mods are installed.
        String previousTabId = null;
        for (ArmorPackDefinition pack : packs) {
            final String after = previousTabId;
            ModItemRegistry.CREATIVE_MODE_TABS.register(pack.tabId(),
                    () -> {
                        CreativeModeTab.Builder builder = CreativeModeTab.builder()
                                .title(Component.translatable(pack.translationKey()))
                                .icon(() -> new ItemStack(require(pack.icon()).get()))
                                .displayItems((features, entries) -> {
                                    for (ArmorItemDefinition definition : pack.items()) {
                                        entries.accept(require(definition.id()).get());
                                    }
                                    if (pack.includeGeneralItems()) {
                                        ModItemRegistry.addGeneralCreativeItems(entries);
                                    }
                                });
                        if (after != null) {
                            builder.withTabsBefore(ResourceKey.create(Registries.CREATIVE_MODE_TAB,
                                    new ResourceLocation(WARBORN.MODID, after)));
                        }
                        return builder.build();
                    });
            previousTabId = pack.tabId();
        }

        WARBORN.LOGGER.info("Loaded {} bundled armor packs with {} items", packs.size(), ITEMS.size());
    }

    public static boolean is(ItemStack stack, String itemId) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
        return id != null && id.equals(new ResourceLocation(WARBORN.MODID, itemId));
    }

    public static RegistryObject<Item> require(String id) {
        RegistryObject<Item> item = ITEMS.get(id);
        if (item == null) throw new IllegalArgumentException("Unknown pack armor item: " + id);
        return item;
    }
}
