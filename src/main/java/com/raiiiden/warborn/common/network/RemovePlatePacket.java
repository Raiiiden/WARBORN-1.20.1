package com.raiiiden.warborn.common.network;

import com.raiiiden.warborn.common.item.ArmorPlateItem;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.Plate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.function.Supplier;

public class RemovePlatePacket {
    private final boolean front;

    public RemovePlatePacket(boolean front) {
        this.front = front;
    }

    public RemovePlatePacket(FriendlyByteBuf buf) {
        this.front = buf.readBoolean();
    }

    public static void handle(RemovePlatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null || !(player.level() instanceof ServerLevel serverLevel)) return;

            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);

            if (chest.isEmpty() || !ArmorPlateItem.isPlateCompatible(chest)) return;

            // Check if player is already processing an insert or remove operation
            if (player.getPersistentData().getBoolean("warborn_processing_removal") ||
                    player.getPersistentData().getBoolean("warborn_processing_insert")) {
                player.displayClientMessage(
                        net.minecraft.network.chat.Component.literal("Please wait for current operation to complete")
                                .withStyle(net.minecraft.ChatFormatting.YELLOW),
                        true
                );
                return;
            }

            // Check if any held plate has pending operations
            if (main.hasTag() && (main.getTag().getBoolean(ArmorPlateItem.PENDING_INSERT_TAG) ||
                    main.getTag().getBoolean(ArmorPlateItem.PENDING_REMOVE_TAG))) {
                return;
            }
            if (off.hasTag() && (off.getTag().getBoolean(ArmorPlateItem.PENDING_INSERT_TAG) ||
                    off.getTag().getBoolean(ArmorPlateItem.PENDING_REMOVE_TAG))) {
                return;
            }

            // Get the plate that will be removed
            Plate plateToRemove = chest.getCapability(PlateHolderProvider.CAP).map(cap ->
                    packet.front ? cap.getFrontPlate() : cap.getBackPlate()
            ).orElse(null);

            if (plateToRemove == null) return;

            // Mark that we're processing a removal
            player.getPersistentData().putBoolean("warborn_processing_removal", true);

            // Animation duration: 55 ticks for animation + buffer
            int animationDuration = 55;

            // ALWAYS send phantom render packet to client with the CORRECT plate
            ClientboundPhantomPlatePacket phantomPacket = new ClientboundPhantomPlatePacket(
                    plateToRemove,
                    animationDuration,
                    player.getUUID()
            );

            ModNetworking.PACKET_CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    phantomPacket
            );

            // Check if player is holding a plate item
            boolean isHoldingPlate = false;
            ItemStack heldPlate = null;

            if (main.getItem() instanceof ArmorPlateItem) {
                isHoldingPlate = true;
                heldPlate = main;
            } else if (off.getItem() instanceof ArmorPlateItem) {
                isHoldingPlate = true;
                heldPlate = off;
            }

            // If holding a plate, trigger the animation on it (for server-side tracking)
            // but the CLIENT will render the phantom plate instead
            if (isHoldingPlate && heldPlate != null) {
                if (heldPlate.getItem() instanceof ArmorPlateItem plateItem) {
                    plateItem.startRemoveAnimation(player, heldPlate, packet.front, serverLevel);
                }
            }
            // If NOT holding a plate, use server-side temporary plate for processing
            else {
                // Create a temporary plate stack with proper NBT for removal animation
                ItemStack tempPlate = ArmorPlateItem.createPlateWithHitsRemaining(
                        plateToRemove.getTier(),
                        plateToRemove.getMaterial(),
                        (int) plateToRemove.getCurrentDurability()
                );

                // Set up the temporary stack as if it's being used for removal
                CompoundTag tag = tempPlate.getOrCreateTag();
                tag.putBoolean(ArmorPlateItem.PENDING_REMOVE_TAG, true);
                tag.putInt("warborn_remove_delay", 55);
                tag.putBoolean("warborn_remove_front", packet.front);

                // Assign a GeckoLib ID for animation
                GeoItem.getOrAssignId(tempPlate, serverLevel);

                // Trigger the animation
                if (tempPlate.getItem() instanceof ArmorPlateItem plateItem) {
                    plateItem.startRemoveAnimation(player, tempPlate, packet.front, serverLevel);
                }

                // Store the temp plate for server-side processing
                player.getPersistentData().put("warborn_temp_plate", tempPlate.save(new CompoundTag()));
            }
        });

        ctx.get().setPacketHandled(true);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(this.front);
    }
}