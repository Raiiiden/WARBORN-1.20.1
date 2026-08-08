package com.raiiiden.warborn.common.pack;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.raiiiden.warborn.WARBORN;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

// Loads the ordered built-in armor packs before Forge freezes the item registry.
public final class ArmorPackLoader {
    private static final Gson GSON = new Gson();
    private static final String ROOT = "assets/fracturepoint/fracturepoint_packs/";
    private static final String INDEX = ROOT + "index.json";

    private ArmorPackLoader() {}

    public static List<ArmorPackDefinition> loadBundledPacks() {
        JsonObject index = readObject(INDEX);
        JsonArray manifests = requireArray(index, "packs", INDEX);
        List<ArmorPackDefinition> packs = new ArrayList<>();
        Set<String> packIds = new HashSet<>();
        Set<String> tabIds = new HashSet<>();
        Set<String> itemIds = new HashSet<>();

        for (JsonElement element : manifests) {
            if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) {
                throw error(INDEX, "packs must contain manifest file names");
            }
            String manifestName = element.getAsString();
            String path = ROOT + manifestName;
            ArmorPackDefinition pack = parsePack(readObject(path), path, itemIds);
            if (!packIds.add(pack.id())) throw error(path, "duplicate pack id '" + pack.id() + "'");
            if (!tabIds.add(pack.tabId())) throw error(path, "duplicate creative tab id '" + pack.tabId() + "'");
            packs.add(pack);
        }

        if (packs.isEmpty()) throw error(INDEX, "at least one armor pack is required");
        return List.copyOf(packs);
    }

    private static ArmorPackDefinition parsePack(JsonObject json, String path, Set<String> knownItems) {
        int format = optionalInt(json, "format_version", 1, path);
        if (format != 1) throw error(path, "unsupported format_version " + format + " (expected 1)");

        String id = requireId(json, "id", path);
        String tabId = requireId(json, "tab_id", path);
        String translationKey = requireString(json, "translation_key", path);
        String icon = requireId(json, "icon", path);
        boolean includeGeneralItems = optionalBoolean(json, "include_general_items", false, path);
        JsonArray itemJson = requireArray(json, "items", path);
        List<ArmorItemDefinition> items = new ArrayList<>();

        for (int i = 0; i < itemJson.size(); i++) {
            JsonElement element = itemJson.get(i);
            if (!element.isJsonObject()) throw error(path, "items[" + i + "] must be an object");
            ArmorItemDefinition definition = parseItem(element.getAsJsonObject(), path, i);
            if (!knownItems.add(definition.id())) {
                throw error(path, "duplicate armor item id '" + definition.id() + "'");
            }
            items.add(definition);
        }

        if (items.stream().noneMatch(item -> item.id().equals(icon))) {
            throw error(path, "icon '" + icon + "' is not an item in this pack");
        }
        return new ArmorPackDefinition(id, tabId, translationKey, icon, includeGeneralItems, items);
    }

    private static ArmorItemDefinition parseItem(JsonObject json, String path, int index) {
        String location = path + " items[" + index + "]";
        String id = requireId(json, "id", location);
        ArmorItemDefinition.Kind kind = optionalEnum(json, "kind", ArmorItemDefinition.Kind.class,
                ArmorItemDefinition.Kind.ARMOR, location);
        // Backpacks default to their own material so they don't inherit a full chestplate's armor value.
        ArmorItemDefinition.Material defaultMaterial = kind == ArmorItemDefinition.Kind.BACKPACK
                ? ArmorItemDefinition.Material.BACKPACK
                : ArmorItemDefinition.Material.ARMOR;
        ArmorItemDefinition.Material material = optionalEnum(json, "material", ArmorItemDefinition.Material.class,
                defaultMaterial, location);
        ArmorItem.Type slot = optionalEnum(json, "slot", ArmorItem.Type.class,
                ArmorItem.Type.CHESTPLATE, location);
        String model = requireString(json, "model", location);
        if (model.isBlank()) throw error(location, "model cannot be blank");
        String texture = optionalString(json, "texture", model, location);
        int armorModifier = optionalInt(json, "armor_modifier", 0, location);
        int resultingDefense = material.resolve().getDefenseForType(slot) + armorModifier;
        if (resultingDefense < 0) {
            throw error(location, "'armor_modifier' lowers armor below zero");
        }
        if (armorModifier > 100) {
            throw error(location, "'armor_modifier' must be 100 or less");
        }
        return new ArmorItemDefinition(id, kind, material, slot, model, texture, armorModifier);
    }

    private static JsonObject readObject(String path) {
        ClassLoader loader = ArmorPackLoader.class.getClassLoader();
        try (InputStream stream = loader.getResourceAsStream(path)) {
            if (stream == null) throw error(path, "resource does not exist");
            JsonElement value = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), JsonElement.class);
            if (value == null || !value.isJsonObject()) throw error(path, "root must be an object");
            return value.getAsJsonObject();
        } catch (IOException | JsonParseException exception) {
            throw new IllegalStateException("Failed to read armor pack " + path, exception);
        }
    }

    private static String requireString(JsonObject json, String key, String path) {
        JsonElement value = json.get(key);
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
            throw error(path, "'" + key + "' must be a string");
        }
        String result = value.getAsString();
        if (result.isBlank()) throw error(path, "'" + key + "' cannot be blank");
        return result;
    }

    private static String requireId(JsonObject json, String key, String path) {
        String id = requireString(json, key, path);
        try {
            new ResourceLocation(WARBORN.MODID, id);
        } catch (ResourceLocationException exception) {
            throw error(path, "'" + key + "' is not a valid registry path: " + id);
        }
        return id;
    }

    private static JsonArray requireArray(JsonObject json, String key, String path) {
        JsonElement value = json.get(key);
        if (value == null || !value.isJsonArray()) throw error(path, "'" + key + "' must be an array");
        return value.getAsJsonArray();
    }

    private static int optionalInt(JsonObject json, String key, int fallback, String path) {
        JsonElement value = json.get(key);
        if (value == null) return fallback;
        try {
            return value.getAsInt();
        } catch (RuntimeException exception) {
            throw error(path, "'" + key + "' must be an integer");
        }
    }

    private static String optionalString(JsonObject json, String key, String fallback, String path) {
        return json.has(key) ? requireString(json, key, path) : fallback;
    }

    private static boolean optionalBoolean(JsonObject json, String key, boolean fallback, String path) {
        JsonElement value = json.get(key);
        if (value == null) return fallback;
        if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isBoolean()) {
            throw error(path, "'" + key + "' must be a boolean");
        }
        return value.getAsBoolean();
    }

    private static <E extends Enum<E>> E optionalEnum(JsonObject json, String key, Class<E> type,
                                                       E fallback, String path) {
        JsonElement value = json.get(key);
        if (value == null) return fallback;
        String name = requireString(json, key, path).toUpperCase(Locale.ROOT);
        try {
            return Enum.valueOf(type, name);
        } catch (IllegalArgumentException exception) {
            throw error(path, "'" + key + "' has unsupported value '" + name.toLowerCase(Locale.ROOT) + "'");
        }
    }

    private static IllegalStateException error(String path, String message) {
        return new IllegalStateException("Armor pack " + path + ": " + message);
    }
}
