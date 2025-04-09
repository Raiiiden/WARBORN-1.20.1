package com.raiiiden.warborn.common.event;

import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.Plate;
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

            // Determine hit direction (front or back)
            var sourcePos = event.getSource().getEntity().position();
            var playerPos = player.position();
            var attackVec = sourcePos.subtract(playerPos).normalize();
            var lookVec = player.getLookAngle().normalize();
            double dot = lookVec.dot(attackVec);

            boolean isFrontHit = dot > 0;
            Plate plateHit = isFrontHit ? cap.getFrontPlate() : cap.getBackPlate();
            boolean hasPlate = isFrontHit ? cap.hasFrontPlate() : cap.hasBackPlate();

            if (hasPlate && plateHit != null) {
                // Calculate damage reduction
                float damageReduction = plateHit.calculateDamageReduction(originalDamage);
                float reducedDamage = originalDamage * (1 - damageReduction);

                // Apply plate damage
                if (isFrontHit) {
                    cap.damageFrontPlate(originalDamage / 3f);
                    LOGGER.info("Front plate absorbed {}% of {} damage. Remaining durability: {}/{}",
                            Math.round(damageReduction * 100), originalDamage,
                            plateHit.getCurrentDurability(), plateHit.getMaxDurability());
                } else {
                    cap.damageBackPlate(originalDamage / 3f);
                    LOGGER.info("Back plate absorbed {}% of {} damage. Remaining durability: {}/{}",
                            Math.round(damageReduction * 100), originalDamage,
                            plateHit.getCurrentDurability(), plateHit.getMaxDurability());
                }

                // Set reduced damage
                event.setAmount(reducedDamage);

                // Apply movement penalty based on plate material
                float speedModifier = plateHit.getSpeedModifier();
                if (speedModifier != 0) {
                    // This is just a placeholder - actual speed modification would need to be implemented elsewhere
                    LOGGER.info("Player speed affected by {}% due to plate material.", speedModifier * 100);
                }

            } else {
                LOGGER.info("No plate in direction of hit or plate broken. Full damage taken.");
            }
        });
    }
}