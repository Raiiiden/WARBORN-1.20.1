package com.raiiiden.warborn.client.sound;

import com.raiiiden.warborn.common.item.ArmorPlateItem;
import com.raiiiden.warborn.common.init.ModSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarbornClientSounds {

    private static final Map<UUID, ArmorPlateSound> activeSounds = new HashMap<>();

    public static void playArmorInsertSound(Player player, ArmorPlateItem item) {
        UUID id = player.getUUID();

        ArmorPlateSound prev = activeSounds.get(id);
        if (prev != null) {
            Minecraft.getInstance().getSoundManager().stop(prev);
        }

        ArmorPlateSound sound = new ArmorPlateSound(player, item);
        activeSounds.put(id, sound);
        Minecraft.getInstance().getSoundManager().play(sound);
    }

    private static class ArmorPlateSound extends AbstractTickableSoundInstance {
        private final Player player;
        private final ArmorPlateItem trackedItem;

        public ArmorPlateSound(Player player, ArmorPlateItem trackedItem) {
            super(ModSoundEvents.WARBORN_PLATE_INSERT.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
            this.player = player;
            this.trackedItem = trackedItem;
            this.looping = false;
            this.volume = 1.0f;
            this.pitch = 1.0f;
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();
        }

        @Override
        public void tick() {
            if (!player.isAlive() || !isHoldingPlate(player)) {
                this.stop();
                activeSounds.remove(player.getUUID());
            } else {
                this.x = player.getX();
                this.y = player.getY();
                this.z = player.getZ();
            }
        }

        private boolean isHoldingPlate(Player player) {
            return player.getMainHandItem().getItem() == trackedItem || player.getOffhandItem().getItem() == trackedItem;
        }

        @Override
        public boolean canStartSilent() {
            return false;
        }
    }
}
