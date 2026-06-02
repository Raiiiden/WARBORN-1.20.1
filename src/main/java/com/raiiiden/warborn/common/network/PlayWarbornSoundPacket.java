package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.client.sound.WarbornClientSounds;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayWarbornSoundPacket {

    public enum SoundType {
        ARMOR_INSERT,
        ARMOR_REMOVE,
        SYRINGE
    }

    private final SoundType soundType;

    public PlayWarbornSoundPacket(SoundType soundType) {
        this.soundType = soundType;
    }

    public PlayWarbornSoundPacket(FriendlyByteBuf buf) {
        this.soundType = buf.readEnum(SoundType.class);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeEnum(soundType);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            switch (soundType) {
                case ARMOR_INSERT -> WarbornClientSounds.playArmorInsertSound();
                case ARMOR_REMOVE -> WarbornClientSounds.playArmorRemoveSound();
                case SYRINGE      -> WarbornClientSounds.playSyringeSound();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}