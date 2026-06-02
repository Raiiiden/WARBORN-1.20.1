package com.raiiiden.warborn.common.object;

import com.raiiiden.warborn.common.init.MenuTypeInit;
import com.raiiiden.warborn.common.item.BackpackItem;
import com.raiiiden.warborn.common.object.capability.BackpackItemStackHandler;
import com.raiiiden.warborn.common.object.slot.DynamicBackpackSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import top.theillusivec4.curios.api.CuriosApi;

public class BackpackMenu extends AbstractContainerMenu {

    public static final int COLUMNS = 9;

    private final BackpackItemStackHandler handler;
    private final ItemStack backpackStack;
    private final int visibleRows;
    private final int totalRows;
    private int scrollRow = 0;

    public BackpackMenu(int id, Inventory inv, FriendlyByteBuf buf) {
        this(id, inv, buf.readItem());
    }

    public BackpackMenu(int id, Inventory inv, ItemStack stack) {
        super(MenuTypeInit.BACKPACK_MENU.get(), id);

        this.backpackStack = stack;

        this.handler = stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(h -> (BackpackItemStackHandler) h)
                .orElse(null);

        if (this.handler == null) {
            throw new IllegalStateException("Backpack has no item handler");
        }

        int tier = BackpackItem.getTier(stack);
        this.visibleRows = BackpackItem.getVisibleRowsForTier(tier);
        this.totalRows   = BackpackItem.getSlotsForTier(tier) / COLUMNS;

        int startX = 8;
        int startY = 18;

        // Visible backpack slots (scroll remaps to real handler indices)
        for (int r = 0; r < visibleRows; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                final int row = r;
                final int col = c;
                // FIX: pass a unique slotIndex (r*COLUMNS+c) so that Minecraft's hover
                // detection treats each DynamicBackpackSlot as a distinct slot identity.
                // Previously all slots shared index 0 on DummyContainer, which caused
                // the tooltip system to fail to resolve the correct hovered slot.
                final int slotNum = r * COLUMNS + c;
                addSlot(new DynamicBackpackSlot(
                        handler,
                        () -> getSlotIndex(row, col),
                        startX + col * 18,
                        startY + row * 18,
                        slotNum
                ));
            }
        }

        // Player inventory
        int invY = visibleRows * 18 + 31;

        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlot(new Slot(inv, c + r * 9 + 9, startX + c * 18, invY + r * 18));

        for (int c = 0; c < 9; c++)
            addSlot(new Slot(inv, c, startX + c * 18, invY + 58));
    }

    private int getSlotIndex(int row, int col) {
        return (scrollRow + row) * COLUMNS + col;
    }

    public void setScrollRow(int row) {
        this.scrollRow = Math.max(0, Math.min(row, getMaxScrollRow()));
    }

    public void setScrollRowServer(int row) {
        this.scrollRow = Math.max(0, Math.min(row, getMaxScrollRow()));
    }

    public int getScrollRow()    { return scrollRow; }
    public int getVisibleRows()  { return visibleRows; }
    public int getMaxScrollRow() { return Math.max(0, totalRows - visibleRows); }

    @Override
    public boolean stillValid(Player player) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i) == backpackStack) return true;
        }
        if (player.getItemBySlot(EquipmentSlot.CHEST) == backpackStack) return true;

        var curiosOpt = CuriosApi.getCuriosInventory(player);
        if (curiosOpt.isPresent()) {
            var curios = curiosOpt.resolve().get();
            for (var entry : curios.getCurios().values()) {
                var stacks = entry.getStacks();
                for (int i = 0; i < stacks.getSlots(); i++) {
                    if (stacks.getStackInSlot(i) == backpackStack) return true;
                }
            }
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack empty = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) return empty;

        ItemStack stack = slot.getItem();
        ItemStack copy  = stack.copy();

        int backpackSlots = visibleRows * COLUMNS;

        if (index < backpackSlots) {
            if (!moveItemStackTo(stack, backpackSlots, slots.size(), true))
                return empty;
        } else {
            if (!moveItemStackTo(stack, 0, backpackSlots, false))
                return empty;
        }

        if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        return copy;
    }
}