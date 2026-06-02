package com.raiiiden.warborn.mixin;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.raiiiden.warborn.common.object.capability.ChestplateBundleHandler;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.tacz.guns.item.ModernKineticGunScriptAPI", remap = false)
public abstract class ChestplateScriptAPIMixin {

    @Shadow
    protected abstract LivingEntity getShooter();

    @Shadow
    protected abstract ItemStack getItemStack();

    @Inject(method = "hasAmmoToConsume", at = @At("HEAD"), cancellable = true, remap = false)
    private void warborn$hasAmmoInChestplate(CallbackInfoReturnable<Boolean> cir) {
        Player player = getPlayerOrNull();
        if (player == null) return;
        ItemStack gun = getItemStack();
        if (gun == null) return;

        ItemStack chest = player.getInventory().getArmor(2);
        if (!isValidChestplate(chest)) return;

        // Check chestplate first, before base runs
        boolean hasChestAmmo = chest.getCapability(ForgeCapabilities.ITEM_HANDLER).map(h -> {
            for (int i = 0; i < h.getSlots(); i++) {
                ItemStack s = h.getStackInSlot(i);
                if (s.isEmpty()) continue;
                if (s.getItem() instanceof IAmmo ia && ia.isAmmoOfGun(gun, s)) return true;
                if (s.getItem() instanceof IAmmoBox ib && ib.isAmmoBoxOfGun(gun, s) && ib.getAmmoCount(s) > 0) return true;
            }
            return false;
        }).orElse(false);

        // If chestplate has ammo, immediately return true to prevent state machine from canceling
        if (hasChestAmmo) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "consumeAmmoFromPlayer", at = @At("RETURN"), cancellable = true)
    private void warborn$extractFromChestplate(int needed, CallbackInfoReturnable<Integer> cir) {
        int baseConsumed = cir.getReturnValue();
        // Only extract from chestplate if we still need ammo
        if (baseConsumed >= needed || needed <= 0) return;

        Player player = getPlayerOrNull();
        if (player == null) return;
        ItemStack gun = getItemStack();
        if (gun == null) return;

        ItemStack chest = player.getInventory().getArmor(2);
        if (!isValidChestplate(chest)) return;

        int extracted = chest.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
            int remaining = needed - baseConsumed; // Only fill the shortfall
            int total = 0;

            for (int i = 0; i < handler.getSlots() && remaining > 0; i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                // Handle loose ammo
                if (stack.getItem() instanceof IAmmo ammo && ammo.isAmmoOfGun(gun, stack)) {
                    int got = handler.extractItem(i, remaining, false).getCount();
                    total += got;
                    remaining -= got;
                }
                // Handle ammo boxes
                else if (stack.getItem() instanceof IAmmoBox box && box.isAmmoBoxOfGun(gun, stack)) {
                    int boxCount = box.getAmmoCount(stack);
                    if (boxCount > 0) {
                        int take = Math.min(boxCount, remaining);
                        box.setAmmoCount(stack, boxCount - take);
                        if (boxCount - take <= 0) {
                            box.setAmmoId(stack, DefaultAssets.EMPTY_AMMO_ID);
                        }
                        total += take;
                        remaining -= take;
                    }
                }
            }

            if (handler instanceof ChestplateBundleHandler bundleHandler) {
                bundleHandler.saveToItem(chest);
            }

            return total;
        }).orElse(0);

        if (extracted > 0) {
            cir.setReturnValue(baseConsumed + extracted);
        }
    }

    private Player getPlayerOrNull() {
        LivingEntity shooter = getShooter();
        return shooter instanceof Player p ? p : null;
    }

    private static boolean isValidChestplate(ItemStack stack) {
        return stack.getItem() instanceof WBArmorItem && WBArmorItem.isChestplateItem(stack);
    }
}