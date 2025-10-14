// TOUCHED: UPDATED
package com.raiiiden.warborn.mixin;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.util.AttachmentDataUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Purpose: only gate starting reload when the chestplate has ammo but player inventory is empty.
 * Removed ThreadLocal and any interception of AbstractGunItem.findAndExtractInventoryAmmo.
 */
@Mixin(AbstractGunItem.class)
public class ChestplateGunMixin {

    @Inject(method = "canReload", at = @At("RETURN"), cancellable = true, remap = false)
    private void warborn$enableReloadIfChestplateHasAmmo(LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        if (!(shooter instanceof Player player)) return;

        AbstractGunItem self = (AbstractGunItem)(Object)this;

        // magazine not full
        CommonGunIndex idx = TimelessAPI.getCommonGunIndex(self.getGunId(gunItem)).orElse(null);
        if (idx == null) return;
        int current = self.getCurrentAmmoCount(gunItem);
        int max = AttachmentDataUtils.getAmmoCountWithAttachment(gunItem, idx.getGunData());
        if (current >= max) return;

        ItemStack chest = player.getInventory().getArmor(2);
        Item item = chest.getItem();
        if (!(item instanceof WBArmorItem) || !WBArmorItem.isChestplateItem(chest)) return;

        boolean hasAmmo = chest.getCapability(ForgeCapabilities.ITEM_HANDLER).map(h -> {
            for (int i = 0; i < h.getSlots(); i++) {
                ItemStack s = h.getStackInSlot(i);
                if (s.isEmpty()) continue;
                Item si = s.getItem();
                if (si instanceof IAmmo ia && ia.isAmmoOfGun(gunItem, s)) return true;
                if (si instanceof IAmmoBox ib && ib.isAmmoBoxOfGun(gunItem, s) && ib.getAmmoCount(s) > 0) return true;
            }
            return false;
        }).orElse(false);

        if (hasAmmo) cir.setReturnValue(true);
    }
}
