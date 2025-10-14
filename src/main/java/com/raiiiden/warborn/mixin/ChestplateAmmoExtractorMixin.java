// TOUCHED: UPDATED
package com.raiiiden.warborn.mixin;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.raiiiden.warborn.common.object.capability.ChestplateBundleHandler;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Purpose: extractor for shell/tick reload path.
 * Only runs when base consume returned 0. No nullable boxing; method returns primitive int.
 */
@Mixin(targets = "com.tacz.guns.item.ModernKineticGunScriptAPI", remap = false)
public class ChestplateAmmoExtractorMixin {

    @Inject(method = "consumeAmmoFromPlayer", at = @At("RETURN"), cancellable = true)
    private void warborn$extractFromChestplateShellSafe(int needed, CallbackInfoReturnable<Integer> cir) {
        int base = cir.getReturnValue();
        if (base >= needed || needed <= 0) return; // only run if we still need ammo

        ModernKineticGunScriptAPIAccessor acc = (ModernKineticGunScriptAPIAccessor)(Object)this;
        if (!(acc.warborn$getShooter() instanceof Player p)) return;
        ItemStack gun = acc.warborn$getItemStack();
        if (gun == null) return;

        ItemStack chest = p.getInventory().getArmor(2);
        if (!(chest.getItem() instanceof WBArmorItem) || !WBArmorItem.isChestplateItem(chest)) return;

        int pulled = chest.getCapability(ForgeCapabilities.ITEM_HANDLER).map(h -> {
            int remain = needed - base; // fill only the shortfall
            for (int i = 0; i < h.getSlots() && remain > 0; i++) {
                ItemStack s = h.getStackInSlot(i);
                if (s.isEmpty()) continue;

                if (s.getItem() instanceof IAmmo ia && ia.isAmmoOfGun(gun, s)) {
                    int got = h.extractItem(i, remain, false).getCount();
                    remain -= got;
                } else if (s.getItem() instanceof IAmmoBox ib && ib.isAmmoBoxOfGun(gun, s)) {
                    int box = ib.getAmmoCount(s);
                    if (box > 0) {
                        int take = Math.min(box, remain);
                        ib.setAmmoCount(s, box - take);
                        if (box - take <= 0) ib.setAmmoId(s, DefaultAssets.EMPTY_AMMO_ID);
                        remain -= take;
                    }
                }
            }
            if (h instanceof ChestplateBundleHandler cbh) cbh.saveToItem(chest);
            return needed - remain; // total from chestplate
        }).orElse(0);

        if (pulled > 0) {
            cir.setReturnValue(base + pulled); // add chestplate ammo to base result
        }
    }
}
