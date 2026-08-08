package com.raiiiden.warborn.mixin.jei;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.raiiiden.warborn.WARBORN;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.library.config.RecipeCategorySortingConfig;
import mezz.jei.library.recipes.RecipeManagerInternal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.Comparator;

// Puts this mod's recipe categories at the front of JEI's category list.
//
// JEI has no API for this. The order comes from the user's recipe-category-sort-order.ini,
// and where a category has no saved position JEI falls back to crafting, then anything under
// minecraft, then alphabetical - which leaves our stations at the bottom either way. This
// wraps the comparator JEI builds in RecipeManagerInternal so ours are ranked first and the
// rest of JEI's ordering is preserved untouched below that.
//
// Only loaded when JEI is installed; see JeiMixinPlugin.
@Mixin(value = RecipeManagerInternal.class, remap = false)
public class RecipeCategoryOrderMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(value = "INVOKE",
                    target = "Lmezz/jei/library/config/RecipeCategorySortingConfig;"
                            + "getComparator(Ljava/util/Collection;)Ljava/util/Comparator;"),
            remap = false)
    private Comparator<RecipeType<?>> warborn$sortOurCategoriesFirst(
            RecipeCategorySortingConfig config,
            Collection<RecipeType<?>> recipeTypes,
            Operation<Comparator<RecipeType<?>>> original) {

        Comparator<RecipeType<?>> jeiOrder = original.call(config, recipeTypes);
        return Comparator
                .comparingInt((RecipeType<?> type) -> warborn$isOurs(type) ? 0 : 1)
                .thenComparing(jeiOrder);
    }

    private static boolean warborn$isOurs(RecipeType<?> type) {
        return WARBORN.MODID.equals(type.getUid().getNamespace());
    }
}
