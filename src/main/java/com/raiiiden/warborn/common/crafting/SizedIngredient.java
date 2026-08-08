package com.raiiiden.warborn.common.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

// An Ingredient plus how many of it a recipe needs; vanilla ingredients are one-each, so amounts used to be faked by repeating entries.
public record SizedIngredient(Ingredient ingredient, int count) {
    public static SizedIngredient fromJson(JsonObject json) {
        // "count" is ours; strip it so Ingredient.fromJson doesn't see an unexpected key.
        int count = GsonHelper.getAsInt(json, "count", 1);
        JsonObject copy = json.deepCopy();
        copy.remove("count");
        return new SizedIngredient(Ingredient.fromJson(copy), Math.max(1, count));
    }

    public static SizedIngredient fromNetwork(FriendlyByteBuf buf) {
        Ingredient ingredient = Ingredient.fromNetwork(buf);
        return new SizedIngredient(ingredient, buf.readVarInt());
    }

    public void toNetwork(FriendlyByteBuf buf) {
        ingredient.toNetwork(buf);
        buf.writeVarInt(count);
    }

    public boolean test(ItemStack stack) {
        return ingredient.test(stack);
    }

    // The ingredient's accepted stacks, each scaled to the required count, for display in JEI.
    public ItemStack[] displayStacks() {
        ItemStack[] items = ingredient.getItems();
        ItemStack[] sized = new ItemStack[items.length];
        for (int i = 0; i < items.length; i++) {
            sized[i] = items[i].copy();
            sized[i].setCount(count);
        }
        return sized;
    }
}
