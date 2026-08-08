package com.raiiiden.warborn.common.event;

import com.raiiiden.warborn.common.item.NVGBatteryItem;
import com.raiiiden.warborn.common.item.WBArmorItem;
import com.raiiiden.warborn.common.network.ClientboundBatteryUpdatePacket;
import com.raiiiden.warborn.common.network.ClientboundNVGBatteryEmptyPacket;
import com.raiiiden.warborn.common.network.ModNetworking;
import com.raiiiden.warborn.common.object.capability.NVGBatteryStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HelmetBatteryTickHandler {

    // FE drained per vision type, applied once every DRAIN_INTERVAL ticks rather than every tick.
    private static final Map<String, Integer> DRAIN_RATES = new HashMap<>();
    static {
        DRAIN_RATES.put("simple_nvg", 1);
        DRAIN_RATES.put("nvg",        2);
        DRAIN_RATES.put("digital",    3);
        DRAIN_RATES.put("thermal",    5);
        DRAIN_RATES.put("thermal_white", 5);
        DRAIN_RATES.put("thermal_black", 5);
    }

    // How often (in ticks) to drain the battery. 100 = once every 5 seconds.
    private static final int DRAIN_INTERVAL = 20;

    // Server-side map of player UUID → currently active vision type.
    private static final Map<UUID, String> nvgActiveMap = new HashMap<>();

    // In-memory battery energy while NVG is active; writing NBT every tick retriggers equipment-change detection and replays the equip sound.
    private static final Map<UUID, Integer> batteryEnergyCache = new HashMap<>();

    // The live helmet ItemStack from when NVG activated; LivingEquipmentChangeEvent.getFrom() is a snapshot copy, not the live reference.
    private static final Map<UUID, ItemStack> helmetReference = new HashMap<>();

    // Called from ServerboundNVGTogglePacket; an empty visionType turns NVG off and flushes cached energy back to the item.
    public static void setNVGActive(UUID playerUUID, String visionType, ServerPlayer player) {
        if (visionType == null || visionType.isEmpty()) {
            nvgActiveMap.remove(playerUUID);
            if (player != null && batteryEnergyCache.containsKey(playerUUID)) {
                flushCacheToItem(player, playerUUID);
            }
            batteryEnergyCache.remove(playerUUID);
            helmetReference.remove(playerUUID);
        } else {
            nvgActiveMap.put(playerUUID, visionType);
            // Cache will be initialised from item NBT on the first tick.
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        UUID uuid = player.getUUID();
        String visionType = nvgActiveMap.get(uuid);
        if (visionType == null) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) {
            // Helmet removed — flush cached energy to the stored live reference.
            ItemStack ref = helmetReference.remove(uuid);
            if (ref != null && !ref.isEmpty() && batteryEnergyCache.containsKey(uuid)) {
                ItemStack battery = WBArmorItem.getInsertedBattery(ref);
                if (!battery.isEmpty() && battery.getItem() instanceof NVGBatteryItem) {
                    CompoundTag batteryTag = battery.getOrCreateTag();
                    batteryTag.putInt(NVGBatteryStorage.NBT_KEY, batteryEnergyCache.get(uuid));
                    WBArmorItem.setInsertedBattery(ref, battery);
                }
            }
            nvgActiveMap.remove(uuid);
            batteryEnergyCache.remove(uuid);
            return;
        }

        ItemStack battery = WBArmorItem.getInsertedBattery(helmet);
        if (battery.isEmpty() || !(battery.getItem() instanceof NVGBatteryItem)) {
            nvgActiveMap.remove(uuid);
            batteryEnergyCache.remove(uuid);
            ModNetworking.sendToPlayer(new ClientboundNVGBatteryEmptyPacket(), player);
            return;
        }

        // Initialise cache from item NBT on the first tick of this NVG session.
        if (!batteryEnergyCache.containsKey(uuid)) {
            batteryEnergyCache.put(uuid, NVGBatteryStorage.readEnergy(battery));
            helmetReference.put(uuid, helmet); // live reference for flush-on-removal
        }

        // Only drain every DRAIN_INTERVAL ticks (every 5 seconds at 20% of tick rate).
        boolean shouldDrain = player.tickCount % DRAIN_INTERVAL == 0;

        int current = batteryEnergyCache.get(uuid);
        int newEnergy = current;

        if (shouldDrain) {
            int drainAmount = DRAIN_RATES.getOrDefault(visionType, 2);
            newEnergy = Math.max(0, current - drainAmount);
            batteryEnergyCache.put(uuid, newEnergy);
        }

        // Push live energy to the client every tick for real-time display.
        ModNetworking.sendToPlayer(new ClientboundBatteryUpdatePacket(newEnergy), player);

        if (newEnergy <= 0) {
            // Write the depleted (0) value back to the item exactly once.
            writeEnergyToItem(player, 0);
            nvgActiveMap.remove(uuid);
            batteryEnergyCache.remove(uuid);
            helmetReference.remove(uuid);
            ModNetworking.sendToPlayer(new ClientboundNVGBatteryEmptyPacket(), player);
        }
    }

    // Flushes cached battery energy when equipment changes so charge isn't lost on helmet removal; it also fires for NBT edits on a helmet that is still worn.
    @SubscribeEvent
    public void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (event.getSlot() != EquipmentSlot.HEAD) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        UUID uuid = player.getUUID();
        if (!batteryEnergyCache.containsKey(uuid)) return;

        // Use the stored live reference — event.getFrom() is a snapshot copy and writes to it are discarded.
        ItemStack liveHelmet = helmetReference.remove(uuid);
        if (liveHelmet != null && !liveHelmet.isEmpty()) {
            ItemStack battery = WBArmorItem.getInsertedBattery(liveHelmet);
            if (!battery.isEmpty() && battery.getItem() instanceof NVGBatteryItem) {
                CompoundTag batteryTag = battery.getOrCreateTag();
                batteryTag.putInt(NVGBatteryStorage.NBT_KEY, batteryEnergyCache.get(uuid));
                WBArmorItem.setInsertedBattery(liveHelmet, battery);
            }
        }
        // Dropping the cache makes the next tick re-read charge from whatever is in the
        // helmet now; nvgActiveMap is deliberately left alone.
        batteryEnergyCache.remove(uuid);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        UUID uuid = event.getEntity().getUUID();
        if (event.getEntity() instanceof ServerPlayer sp && batteryEnergyCache.containsKey(uuid)) {
            flushCacheToItem(sp, uuid);
        }
        nvgActiveMap.remove(uuid);
        batteryEnergyCache.remove(uuid);
        helmetReference.remove(uuid);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    // Writes the cached energy value for {@code uuid} into the player's helmet item.
    private static void flushCacheToItem(ServerPlayer player, UUID uuid) {
        int energy = batteryEnergyCache.getOrDefault(uuid, 0);
        writeEnergyToItem(player, energy);
    }

    // Directly sets the battery energy stored in the equipped helmet's NBT.
    private static void writeEnergyToItem(ServerPlayer player, int energy) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) return;
        ItemStack battery = WBArmorItem.getInsertedBattery(helmet);
        if (battery.isEmpty() || !(battery.getItem() instanceof NVGBatteryItem)) return;

        CompoundTag batteryTag = battery.getOrCreateTag();
        batteryTag.putInt(NVGBatteryStorage.NBT_KEY, energy);
        WBArmorItem.setInsertedBattery(helmet, battery);
    }
}