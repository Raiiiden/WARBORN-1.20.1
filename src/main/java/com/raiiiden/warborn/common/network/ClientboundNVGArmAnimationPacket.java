package com.raiiiden.warborn.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class ClientboundNVGArmAnimationPacket {
    public final int entityId;
    public final boolean start;

    public ClientboundNVGArmAnimationPacket(int entityId, boolean start) {
        this.entityId = entityId;
        this.start = start;
    }
    public ClientboundNVGArmAnimationPacket(FriendlyByteBuf buf) {
        this.entityId = buf.readInt();
        this.start = buf.readBoolean();
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(start);
    }
    public static void handle(ClientboundNVGArmAnimationPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.level == null) return;
            var entity = mc.level.getEntity(msg.entityId);
            if (entity != null) {
                if (msg.start) {
                    entity.getPersistentData().putInt("NVG_ANIM_TICK", 1);
                } else {
                    entity.getPersistentData().putInt("NVG_ANIM_TICK", 0);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}