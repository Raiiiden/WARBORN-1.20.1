package com.raiiiden.warborn.mixin.client;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.client.gui.overlay.GunHudOverlay;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(GunHudOverlay.class)
public class GunHudOverlayMixin {

    @Inject(method = "handleInventoryAmmo", at = @At("RETURN"), remap = false)
    private static void addChestplateBundleAmmo(ItemStack gunStack, Inventory inventory, CallbackInfo ci) {
        Player player = inventory.player;
        ItemStack chestplate = player.getInventory().getArmor(2);

        if (!(chestplate.getItem() instanceof WBArmorItem) || !WBArmorItem.isChestplateItem(chestplate)) {
            return;
        }

        chestplate.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent((IItemHandler handler) -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack slotStack = handler.getStackInSlot(i);
                Item item = slotStack.getItem();

                if (item instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(gunStack, slotStack)) {
                    GunHudOverlayAccessor.setCacheInventoryAmmoCount(
                            GunHudOverlayAccessor.getCacheInventoryAmmoCount() + slotStack.getCount()
                    );
                }

                if (item instanceof IAmmoBox iBox && iBox.isAmmoBoxOfGun(gunStack, slotStack)) {
                    GunHudOverlayAccessor.setCacheInventoryAmmoCount(
                            GunHudOverlayAccessor.getCacheInventoryAmmoCount() + iBox.getAmmoCount(slotStack)
                    );
                }
            }
        });
    }
}
