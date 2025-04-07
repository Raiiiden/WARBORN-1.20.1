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
            float originalDamage = event.getAmount();
            float reducedDamage = originalDamage * 0.6f;
            int plateDamage = Math.max(1, Math.round(originalDamage / 3f));

            var sourcePos = event.getSource().getEntity().position();
            var playerPos = player.position();
            var attackVec = sourcePos.subtract(playerPos).normalize();
            var lookVec = player.getLookAngle().normalize();

            double dot = lookVec.dot(attackVec); // front vs back

            if (dot > 0 && cap.hasFrontPlate() && cap.getFrontDurability() > 0) {
                int durability = cap.getFrontDurability();

                if (durability >= plateDamage) {
                    cap.damageFront(plateDamage);
                    event.setAmount(reducedDamage);
                    LOGGER.info("Front plate absorbed {}. Remaining: {}", plateDamage, cap.getFrontDurability());
                } else {
                    cap.damageFront(durability); // break it
                    float overflowRatio = (plateDamage - durability) / (float) plateDamage;
                    float overflowDamage = originalDamage * overflowRatio;
                    event.setAmount(reducedDamage + overflowDamage);
                    LOGGER.info("Front plate broke. Overflow damage: {}", overflowDamage);
                }

            } else if (dot < 0 && cap.hasBackPlate() && cap.getBackDurability() > 0) {
                int durability = cap.getBackDurability();

                if (durability >= plateDamage) {
                    cap.damageBack(plateDamage);
                    event.setAmount(reducedDamage);
                    LOGGER.info("Back plate absorbed {}. Remaining: {}", plateDamage, cap.getBackDurability());
                } else {
                    cap.damageBack(durability); // break it
                    float overflowRatio = (plateDamage - durability) / (float) plateDamage;
                    float overflowDamage = originalDamage * overflowRatio;
                    event.setAmount(reducedDamage + overflowDamage);
                    LOGGER.info("Back plate broke. Overflow damage: {}", overflowDamage);
                }

            } else {
                LOGGER.info("No plate in direction of hit or plate broken. Full damage.");
            }
        });
    }
}