package com.raiiiden.warborn.compat.jei;

import com.raiiiden.warborn.common.block.WorkstationBlock.WorkstationKind;
import com.raiiiden.warborn.common.crafting.BatteryChargeRecipe;
import com.raiiiden.warborn.common.crafting.SizedIngredient;
import com.raiiiden.warborn.common.crafting.WorkstationRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

// One category for both stations, parameterised by kind; uses JEI's own slot and arrow drawables rather than slicing the station GUI texture.
public class WorkstationRecipeCategory implements mezz.jei.api.recipe.category.IRecipeCategory<WorkstationRecipe> {
    // Ingredients wrap onto a new row after this many, matching the bench's 3x3 grid.
    private static final int COLUMNS = 3;
    private static final int SLOT = 18;
    private static final int GRID_X = 1;
    private static final int GRID_Y = 3;
    private static final int ARROW_X = 62;
    private static final int OUTPUT_X = 96;
    private static final int WIDTH = 124;

    private final WorkstationKind kind;
    private final RecipeType<WorkstationRecipe> recipeType;
    private final Component title;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    @Nullable
    private final IDrawableAnimated flame;
    private final int rows;

    public WorkstationRecipeCategory(WorkstationKind kind, RecipeType<WorkstationRecipe> recipeType,
                                     IGuiHelper guiHelper, ItemLike block) {
        this.kind = kind;
        this.recipeType = recipeType;
        this.title = kind.title();
        this.icon = guiHelper.createDrawableItemLike(block);
        // The animation length is cosmetic; it does not have to match any particular recipe's process time.
        this.arrow = guiHelper.createAnimatedRecipeArrow(kind.defaultProcessTime());
        this.flame = kind.hasFuel() ? guiHelper.createAnimatedRecipeFlame(300) : null;
        // The press only ever shows one row; the bench can wrap to three.
        this.rows = kind == WorkstationKind.INDUSTRIAL_PRESS ? 1 : 3;
    }

    @Override
    public @NotNull RecipeType<WorkstationRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public @NotNull Component getTitle() {
        return title;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return GRID_Y + rows * SLOT + (kind.hasFuel() ? 18 : 3);
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull WorkstationRecipe recipe,
                          @NotNull IFocusGroup focuses) {
        List<SizedIngredient> ingredients = recipe.sizedIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            int x = GRID_X + (i % COLUMNS) * SLOT;
            int y = GRID_Y + (i / COLUMNS) * SLOT;
            builder.addInputSlot(x, y)
                    .setStandardSlotBackground()
                    // Stacks are pre-scaled to the required count, so the page shows "x4" rather than
                    // four separate slots the way the old repeat-the-entry format did.
                    .addItemStacks(Arrays.asList(ingredients.get(i).displayStacks()));
        }

        builder.addOutputSlot(OUTPUT_X, centreY())
                .setOutputSlotBackground()
                .addItemStack(outputStack(recipe));

        // Inputs are matched as an unordered bag, so JEI should not imply a required arrangement.
        builder.setShapeless();
    }

    @Override
    public void draw(@NotNull WorkstationRecipe recipe, @NotNull IRecipeSlotsView slots,
                     @NotNull GuiGraphics graphics, double mouseX, double mouseY) {
        arrow.draw(graphics, ARROW_X, centreY());
        if (flame != null) {
            flame.draw(graphics, ARROW_X + 5, centreY() + SLOT + 4);
        }
    }

    @Override
    public void getTooltip(@NotNull ITooltipBuilder tooltip, @NotNull WorkstationRecipe recipe,
                           @NotNull IRecipeSlotsView slots, double mouseX, double mouseY) {
        boolean overArrow = mouseX >= ARROW_X && mouseX < ARROW_X + 24
                && mouseY >= centreY() && mouseY < centreY() + 17;
        if (!overArrow) return;

        tooltip.add(Component.translatable("gui.fracturepoint.jei.time", recipe.processTime() / 20.0f));
        if (recipe instanceof BatteryChargeRecipe charge) {
            // The recipe page can't show this any other way: the result stack looks like a plain battery.
            tooltip.add(Component.translatable("gui.fracturepoint.jei.charge", charge.energyPerCatalyst()));
        }
    }

    private static ItemStack outputStack(WorkstationRecipe recipe) {
        // Null registry access is safe here: this recipe's result is a stored stack, not looked up.
        return recipe.getResultItem(null);
    }

    // Vertically centres the arrow and output against however many ingredient rows are shown.
    private int centreY() {
        return GRID_Y + (rows * SLOT) / 2 - 8;
    }
}
