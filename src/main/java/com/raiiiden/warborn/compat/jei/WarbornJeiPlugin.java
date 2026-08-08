package com.raiiiden.warborn.compat.jei;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.block.WorkstationBlock.WorkstationKind;
import com.raiiiden.warborn.common.crafting.WorkstationRecipe;
import com.raiiiden.warborn.common.init.MenuTypeInit;
import com.raiiiden.warborn.common.init.ModBlockRegistry;
import com.raiiiden.warborn.common.init.ModRecipeRegistry;
import com.raiiiden.warborn.common.menu.WorkstationMenu;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import org.jetbrains.annotations.NotNull;

// Makes the two workstations discoverable in JEI; loaded only by JEI's own @JeiPlugin scan, so a game without JEI never touches it.
@JeiPlugin
public class WarbornJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(WARBORN.MODID, "jei_plugin");

    public static final RecipeType<WorkstationRecipe> INDUSTRIAL_PRESS =
            RecipeType.create(WARBORN.MODID, "industrial_press", WorkstationRecipe.class);
    public static final RecipeType<WorkstationRecipe> BALLISTICS_BENCH =
            RecipeType.create(WARBORN.MODID, "ballistics_bench", WorkstationRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        var guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(
                new WorkstationRecipeCategory(WorkstationKind.INDUSTRIAL_PRESS, INDUSTRIAL_PRESS,
                        guiHelper, ModBlockRegistry.INDUSTRIAL_PRESS.get()),
                new WorkstationRecipeCategory(WorkstationKind.BALLISTICS_BENCH, BALLISTICS_BENCH,
                        guiHelper, ModBlockRegistry.BALLISTICS_BENCH.get()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;

        RecipeManager recipes = level.getRecipeManager();
        registration.addRecipes(INDUSTRIAL_PRESS,
                recipes.getAllRecipesFor(ModRecipeRegistry.INDUSTRIAL_PRESS_TYPE.get()));
        registration.addRecipes(BALLISTICS_BENCH,
                recipes.getAllRecipesFor(ModRecipeRegistry.BALLISTICS_BENCH_TYPE.get()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ModBlockRegistry.INDUSTRIAL_PRESS.get(), INDUSTRIAL_PRESS);
        registration.addRecipeCatalyst(ModBlockRegistry.BALLISTICS_BENCH.get(), BALLISTICS_BENCH);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // Both stations share one menu type, so the transfer handlers are distinguished by recipe type. The
        // input slots always start at index 0; the player inventory starts right after the station's slots.
        for (WorkstationKind kind : WorkstationKind.values()) {
            RecipeType<WorkstationRecipe> type =
                    kind == WorkstationKind.INDUSTRIAL_PRESS ? INDUSTRIAL_PRESS : BALLISTICS_BENCH;
            registration.addRecipeTransferHandler(
                    WorkstationMenu.class, MenuTypeInit.WORKSTATION_MENU.get(), type,
                    0, kind.inputSlots(), kind.totalSlots(), 36);
        }
    }
}
