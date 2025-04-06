package com.raiiiden.warborn.common.event;

import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class DamageHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty()) return;

        if (event.getSource().getEntity() == null) return;

        chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            float original = event.getAmount();

            var sourcePos = event.getSource().getEntity().position();
            var playerPos = player.position();
            var attackVec = sourcePos.subtract(playerPos).normalize();
            var lookVec = player.getLookAngle().normalize();

            double dot = lookVec.dot(attackVec); // front vs back

            if (dot > 0 && cap.hasFrontPlate() && cap.getFrontDurability() > 0) { // Added durability check
                cap.damageFront(1);
                event.setAmount(original * 0.6f);
                LOGGER.info("Hit front plate. Reduced damage.");
            } else if (dot < 0 && cap.hasBackPlate() && cap.getBackDurability() > 0) { // Added durability check
                cap.damageBack(1);
                event.setAmount(original * 0.6f);
                LOGGER.info("Hit back plate. Reduced damage.");
            } else {
                LOGGER.info("No plate in direction of hit or plate broken. Full damage.");
            }
        });
    }
}