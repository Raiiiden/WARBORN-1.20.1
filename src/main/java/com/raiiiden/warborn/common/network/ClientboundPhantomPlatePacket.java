package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Sent from server to client to trigger phantom plate rendering
 * for removal animations when the player isn't holding a plate.
 */
public class ClientboundPhantomPlatePacket {
    private final String tierName;
    private final String materialName;
    private final float currentDurability;
    private final int durationTicks;
    private final UUID playerUUID;

    public ClientboundPhantomPlatePacket(Plate plate, int durationTicks, UUID playerUUID) {
        this.tierName = plate.getTier().name();
        this.materialName = plate.getMaterial().name();
        this.currentDurability = plate.getCurrentDurability();
        this.durationTicks = durationTicks;
        this.playerUUID = playerUUID;
    }

    public ClientboundPhantomPlatePacket(FriendlyByteBuf buf) {
        this.tierName = buf.readUtf();
        this.materialName = buf.readUtf();
        this.currentDurability = buf.readFloat();
        this.durationTicks = buf.readInt();
        this.playerUUID = buf.readUUID();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(this.tierName);
        buf.writeUtf(this.materialName);
        buf.writeFloat(this.currentDurability);
        buf.writeInt(this.durationTicks);
        buf.writeUUID(this.playerUUID);
    }

    public String getTierName() {
        return tierName;
    }

    public String getMaterialName() {
        return materialName;
    }

    public float getCurrentDurability() {
        return currentDurability;
    }

    public int getDurationTicks() {
        return durationTicks;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public static void handle(ClientboundPhantomPlatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Delegate to client-only handler
            net.minecraftforge.fml.DistExecutor.unsafeRunWhenOn(
                    net.minecraftforge.api.distmarker.Dist.CLIENT,
                    () -> () -> com.raiiiden.warborn.client.network.ClientPacketHandler.handlePhantomPlate(packet)
            );
        });
        ctx.get().setPacketHandled(true);
    }
}