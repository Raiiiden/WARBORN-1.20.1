package com.raiiiden.warborn.common.event;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.Plate;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = WARBORN.MODID)
public class PlateSpeedHandler {
    // we can be funny with the uuid
    private static final UUID PLATE_SPEED_MODIFIER_UUID = UUID.fromString("d10a0ceb-f815-4b8c-8b41-b7c3b8612891");
    private static final String PLATE_SPEED_MODIFIER_NAME = "Armor Plate Speed Modifier";

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Player player = event.player;

        if (player.isSpectator()) {
            removeSpeedModifier(player);
            return;
        }

        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.isEmpty()) {
            removeSpeedModifier(player);
            return;
        }

        chestplate.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            float totalSpeedMod = 0.0F;
            int plateCount = 0;

            if (cap.hasFrontPlate()) {
                Plate frontPlate = cap.getFrontPlate();
                if (frontPlate != null && !frontPlate.isBroken()) {
                    totalSpeedMod += frontPlate.getSpeedModifier();
                    plateCount++;
                }
            }

            if (cap.hasBackPlate()) {
                Plate backPlate = cap.getBackPlate();
                if (backPlate != null && !backPlate.isBroken()) {
                    totalSpeedMod += backPlate.getSpeedModifier();
                    plateCount++;
                }
            }

            if (plateCount > 0) {
                float averageSpeedMod = totalSpeedMod / plateCount;
                applySpeedModifier(player, averageSpeedMod);
            } else {
                removeSpeedModifier(player);
            }
        });
    }

    private static void applySpeedModifier(Player player, float speedModifier) {
        removeSpeedModifier(player);

        // fck using floats everwhere
        if (Math.abs(speedModifier) > 0.001f) {
            AttributeModifier modifier = new AttributeModifier(
                    PLATE_SPEED_MODIFIER_UUID,
                    PLATE_SPEED_MODIFIER_NAME,
                    speedModifier,
                    AttributeModifier.Operation.MULTIPLY_TOTAL);

            player.getAttribute(Attributes.MOVEMENT_SPEED)
                    .addTransientModifier(modifier);
        }
    }

    private static void removeSpeedModifier(Player player) {
        if (player.getAttribute(Attributes.MOVEMENT_SPEED)
                .getModifier(PLATE_SPEED_MODIFIER_UUID) != null) {
            player.getAttribute(Attributes.MOVEMENT_SPEED)
                    .removeModifier(PLATE_SPEED_MODIFIER_UUID);
        }
    }
} 