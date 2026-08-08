package com.raiiiden.warborn.common.crafting;

import com.google.gson.JsonObject;
import com.raiiiden.warborn.common.init.ModItemRegistry;
import com.raiiiden.warborn.common.object.capability.NVGBatteryStorage;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

// Tops a battery up from charged blocks; needs its own class because the result is the input battery with more energy, not a fixed stack.
public class BatteryChargeRecipe extends WorkstationRecipe {
    private final Ingredient battery;
    private final Ingredient catalyst;
    private final int energyPerCatalyst;

    public BatteryChargeRecipe(ResourceLocation id, Ingredient battery, Ingredient catalyst,
                               int energyPerCatalyst, int processTime,
                               RecipeType<?> type, RecipeSerializer<?> serializer) {
        super(id,
                List.of(new SizedIngredient(battery, 1), new SizedIngredient(catalyst, 1)),
                new ItemStack(ModItemRegistry.NVG_BATTERY.get()),
                processTime, type, serializer);
        this.battery = battery;
        this.catalyst = catalyst;
        this.energyPerCatalyst = Math.max(1, energyPerCatalyst);
    }

    public int energyPerCatalyst() {
        return energyPerCatalyst;
    }

    // What a single craft would do, given the current inputs.
    private record Charge(int batterySlot, int catalystsUsed, int newEnergy) {}

    @Nullable
    private Charge plan(Container inputs) {
        int batterySlot = -1;
        int available = 0;
        for (int slot = 0; slot < inputs.getContainerSize(); slot++) {
            ItemStack stack = inputs.getItem(slot);
            if (stack.isEmpty()) continue;
            if (batterySlot < 0 && battery.test(stack)) {
                batterySlot = slot;
            } else if (catalyst.test(stack)) {
                available += stack.getCount();
            }
        }
        if (batterySlot < 0 || available <= 0) return null;

        int current = NVGBatteryStorage.readEnergy(inputs.getItem(batterySlot));
        int room = NVGBatteryStorage.MAX_CAPACITY - current;
        if (room <= 0) return null;

        // One catalyst per craft. Draining the whole stack in a single pass emptied a full
        // stack of blocks the instant a battery was dropped in, with no way to stop partway;
        // charging a block at a time keeps each craft visible on the progress bar and lets
        // the player pull the battery whenever they like. Overflow is clamped to the room
        // left, so the last block tops it off instead of being wasted.
        int gained = Math.min(room, energyPerCatalyst);
        return new Charge(batterySlot, 1, current + gained);
    }

    @Override
    public boolean matches(@NotNull Container inputs, @NotNull Level level) {
        return plan(inputs) != null;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull Container inputs, @NotNull RegistryAccess registryAccess) {
        Charge charge = plan(inputs);
        if (charge == null) return ItemStack.EMPTY;

        ItemStack charged = inputs.getItem(charge.batterySlot()).copy();
        charged.getOrCreateTag().putInt(NVGBatteryStorage.NBT_KEY, charge.newEnergy());
        return charged;
    }

    @Override
    public void consume(@NotNull Container inputs) {
        Charge charge = plan(inputs);
        if (charge == null) return;

        inputs.getItem(charge.batterySlot()).shrink(1);

        int remaining = charge.catalystsUsed();
        for (int slot = 0; slot < inputs.getContainerSize() && remaining > 0; slot++) {
            if (slot == charge.batterySlot()) continue;
            ItemStack stack = inputs.getItem(slot);
            if (stack.isEmpty() || !catalyst.test(stack)) continue;

            int take = Math.min(remaining, stack.getCount());
            stack.shrink(take);
            remaining -= take;
        }
        inputs.setChanged();
    }

    public static class Serializer implements RecipeSerializer<BatteryChargeRecipe> {
        private final Supplier<RecipeType<WorkstationRecipe>> type;

        public Serializer(Supplier<RecipeType<WorkstationRecipe>> type) {
            this.type = type;
        }

        @Override
        public BatteryChargeRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new BatteryChargeRecipe(id,
                    Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "battery")),
                    Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "catalyst")),
                    GsonHelper.getAsInt(json, "energyPerCatalyst", 100),
                    GsonHelper.getAsInt(json, "processTime", 300),
                    type.get(), this);
        }

        @Override
        public BatteryChargeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            return new BatteryChargeRecipe(id,
                    Ingredient.fromNetwork(buf), Ingredient.fromNetwork(buf),
                    buf.readVarInt(), buf.readVarInt(), type.get(), this);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, BatteryChargeRecipe recipe) {
            recipe.battery.toNetwork(buf);
            recipe.catalyst.toNetwork(buf);
            buf.writeVarInt(recipe.energyPerCatalyst);
            buf.writeVarInt(recipe.processTime());
        }
    }
}
