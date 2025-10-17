package com.raiiiden.warborn.client.sound;

import com.raiiiden.warborn.common.item.ArmorPlateItem;
import com.raiiiden.warborn.common.init.ModSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class WarbornClientSounds {

    public static void playArmorInsertSound(Player player, ArmorPlateItem item) {
        playSimpleSound(player, ModSoundEvents.WARBORN_PLATE_INSERT.get());
    }

    public static void playArmorRemoveSound(Player player, ArmorPlateItem item) {
        playSimpleSound(player, ModSoundEvents.WARBORN_PLATE_REMOVE.get());
    }

    private static void playSimpleSound(Player player, SoundEvent soundEvent) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        float pitch = 0.9f + mc.level.random.nextFloat() * 0.2f;

        mc.level.playLocalSound(
                player.getX(),
                player.getY(),
                player.getZ(),
                soundEvent,
                SoundSource.PLAYERS,
                1.0f,
                pitch,
                false
        );
    }
}
