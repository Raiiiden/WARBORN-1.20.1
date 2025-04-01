package com.raiiiden.warborn.common.event;

import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "warborn", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WarbornArmorInteractions {

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        ItemStack held = event.getItemStack();

        if (player.isShiftKeyDown() && held.getItem() instanceof WarbornArmorItem) {
            WarbornArmorItem.removeItem(held).ifPresent(removed -> {
                if (!player.getInventory().add(removed)) {
                    player.drop(removed, false);
                }
                player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 1.0F, 1.0F);
                event.setCanceled(true);
                event.setCancellationResult(InteractionResult.SUCCESS);
            });
            return;
        }

        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chestplate.getItem() instanceof WarbornArmorItem)) return;
        if (!held.getItem().canFitInsideContainerItems()) return;

        int canAdd = WarbornArmorItem.getAvailableSpace(chestplate, held);
        if (canAdd <= 0) return;

        ItemStack toInsert = held.copyWithCount(Math.min(canAdd, held.getCount()));
        int inserted = WarbornArmorItem.addItems(chestplate, toInsert);

        if (inserted > 0) {
            held.shrink(inserted);
            player.setItemInHand(event.getHand(), held.isEmpty() ? ItemStack.EMPTY : held);
            player.playSound(SoundEvents.BUNDLE_INSERT, 1.0F, 1.0F);
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @SubscribeEvent
    public static void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;

        if (!player.isShiftKeyDown()) return;

        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof WarbornArmorItem)) return;

        WarbornArmorItem.removeItem(held).ifPresent(removed -> {
            if (!player.getInventory().add(removed)) {
                player.drop(removed, false);
            }
            player.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 1.0F, 1.0F);
        });
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }
}
