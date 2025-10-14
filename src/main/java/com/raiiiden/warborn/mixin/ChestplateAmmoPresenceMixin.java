package com.raiiiden.warborn.mixin;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.tacz.guns.item.ModernKineticGunScriptAPI", remap = false)
public class ChestplateAmmoPresenceMixin {
    @Inject(method = "hasAmmoToConsume", at = @At("HEAD"), cancellable = true)
    private void warborn$hasAmmoInChestplate(CallbackInfoReturnable<Boolean> cir) {
        ModernKineticGunScriptAPIAccessor acc = (ModernKineticGunScriptAPIAccessor)(Object)this;
        if (!(acc.warborn$getShooter() instanceof Player p)) return;
        ItemStack gun = acc.warborn$getItemStack();
        if (gun == null) return;

        ItemStack chest = p.getInventory().getArmor(2);
        if (!(chest.getItem() instanceof WBArmorItem) || !WBArmorItem.isChestplateItem(chest)) return;

        boolean ok = chest.getCapability(ForgeCapabilities.ITEM_HANDLER).map(h -> {
            for (int i = 0; i < h.getSlots(); i++) {
                ItemStack s = h.getStackInSlot(i);
                if (s.isEmpty()) continue;
                if (s.getItem() instanceof IAmmo ia && ia.isAmmoOfGun(gun, s)) return true;
                if (s.getItem() instanceof IAmmoBox ib && ib.isAmmoBoxOfGun(gun, s) && ib.getAmmoCount(s) > 0) return true;
            }
            return false;
        }).orElse(false);

        if (ok) {
            cir.setReturnValue(true);
            cir.cancel(); // stop original logic from running
        }
    }
}
