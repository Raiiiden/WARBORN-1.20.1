package com.raiiiden.warborn.common.init;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.block.WorkstationBlock.WorkstationKind;
import com.raiiiden.warborn.common.crafting.BatteryChargeRecipe;
import com.raiiiden.warborn.common.crafting.WorkstationRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeRegistry {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, WARBORN.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, WARBORN.MODID);

    public static final RegistryObject<RecipeType<WorkstationRecipe>> INDUSTRIAL_PRESS_TYPE =
            RECIPE_TYPES.register("industrial_press", () -> new SimpleRecipeType<>("industrial_press"));
    public static final RegistryObject<RecipeType<WorkstationRecipe>> BALLISTICS_BENCH_TYPE =
            RECIPE_TYPES.register("ballistics_bench", () -> new SimpleRecipeType<>("ballistics_bench"));

    public static final RegistryObject<RecipeSerializer<WorkstationRecipe>> INDUSTRIAL_PRESS_SERIALIZER =
            RECIPE_SERIALIZERS.register("industrial_press", () -> new WorkstationRecipe.Serializer(
                    INDUSTRIAL_PRESS_TYPE, WorkstationKind.INDUSTRIAL_PRESS.defaultProcessTime()));
    public static final RegistryObject<RecipeSerializer<WorkstationRecipe>> BALLISTICS_BENCH_SERIALIZER =
            RECIPE_SERIALIZERS.register("ballistics_bench", () -> new WorkstationRecipe.Serializer(
                    BALLISTICS_BENCH_TYPE, WorkstationKind.BALLISTICS_BENCH.defaultProcessTime()));

    // Battery charging needs its own serializer since its result is computed from the inputs, but its recipes still report the bench's recipe type.
    public static final RegistryObject<RecipeSerializer<BatteryChargeRecipe>> BATTERY_CHARGE_SERIALIZER =
            RECIPE_SERIALIZERS.register("battery_charge", () -> new BatteryChargeRecipe.Serializer(BALLISTICS_BENCH_TYPE));

    public static RecipeType<WorkstationRecipe> typeFor(WorkstationKind kind) {
        return switch (kind) {
            case INDUSTRIAL_PRESS -> INDUSTRIAL_PRESS_TYPE.get();
            case BALLISTICS_BENCH -> BALLISTICS_BENCH_TYPE.get();
        };
    }

    private record SimpleRecipeType<T extends Recipe<?>>(String name) implements RecipeType<T> {
        @Override
        public String toString() {
            return new ResourceLocation(WARBORN.MODID, name).toString();
        }
    }
}
