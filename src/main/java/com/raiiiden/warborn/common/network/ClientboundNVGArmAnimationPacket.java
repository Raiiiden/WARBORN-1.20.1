package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
                if (msg.start && entity instanceof LivingEntity livingEntity) {
                    var helmet = livingEntity.getItemBySlot(EquipmentSlot.HEAD);
                    boolean currentlyOpen = helmet.getItem() instanceof WBArmorItem helmetItem
                            && helmetItem.isTopOpen(helmet);
                    int firstPersonDuration = currentlyOpen ? 36 : 41;
                    int helmetTriggerTick = (firstPersonDuration + 1) / 2;
                    entity.getPersistentData().putInt("NVG_ANIM_TRIGGER_TICK", helmetTriggerTick);
                    int earlyArmTriggerTick = Math.max(1, helmetTriggerTick - 20);
                    entity.getPersistentData().putInt("NVG_ARM_TRIGGER_TICK", earlyArmTriggerTick + 10);
                    entity.getPersistentData().putBoolean("NVG_ANIM_TARGET_OPEN", !currentlyOpen);
                    entity.getPersistentData().putBoolean("NVG_ANIM_HELMET_READY", false);
                    entity.getPersistentData().putInt("NVG_ANIM_TICK", 1);
                } else {
                    entity.getPersistentData().putInt("NVG_ANIM_TICK", 0);
                }
            }
            // Trigger first-person NVG hand animation for the local player
            com.raiiiden.warborn.client.network.ClientPacketHandler.handleNVGArmAnimation(msg);
        });
        ctx.get().setPacketHandled(true);
    }
}
