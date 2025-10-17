package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.client.renderer.PhantomPlateRenderManager;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
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

    public static void handle(ClientboundPhantomPlatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Client-side only
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            try {
                ProtectionTier tier = ProtectionTier.valueOf(packet.tierName);
                MaterialType material = MaterialType.valueOf(packet.materialName);

                Plate plate = new Plate(tier, material);
                plate.setCurrentDurability(packet.currentDurability);

                // Start the phantom render with the plate data
                PhantomPlateRenderManager.getInstance().startPhantomRender(
                        plate,
                        packet.durationTicks,
                        packet.playerUUID
                );

                // CRITICAL: Trigger the animation on the client side
                ItemStack phantomStack = PhantomPlateRenderManager.getInstance().getPhantomStack();
                if (!phantomStack.isEmpty() && phantomStack.getItem() instanceof com.raiiiden.warborn.common.item.ArmorPlateItem plateItem) {
                    // For client-side, we need to get or create a GeckoLib ID without ServerLevel
                    CompoundTag tag = phantomStack.getOrCreateTag();
                    long geckoId;

                    if (tag.contains("GeckoLibID")) {
                        geckoId = tag.getLong("GeckoLibID");
                    } else {
                        // Generate a new ID client-side
                        geckoId = software.bernie.geckolib.animatable.GeoItem.getId(phantomStack);
                        tag.putLong("GeckoLibID", geckoId);
                    }

                    // CRITICAL FIX: Force reset the animation controller before triggering
                    // This ensures the animation plays from the start each time
                    var animatableManager = plateItem.getAnimatableInstanceCache().getManagerForId(geckoId);
                    if (animatableManager != null) {
                        var controller = animatableManager.getAnimationControllers().get(com.raiiiden.warborn.common.item.ArmorPlateItem.CONTROLLER);
                        if (controller != null) {
                            controller.forceAnimationReset();
                        }
                    }

                    // Trigger the remove animation client-side
                    plateItem.triggerAnim(mc.player, geckoId, com.raiiiden.warborn.common.item.ArmorPlateItem.CONTROLLER, "remove");
                }

            } catch (IllegalArgumentException e) {
                // Invalid tier or material name
                e.printStackTrace();
                mc.player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("Error: Invalid plate data"),
                        false
                );
            }
        });

        ctx.get().setPacketHandled(true);
    }
}