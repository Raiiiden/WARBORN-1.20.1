package com.raiiiden.warborn.client.network;

import com.raiiiden.warborn.client.renderer.PhantomPlateRenderManager;
import com.raiiiden.warborn.client.sound.WarbornClientSounds;
import com.raiiiden.warborn.common.network.ClientboundPhantomPlatePacket;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Handles client-side packet processing.
 * This class must only be loaded on the client to avoid server crashes.
 */
public class ClientPacketHandler {

    public static void handlePhantomPlate(ClientboundPhantomPlatePacket packet) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        try {
            ProtectionTier tier = ProtectionTier.valueOf(packet.getTierName());
            MaterialType material = MaterialType.valueOf(packet.getMaterialName());

            Plate plate = new Plate(tier, material);
            plate.setCurrentDurability(packet.getCurrentDurability());

            // Start the phantom render with the plate data
            PhantomPlateRenderManager.getInstance().startPhantomRender(
                    plate,
                    packet.getDurationTicks(),
                    packet.getPlayerUUID()
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
                WarbornClientSounds.playArmorRemoveSound(mc.player, plateItem);
            }

        } catch (IllegalArgumentException e) {
            // Invalid tier or material name
            e.printStackTrace();
            mc.player.displayClientMessage(
                    net.minecraft.network.chat.Component.literal("Error: Invalid plate data"),
                    false
            );
        }
    }
}