package com.raiiiden.warborn.mixin.client;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.tacz.guns.client.animation.statemachine.GunAnimationStateContext", remap = false)
public abstract class ChestplateClientStateMachineMixin {

    @Shadow
    private ItemStack currentGunItem;

    @Inject(method = "hasAmmoToConsume", at = @At("HEAD"), cancellable = true)
    private void warborn$clientHasChestplateAmmo(CallbackInfoReturnable<Boolean> cir) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || currentGunItem == null) return;

        ItemStack chest = player.getInventory().getArmor(2);
        if (!(chest.getItem() instanceof WBArmorItem) || !WBArmorItem.isChestplateItem(chest)) {
            return;
        }

        boolean hasChestAmmo = chest.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                if (stack.getItem() instanceof IAmmo ammo && ammo.isAmmoOfGun(currentGunItem, stack)) {
                    return true;
                }
                if (stack.getItem() instanceof IAmmoBox box && box.isAmmoBoxOfGun(currentGunItem, stack)) {
                    return box.getAmmoCount(stack) > 0;
                }
            }
            return false;
        }).orElse(false);

        if (hasChestAmmo) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}