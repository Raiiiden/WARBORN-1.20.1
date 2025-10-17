package com.raiiiden.warborn.common.event;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.ArmorPlateItem;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.Plate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handles server-side ticking for phantom plate removal animations
 * when the player isn't holding a plate item
 */
@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PhantomPlateServerHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer serverPlayer)) return;

        CompoundTag playerData = serverPlayer.getPersistentData();

        if (!playerData.getBoolean("warborn_processing_removal")) return;

        // Load the temporary plate from NBT
        if (!playerData.contains("warborn_temp_plate")) {
            playerData.remove("warborn_processing_removal");
            return;
        }

        CompoundTag plateNBT = playerData.getCompound("warborn_temp_plate");
        ItemStack tempPlate = ItemStack.of(plateNBT);

        if (tempPlate.isEmpty() || !(tempPlate.getItem() instanceof ArmorPlateItem)) {
            cleanupPhantomRemoval(serverPlayer);
            return;
        }

        CompoundTag tag = tempPlate.getTag();
        if (tag == null || !tag.getBoolean(ArmorPlateItem.PENDING_REMOVE_TAG)) {
            cleanupPhantomRemoval(serverPlayer);
            return;
        }

        // Tick down the removal delay
        int delay = tag.getInt("warborn_remove_delay") - 1;
        tag.putInt("warborn_remove_delay", delay);

        if (delay > 0) {
            // Save back to player data
            playerData.put("warborn_temp_plate", tempPlate.save(new CompoundTag()));
            return;
        }

        // Animation complete - actually remove the plate
        boolean removeFront = tag.getBoolean("warborn_remove_front");
        ItemStack chest = serverPlayer.getItemBySlot(EquipmentSlot.CHEST);

        if (!chest.isEmpty() && ArmorPlateItem.isPlateCompatible(chest)) {
            chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
                if (removeFront && cap.hasFrontPlate()) {
                    Plate plate = cap.getFrontPlate();
                    if (plate != null) {
                        ItemStack plateStack = ArmorPlateItem.createPlateWithHitsRemaining(
                                plate.getTier(),
                                plate.getMaterial(),
                                (int) plate.getCurrentDurability()
                        );
                        serverPlayer.addItem(plateStack);
                        cap.removeFrontPlate();
                        // LOGGER.info("Removed front plate via phantom animation for player: {}", serverPlayer.getName().getString());
                    }
                } else if (!removeFront && cap.hasBackPlate()) {
                    Plate plate = cap.getBackPlate();
                    if (plate != null) {
                        ItemStack plateStack = ArmorPlateItem.createPlateWithHitsRemaining(
                                plate.getTier(),
                                plate.getMaterial(),
                                (int) plate.getCurrentDurability()
                        );
                        serverPlayer.addItem(plateStack);
                        cap.removeBackPlate();
                        // LOGGER.info("Removed back plate via phantom animation for player: {}", serverPlayer.getName().getString());
                    }
                }
            });
        }

        cleanupPhantomRemoval(serverPlayer);
    }

    private static void cleanupPhantomRemoval(Player player) {
        CompoundTag playerData = player.getPersistentData();
        playerData.remove("warborn_temp_plate");
        playerData.remove("warborn_processing_removal");
    }
}