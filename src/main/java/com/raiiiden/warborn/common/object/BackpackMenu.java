package com.raiiiden.warborn.common.object;

import com.raiiiden.warborn.common.init.MenuTypeInit;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class BackpackMenu extends AbstractContainerMenu {
    private final ItemStack itemStack;

    public BackpackMenu(int pContainerId, Inventory pPlayerInventory, FriendlyByteBuf buf) {
        this(pContainerId, pPlayerInventory, getBackpackItem(pPlayerInventory));
    }

    public BackpackMenu(int pContainerId, Inventory pPlayerInventory, ItemStack itemStack) {
        super(MenuTypeInit.BACKPACK_MENU.get(), pContainerId);
        this.itemStack = itemStack;
        IItemHandler handler = itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);

        int startX = 8;  // X position of the first slot

        // Set a specific Y position for the backpack slots
        int backpackStartY = 18;  // Y position of the first backpack slot (adjust this to move the backpack slots up/down)

        // Add 3x9 grid for the backpack (3 rows, 9 columns)
        for (int row = 0; row < 3; ++row) {  // 3 rows
            for (int col = 0; col < 9; ++col) {  // 9 columns
                int index = col + row * 9;
                this.addSlot(new SlotItemHandler(handler, index, startX + col * 18, backpackStartY + row * 18)); // Backpack Y position is independent
            }
        }

        // Set a different Y position for the player's inventory slots
        int inventoryStartY = backpackStartY + 66; // Offset for the player's inventory below the backpack slots

        // Add player's inventory slots (3 rows, 9 columns)
        for (int l = 0; l < 3; ++l) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(pPlayerInventory, k + l * 9 + 9, 8 + k * 18, inventoryStartY + l * 18));
            }
        }

        // Add player's hotbar slots (1 row, 9 columns)
        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlot(new Slot(pPlayerInventory, i1, 8 + i1 * 18, inventoryStartY + 58));
        }
    }

    private static ItemStack getBackpackItem(Inventory inv) {
        ItemStack main = inv.player.getMainHandItem();
        ItemStack off = inv.player.getOffhandItem();
        return isBackpack(main) ? main : isBackpack(off) ? off : ItemStack.EMPTY;
    }

    private static boolean isBackpack(ItemStack stack) {
        return stack.getItem() instanceof WarbornArmorItem item && item.getArmorType().contains("backpack");
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        IItemHandler handler = this.itemStack.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < handler.getSlots()) {
                if (!this.moveItemStackTo(itemstack1, handler.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, handler.getSlots(), false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }
}
