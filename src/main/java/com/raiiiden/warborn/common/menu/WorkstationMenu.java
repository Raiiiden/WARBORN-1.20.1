package com.raiiiden.warborn.common.menu;

import com.raiiiden.warborn.common.block.WorkstationBlock.WorkstationKind;
import com.raiiiden.warborn.common.blockentity.WorkstationBlockEntity;
import com.raiiiden.warborn.common.init.MenuTypeInit;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;

// One menu for both stations; the kind drives the slot layout instead of a subclass and MenuType per station.
public class WorkstationMenu extends AbstractContainerMenu {
    // Where the player inventory starts, shared by both station layouts.
    public static final int INVENTORY_X = 8;
    public static final int INVENTORY_Y = 84;

    private final Container container;
    private final WorkstationKind kind;
    private final ContainerData data;

    // Client side: rebuilt from the buffer the block entity wrote when the screen was opened.
    public WorkstationMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory, OpenData.read(inventory, buf));
    }

    private WorkstationMenu(int id, Inventory inventory, OpenData open) {
        this(id, inventory, open.kind(), open.container(), new SimpleContainerData(WorkstationBlockEntity.DATA_SIZE));
    }

    public WorkstationMenu(int id, Inventory inventory, WorkstationKind kind, Container container, ContainerData data) {
        super(MenuTypeInit.WORKSTATION_MENU.get(), id);
        this.kind = kind;
        this.container = container;
        this.data = data;

        checkContainerSize(container, kind.totalSlots());
        checkContainerDataCount(data, WorkstationBlockEntity.DATA_SIZE);
        container.startOpen(inventory.player);

        addStationSlots();
        addPlayerInventory(inventory);
        addDataSlots(data);
    }

    // What the block entity wrote on open: position then kind. The kind is explicit because the client may not have that block entity yet.
    private record OpenData(WorkstationKind kind, Container container) {
        static OpenData read(Inventory inventory, FriendlyByteBuf buf) {
            BlockPos pos = buf.readBlockPos();
            WorkstationKind kind = WorkstationKind.byOrdinal(buf.readByte());
            Container container = inventory.player.level().getBlockEntity(pos) instanceof WorkstationBlockEntity station
                    ? station
                    : new SimpleContainer(kind.totalSlots());
            return new OpenData(kind, container);
        }
    }

    private void addStationSlots() {
        switch (kind) {
            case INDUSTRIAL_PRESS -> {
                for (int i = 0; i < 3; i++) {
                    addSlot(new Slot(container, i, 26 + i * 18, 17));
                }
                addSlot(new FuelSlot(container, kind.fuelSlot(), 44, 56));
                addSlot(new OutputSlot(container, kind.outputSlot(), 122, 33));
            }
            case BALLISTICS_BENCH -> {
                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        addSlot(new Slot(container, col + row * 3, 30 + col * 18, 17 + row * 18));
                    }
                }
                addSlot(new OutputSlot(container, kind.outputSlot(), 124, 33));
            }
        }
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, INVENTORY_X + col * 18, INVENTORY_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, INVENTORY_X + col * 18, INVENTORY_Y + 58));
        }
    }

    public WorkstationKind getKind() {
        return kind;
    }

    // 0 when idle, otherwise how far along the current craft is, scaled to {@code width} pixels.
    public int getProgressScaled(int width) {
        int progress = data.get(WorkstationBlockEntity.DATA_PROGRESS);
        int max = data.get(WorkstationBlockEntity.DATA_MAX_PROGRESS);
        return max <= 0 ? 0 : Math.min(width, progress * width / max);
    }

    public int getFuelScaled(int height) {
        int burn = data.get(WorkstationBlockEntity.DATA_BURN);
        int max = data.get(WorkstationBlockEntity.DATA_MAX_BURN);
        return max <= 0 ? 0 : Math.min(height, burn * height / max);
    }

    public boolean isBurning() {
        return data.get(WorkstationBlockEntity.DATA_BURN) > 0;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return container.stillValid(player);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        container.stopOpen(player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        int stationEnd = kind.totalSlots();
        int inventoryStart = stationEnd;
        int hotbarStart = inventoryStart + 27;
        int end = slots.size();

        if (index < stationEnd) {
            // Out of the station and into the player, hotbar first so the item lands somewhere visible.
            if (!moveItemStackTo(stack, inventoryStart, end, true)) return ItemStack.EMPTY;
            if (index == kind.outputSlot()) slot.onQuickCraft(stack, original);
        } else {
            boolean moved = false;
            if (kind.hasFuel() && ForgeHooks.getBurnTime(stack, null) > 0) {
                moved = moveItemStackTo(stack, kind.fuelSlot(), kind.fuelSlot() + 1, false);
            }
            if (!moved) {
                moved = moveItemStackTo(stack, 0, kind.inputSlots(), false);
            }
            if (!moved) {
                // Fall through to the usual inventory/hotbar shuffle.
                if (index < hotbarStart) {
                    moved = moveItemStackTo(stack, hotbarStart, end, false);
                } else {
                    moved = moveItemStackTo(stack, inventoryStart, hotbarStart, false);
                }
            }
            if (!moved) return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) return ItemStack.EMPTY;

        slot.onTake(player, stack);
        return original;
    }

    private static class OutputSlot extends Slot {
        OutputSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return false;
        }
    }

    private static class FuelSlot extends Slot {
        FuelSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return ForgeHooks.getBurnTime(stack, null) > 0;
        }
    }
}
