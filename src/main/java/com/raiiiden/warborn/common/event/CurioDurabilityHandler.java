package com.raiiiden.warborn.common.event;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

// Wears down gear worn in a curios slot when its wearer is hurt.
//
// Vanilla only walks the four armor slots, so a uniform, mask or shoulderpads would soak hits
// forever without ever losing durability. This mirrors Inventory.hurtArmor so curios gear ages at
// the same rate as the armor it sits over.
@Mod.EventBusSubscriber(modid = WARBORN.MODID)
public final class CurioDurabilityHandler {

    private CurioDurabilityHandler() {}

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        float damage = event.getAmount();
        if (damage <= 0.0F) return;

        // Same conversion vanilla armor uses: a quarter of the incoming damage, at least one point.
        int wear = Math.max(1, (int) (damage / 4.0F));
        DamageSource source = event.getSource();

        for (SlotResult slotResult : CuriosApi.getCuriosHelper().findCurios(entity, CurioDurabilityHandler::wearsDown)) {
            ItemStack stack = slotResult.stack();
            if (source.is(DamageTypeTags.IS_FIRE) && stack.getItem().isFireResistant()) continue;

            stack.hurtAndBreak(wear, entity, wearer -> wearer.broadcastBreakEvent(EquipmentSlot.CHEST));
        }
    }

    // Backpacks are storage rather than protection, so they are exempt while worn as a curio.
    // They are BackpackItem, not WBArmorItem, so this check excludes them on its own.
    private static boolean wearsDown(ItemStack stack) {
        return stack.getItem() instanceof WBArmorItem && stack.isDamageableItem();
    }
}
