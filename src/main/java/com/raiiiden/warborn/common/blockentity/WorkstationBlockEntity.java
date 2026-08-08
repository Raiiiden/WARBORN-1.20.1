package com.raiiiden.warborn.common.blockentity;

import com.raiiiden.warborn.common.block.WorkstationBlock;
import com.raiiiden.warborn.common.block.WorkstationBlock.WorkstationKind;
import com.raiiiden.warborn.common.crafting.WorkstationRecipe;
import com.raiiiden.warborn.common.init.ModBlockEntityRegistry;
import com.raiiiden.warborn.common.init.ModRecipeRegistry;
import com.raiiiden.warborn.common.menu.WorkstationMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorkstationBlockEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_MAX_PROGRESS = 1;
    public static final int DATA_BURN = 2;
    public static final int DATA_MAX_BURN = 3;
    public static final int DATA_SIZE = 4;

    // Block update + client notify, the flags every vanilla machine uses when toggling its lit state.
    private static final int LIT_UPDATE_FLAGS = 3;

    private final WorkstationKind kind;
    private NonNullList<ItemStack> items;

    private int progress;
    private int maxProgress;
    private int burnTime;
    private int maxBurnTime;

    // Recipe lookups scan every recipe of the type, so they only rerun when the inputs actually change.
    private boolean inputsDirty = true;
    @Nullable
    private WorkstationRecipe cachedRecipe;

    private final LazyOptional<?>[] itemHandlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

    private final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DATA_PROGRESS -> progress;
                case DATA_MAX_PROGRESS -> maxProgress;
                case DATA_BURN -> burnTime;
                case DATA_MAX_BURN -> maxBurnTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DATA_PROGRESS -> progress = value;
                case DATA_MAX_PROGRESS -> maxProgress = value;
                case DATA_BURN -> burnTime = value;
                case DATA_MAX_BURN -> maxBurnTime = value;
                default -> { }
            }
        }

        @Override
        public int getCount() {
            return DATA_SIZE;
        }
    };

    public WorkstationBlockEntity(WorkstationKind kind, BlockPos pos, BlockState state) {
        super(ModBlockEntityRegistry.typeFor(kind), pos, state);
        this.kind = kind;
        this.items = NonNullList.withSize(kind.totalSlots(), ItemStack.EMPTY);
    }

    public WorkstationKind getKind() {
        return kind;
    }

    public ContainerData getData() {
        return data;
    }

    // ------------------------------------------------------------------ ticking

    public static void serverTick(Level level, BlockPos pos, BlockState state, WorkstationBlockEntity station) {
        boolean changed = false;
        boolean wasLit = station.isWorking();

        if (station.burnTime > 0) {
            station.burnTime--;
            changed = true;
        }

        WorkstationRecipe recipe = station.activeRecipe();
        if (recipe != null && station.canOutput(recipe)) {
            station.maxProgress = recipe.processTime();

            if (station.kind.hasFuel() && station.burnTime <= 0) {
                changed |= station.consumeFuel();
            }

            if (!station.kind.hasFuel() || station.burnTime > 0) {
                station.progress++;
                changed = true;
                if (station.progress >= station.maxProgress) {
                    station.craft(recipe);
                    station.progress = 0;
                }
            }
        } else if (station.progress != 0 || station.maxProgress != 0) {
            station.progress = 0;
            station.maxProgress = 0;
            changed = true;
        }

        boolean lit = station.isWorking();
        if (lit != wasLit) {
            level.setBlock(pos, state.setValue(WorkstationBlock.LIT, lit), LIT_UPDATE_FLAGS);
            changed = true;
        }

        if (changed) {
            setChanged(level, pos, state);
        }
    }

    // A station counts as working when the player can see something happening.
    private boolean isWorking() {
        return progress > 0 || burnTime > 0;
    }

    private boolean consumeFuel() {
        ItemStack fuel = items.get(kind.fuelSlot());
        int burn = ForgeHooks.getBurnTime(fuel, null);
        if (burn <= 0) return false;

        burnTime = burn;
        maxBurnTime = burn;
        ItemStack remainder = fuel.getCraftingRemainingItem();
        fuel.shrink(1);
        if (fuel.isEmpty()) {
            items.set(kind.fuelSlot(), remainder.isEmpty() ? ItemStack.EMPTY : remainder);
        }
        inputsDirty = true;
        return true;
    }

    // The recipe the current inputs satisfy, or null; it sees input slots only, and takes the greediest match when one recipe's ingredients are a subset of another's.
    @Nullable
    private WorkstationRecipe activeRecipe() {
        if (level == null) return null;
        if (!inputsDirty) return cachedRecipe;

        inputsDirty = false;
        cachedRecipe = null;
        for (WorkstationRecipe candidate : level.getRecipeManager()
                .getRecipesFor(ModRecipeRegistry.typeFor(kind), inputView(), level)) {
            if (cachedRecipe == null || candidate.totalIngredientCount() > cachedRecipe.totalIngredientCount()) {
                cachedRecipe = candidate;
            }
        }
        return cachedRecipe;
    }

    // A container over the same ItemStack instances as the input slots, so consuming through it shrinks the real stacks.
    private SimpleContainer inputView() {
        ItemStack[] inputs = new ItemStack[kind.inputSlots()];
        for (int slot = 0; slot < inputs.length; slot++) {
            inputs[slot] = items.get(slot);
        }
        return new SimpleContainer(inputs);
    }

    // Only reached from the tick, which has already established a non-null level via {@link #activeRecipe}.
    private boolean canOutput(WorkstationRecipe recipe) {
        // assemble() rather than getResultItem(), because a battery charge recipe builds its result out of
        // the battery that's actually in the slots.
        ItemStack result = recipe.assemble(inputView(), level.registryAccess());
        if (result.isEmpty()) return false;

        ItemStack output = items.get(kind.outputSlot());
        if (output.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(output, result)) return false;
        return output.getCount() + result.getCount() <= Math.min(output.getMaxStackSize(), getMaxStackSize());
    }

    private void craft(WorkstationRecipe recipe) {
        // Assembled before consuming, since consuming shrinks the very stacks the result is derived from.
        ItemStack result = recipe.assemble(inputView(), level.registryAccess()).copy();
        recipe.consume(inputView());

        // consume() shrinks in place, which can leave zero-count stacks behind.
        for (int slot = 0; slot < kind.inputSlots(); slot++) {
            if (items.get(slot).isEmpty()) items.set(slot, ItemStack.EMPTY);
        }

        ItemStack output = items.get(kind.outputSlot());
        if (output.isEmpty()) {
            items.set(kind.outputSlot(), result);
        } else {
            output.grow(result.getCount());
        }
        inputsDirty = true;
    }

    // ------------------------------------------------------------------ menu

    @Override
    public Component getDisplayName() {
        return kind.title();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new WorkstationMenu(id, inventory, kind, this, data);
    }

    public void writeScreenOpeningData(FriendlyByteBuf buf) {
        buf.writeBlockPos(worldPosition);
        buf.writeByte(kind.ordinal());
    }

    // ------------------------------------------------------------------ saving

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, items);
        tag.putInt("Progress", progress);
        tag.putInt("MaxProgress", maxProgress);
        tag.putInt("BurnTime", burnTime);
        tag.putInt("MaxBurnTime", maxBurnTime);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        items = NonNullList.withSize(kind.totalSlots(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, items);
        progress = tag.getInt("Progress");
        maxProgress = tag.getInt("MaxProgress");
        burnTime = tag.getInt("BurnTime");
        maxBurnTime = tag.getInt("MaxBurnTime");
        inputsDirty = true;
    }

    // ------------------------------------------------------------------ container

    @Override
    public int getContainerSize() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        ItemStack stack = ContainerHelper.removeItem(items, slot, amount);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = ContainerHelper.takeItem(items, slot);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        items.set(slot, stack);
        if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
        setChanged();
    }

    @Override
    public void setChanged() {
        inputsDirty = true;
        super.setChanged();
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        if (slot == kind.outputSlot()) return false;
        if (kind.hasFuel() && slot == kind.fuelSlot()) return ForgeHooks.getBurnTime(stack, null) > 0;
        return true;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return net.minecraft.world.Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }

    // ------------------------------------------------------------------ automation

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        if (side == Direction.DOWN) return new int[]{kind.outputSlot()};
        if (side == Direction.UP) return inputSlotIndices();
        // The sides feed fuel on a station that burns it, and inputs on one that doesn't.
        return kind.hasFuel() ? new int[]{kind.fuelSlot()} : inputSlotIndices();
    }

    private int[] inputSlotIndices() {
        int[] slots = new int[kind.inputSlots()];
        for (int i = 0; i < slots.length; i++) slots[i] = i;
        return slots;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NotNull ItemStack stack, @Nullable Direction side) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NotNull ItemStack stack, @NotNull Direction side) {
        return slot == kind.outputSlot();
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (!remove && side != null && capability == ForgeCapabilities.ITEM_HANDLER) {
            return switch (side) {
                case UP -> itemHandlers[0].cast();
                case DOWN -> itemHandlers[1].cast();
                default -> itemHandlers[2].cast();
            };
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        for (LazyOptional<?> handler : itemHandlers) {
            handler.invalidate();
        }
    }
}
