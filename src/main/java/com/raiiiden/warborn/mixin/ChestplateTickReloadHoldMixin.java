// TOUCHED: NEW
package com.raiiiden.warborn.mixin;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.ReloadState;
import com.tacz.guns.api.entity.ReloadState.StateType;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.util.AttachmentDataUtils;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.tacz.guns.item.ModernKineticGunItem", remap = false)
public class ChestplateTickReloadHoldMixin {

    @Inject(method = "tickReload", at = @At("RETURN"), cancellable = true)
    private void warborn$holdFeedingWhenChestplateHasAmmo(ShooterDataHolder dataHolder, ItemStack gunItem, LivingEntity shooter, CallbackInfoReturnable<ReloadState> cir) {
        ReloadState out = cir.getReturnValue();
        StateType prev = dataHolder.reloadStateType;
        if (prev != StateType.EMPTY_RELOAD_FEEDING && prev != StateType.TACTICAL_RELOAD_FEEDING) return;
        if (out.getStateType() != StateType.NOT_RELOADING) return;
        if (!(gunItem.getItem() instanceof AbstractGunItem gun)) return;

        CommonGunIndex idx = TimelessAPI.getCommonGunIndex(gun.getGunId(gunItem)).orElse(null);
        if (idx == null) return;

        int current = gun.getCurrentAmmoCount(gunItem);
        int max = AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, idx.getGunData());
        if (current >= max) return;

        if (!(shooter instanceof Player p)) return;
        ItemStack chest = p.getInventory().getArmor(2);
        if (!(chest.getItem() instanceof WBArmorItem) || !WBArmorItem.isChestplateItem(chest)) return;

        boolean hasChestAmmo = chest.getCapability(ForgeCapabilities.ITEM_HANDLER).map(h -> {
            for (int i = 0; i < h.getSlots(); i++) {
                ItemStack s = h.getStackInSlot(i);
                if (s.isEmpty()) continue;
                if (s.getItem() instanceof IAmmo ia && ia.isAmmoOfGun(gunItem, s)) return true;
                if (s.getItem() instanceof IAmmoBox ib && ib.isAmmoBoxOfGun(gunItem, s) && ib.getAmmoCount(s) > 0) return true;
            }
            return false;
        }).orElse(false);

        if (!hasChestAmmo) return;

        ReloadState keep = new ReloadState();
        keep.setStateType(prev);
        keep.setCountDown(50L); // small positive ms to keep feed ticking
        cir.setReturnValue(keep);
    }
}
