package com.raiiiden.warborn.client.sound;

import com.raiiiden.warborn.common.init.ModSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundSource;

public class WarbornClientSounds {

    private static SoundInstance currentSyringeSound;
    private static SoundInstance currentArmorInsertSound;
    private static SoundInstance currentArmorRemoveSound;

    public static void playArmorInsertSound() {
        stopArmorInsertSound();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        float pitch = 0.9f + mc.level.random.nextFloat() * 0.2f;
        currentArmorInsertSound = new SimpleSoundInstance(
                ModSoundEvents.WARBORN_PLATE_INSERT.get(),
                SoundSource.PLAYERS, 1.0f, pitch, mc.level.random,
                (float) mc.player.getX(), (float) mc.player.getY(), (float) mc.player.getZ()
        );
        mc.getSoundManager().play(currentArmorInsertSound);
    }

    public static void playArmorRemoveSound() {
        stopArmorRemoveSound();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        float pitch = 0.9f + mc.level.random.nextFloat() * 0.2f;
        currentArmorRemoveSound = new SimpleSoundInstance(
                ModSoundEvents.WARBORN_PLATE_REMOVE.get(),
                SoundSource.PLAYERS, 1.0f, pitch, mc.level.random,
                (float) mc.player.getX(), (float) mc.player.getY(), (float) mc.player.getZ()
        );
        mc.getSoundManager().play(currentArmorRemoveSound);
    }

    public static void playSyringeSound() {
        stopSyringeSound();
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        float pitch = 0.9f + mc.level.random.nextFloat() * 0.2f;
        currentSyringeSound = new SimpleSoundInstance(
                ModSoundEvents.WARBORN_SYRINGE_USE.get(),
                SoundSource.PLAYERS, 1.0f, pitch, mc.level.random,
                (float) mc.player.getX(), (float) mc.player.getY(), (float) mc.player.getZ()
        );
        mc.getSoundManager().play(currentSyringeSound);
    }

    public static void stopArmorInsertSound() {
        if (currentArmorInsertSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentArmorInsertSound);
            currentArmorInsertSound = null;
        }
    }

    public static void stopArmorRemoveSound() {
        if (currentArmorRemoveSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentArmorRemoveSound);
            currentArmorRemoveSound = null;
        }
    }

    public static void stopSyringeSound() {
        if (currentSyringeSound != null) {
            Minecraft.getInstance().getSoundManager().stop(currentSyringeSound);
            currentSyringeSound = null;
        }
    }
}