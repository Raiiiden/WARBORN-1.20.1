package com.raiiiden.warborn.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

// A recipe for either workstation; inputs are matched as an unordered bag with leftovers allowed, so only the registered RecipeType differs.
public class WorkstationRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final List<SizedIngredient> ingredients;
    private final ItemStack result;
    private final int processTime;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;

    public WorkstationRecipe(ResourceLocation id, List<SizedIngredient> ingredients, ItemStack result,
                             int processTime, RecipeType<?> type, RecipeSerializer<?> serializer) {
        this.id = id;
        this.ingredients = List.copyOf(ingredients);
        this.result = result;
        this.processTime = Math.max(1, processTime);
        this.type = type;
        this.serializer = serializer;
    }

    public List<SizedIngredient> sizedIngredients() {
        return ingredients;
    }

    public int processTime() {
        return processTime;
    }

    // How many items this recipe eats in total. Used to break ties between recipes that both match.
    public int totalIngredientCount() {
        int total = 0;
        for (SizedIngredient ingredient : ingredients) {
            total += ingredient.count();
        }
        return total;
    }

    @Override
    public boolean matches(Container inputs, Level level) {
        return distribute(inputs, false);
    }

    // Removes this recipe's ingredients from the given container. Only call it after {@link #matches}.
    public void consume(Container inputs) {
        distribute(inputs, true);
    }

    // Takes what each ingredient needs out of the slots; matching and consumption share this so they can't disagree. apply=false only reports whether it would succeed.
    private boolean distribute(Container inputs, boolean apply) {
        int size = inputs.getContainerSize();
        int[] available = new int[size];
        for (int slot = 0; slot < size; slot++) {
            available[slot] = inputs.getItem(slot).getCount();
        }

        // Two passes so nothing is consumed unless every ingredient can be satisfied.
        int[][] taken = new int[ingredients.size()][];
        for (int i = 0; i < ingredients.size(); i++) {
            SizedIngredient ingredient = ingredients.get(i);
            int needed = ingredient.count();
            int[] perSlot = new int[size];

            for (int slot = 0; slot < size && needed > 0; slot++) {
                if (available[slot] <= 0) continue;
                if (!ingredient.test(inputs.getItem(slot))) continue;

                int take = Math.min(needed, available[slot]);
                available[slot] -= take;
                perSlot[slot] = take;
                needed -= take;
            }

            if (needed > 0) return false;
            taken[i] = perSlot;
        }

        if (apply) {
            for (int[] perSlot : taken) {
                for (int slot = 0; slot < size; slot++) {
                    if (perSlot[slot] > 0) inputs.getItem(slot).shrink(perSlot[slot]);
                }
            }
            inputs.setChanged();
        }
        return true;
    }

    @Override
    public ItemStack assemble(Container inputs, RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> plain = NonNullList.create();
        for (SizedIngredient ingredient : ingredients) {
            plain.add(ingredient.ingredient());
        }
        return plain;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return type;
    }

    public static class Serializer implements RecipeSerializer<WorkstationRecipe> {
        private final Supplier<RecipeType<WorkstationRecipe>> type;
        private final int defaultProcessTime;

        // Type is supplied lazily - the registry object isn't resolvable until a datapack is read.
        public Serializer(Supplier<RecipeType<WorkstationRecipe>> type, int defaultProcessTime) {
            this.type = type;
            this.defaultProcessTime = defaultProcessTime;
        }

        @Override
        public WorkstationRecipe fromJson(ResourceLocation id, JsonObject json) {
            JsonArray array = GsonHelper.getAsJsonArray(json, "ingredients");
            List<SizedIngredient> ingredients = new ArrayList<>(array.size());
            for (int i = 0; i < array.size(); i++) {
                ingredients.add(SizedIngredient.fromJson(GsonHelper.convertToJsonObject(array.get(i), "ingredient")));
            }
            if (ingredients.isEmpty()) {
                throw new com.google.gson.JsonSyntaxException("Recipe " + id + " has no ingredients");
            }

            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            int processTime = GsonHelper.getAsInt(json, "processTime", defaultProcessTime);
            return new WorkstationRecipe(id, ingredients, result, processTime, type.get(), this);
        }

        @Override
        public WorkstationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<SizedIngredient> ingredients = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                ingredients.add(SizedIngredient.fromNetwork(buf));
            }
            ItemStack result = buf.readItem();
            int processTime = buf.readVarInt();
            return new WorkstationRecipe(id, ingredients, result, processTime, type.get(), this);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, WorkstationRecipe recipe) {
            buf.writeVarInt(recipe.ingredients.size());
            for (SizedIngredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buf);
            }
            buf.writeItem(recipe.result);
            buf.writeVarInt(recipe.processTime);
        }
    }
}
