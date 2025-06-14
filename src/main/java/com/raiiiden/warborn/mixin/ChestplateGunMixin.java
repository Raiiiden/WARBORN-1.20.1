package com.raiiiden.warborn.mixin;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.raiiiden.warborn.common.object.capability.ChestplateBundleHandler;
import com.raiiiden.warborn.common.util.MutableInt;
import com.tacz.guns.api.DefaultAssets;
import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.api.item.IAmmoBox;
import com.tacz.guns.api.item.gun.AbstractGunItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractGunItem.class)
public class ChestplateGunMixin {

    @Unique
    private static final Logger LOGGER = LogManager.getLogger("Warborn/ChestplateGunMixin");

    @Unique
    private static final ThreadLocal<Player> RELOADING_PLAYER = new ThreadLocal<>();


    @Inject(method = "canReload", at = @At("HEAD"), remap = false)
    private void capturePlayerBeforeReload(LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> cir) {
        if (shooter instanceof Player player) {
            RELOADING_PLAYER.set(player);
            //LOGGER.info(" {} Reloading", player.getName().getString());
        }
    }

    @Inject(method = "canReload", at = @At("RETURN"), cancellable = true, remap = false)
    private void checkChestplateForAmmo(LivingEntity shooter, ItemStack gunItem, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            //LOGGER.info("Reload already allowed, skipping chestplate check");
            return;
        }

        if (!(shooter instanceof Player player)) return;

        int current = ((AbstractGunItem)(Object)this).getCurrentAmmoCount(gunItem);
        int max = com.tacz.guns.util.AttachmentDataUtils.getAmmoCountWithAttachment(
                gunItem,
                com.tacz.guns.api.TimelessAPI.getCommonGunIndex(((AbstractGunItem)(Object)this).getGunId(gunItem)).orElseThrow().getGunData()
        );

        if (current >= max) {
            //LOGGER.info("Gun already full ({} / {})", current, max);
            return;
        }

        ItemStack chestplate = player.getInventory().getArmor(2);
        if (!(chestplate.getItem() instanceof WBArmorItem) || !WBArmorItem.isChestplateItem(chestplate)) return;

        boolean hasAmmo = chestplate.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (stack.isEmpty()) continue;
                if (stack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(gunItem, stack)) return true;
                if (stack.getItem() instanceof IAmmoBox iBox && iBox.isAmmoBoxOfGun(gunItem, stack)) return true;
            }
            return false;
        }).orElse(false);

        if (hasAmmo) {
            //LOGGER.info("Chestplate has ammo and gun not full");
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "findAndExtractInventoryAmmo", at = @At("RETURN"), cancellable = true, remap = false)
    private void extractAmmoFromChestplate(IItemHandler itemHandler, ItemStack gunItem, int needAmmoCount,
                                           CallbackInfoReturnable<Integer> cir) {

        int alreadyFound = cir.getReturnValue();
        int remainingToFind = needAmmoCount - alreadyFound;

        //LOGGER.info("Ammo needed: {}", needAmmoCount);
        //LOGGER.info("Inventory provided: {}", alreadyFound);

        if (remainingToFind <= 0) {
            //LOGGER.info("Ammo already full");
            RELOADING_PLAYER.remove();
            return;
        }

        Player player = RELOADING_PLAYER.get();
        RELOADING_PLAYER.remove();

        if (player == null) return;

        ItemStack chestplate = player.getInventory().getArmor(2);
        if (!(chestplate.getItem() instanceof WBArmorItem armor) || !WBArmorItem.isChestplateItem(chestplate)) {
            //LOGGER.info("Invalid or missing chestplate");
            return;
        }

        MutableInt remaining = new MutableInt(remainingToFind);
        int foundInChestplate = chestplate.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
            int found = 0;

            for (int i = 0; i < handler.getSlots() && remaining.value > 0; i++) {
                ItemStack stack = handler.getStackInSlot(i);
                //LOGGER.info("Slot {}: {}", i, stack);

                if (stack.isEmpty()) continue;

                if (stack.getItem() instanceof IAmmo iAmmo && iAmmo.isAmmoOfGun(gunItem, stack)) {
                    int toExtract = Math.min(stack.getCount(), remaining.value);
                    ItemStack extracted = handler.extractItem(i, toExtract, false);
                    found += extracted.getCount();
                    remaining.value -= extracted.getCount();
                    //LOGGER.info("Extracted {} IAmmo from slot {}", extracted.getCount(), i);
                }

                if (stack.getItem() instanceof IAmmoBox iBox && iBox.isAmmoBoxOfGun(gunItem, stack)) {
                    int boxAmmo = iBox.getAmmoCount(stack);
                    int extractCount = Math.min(boxAmmo, remaining.value);
                    iBox.setAmmoCount(stack, boxAmmo - extractCount);
                    if (boxAmmo - extractCount <= 0) {
                        iBox.setAmmoId(stack, DefaultAssets.EMPTY_AMMO_ID);
                    }
                    found += extractCount;
                    remaining.value -= extractCount;
                    //LOGGER.info("Extracted {} from IAmmoBox in slot {}", extractCount, i);
                }
            }

            if (handler instanceof ChestplateBundleHandler cbh) {
                cbh.saveToItem(chestplate);
            }

            return found;
        }).orElse(0);

        //LOGGER.info("Chestplate provided: {}", foundInChestplate);
        //LOGGER.info("Final total ammo returned: {}", alreadyFound + foundInChestplate);

        cir.setReturnValue(alreadyFound + foundInChestplate);
    }
}
