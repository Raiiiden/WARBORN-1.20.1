package com.raiiiden.warborn.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.raiiiden.warborn.common.init.ModItemRegistry;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Manages phantom (virtual) plate rendering for removal animations
 * when the player isn't actually holding a plate item.
 */
@OnlyIn(Dist.CLIENT)
public class PhantomPlateRenderManager {
    private static PhantomPlateRenderManager INSTANCE;

    private ItemStack phantomStack = ItemStack.EMPTY;
    private int remainingTicks = 0;
    private boolean isActive = false;
    private UUID playerUUID = null;

    private PhantomPlateRenderManager() {}

    public static PhantomPlateRenderManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PhantomPlateRenderManager();
        }
        return INSTANCE;
    }

    /**
     * Starts rendering a phantom plate for the removal animation
     */
    public void startPhantomRender(Plate plate, int durationTicks, UUID playerUUID) {
        if (plate == null) return;

        this.phantomStack = ModItemRegistry.getPlateItem(plate.getTier(), plate.getMaterial())
                .getDefaultInstance();

        // Mark this as a phantom render so the renderer knows
        CompoundTag tag = this.phantomStack.getOrCreateTag();
        tag.putBoolean("warborn_phantom_render", true);
        // Mark as removing to trigger the animation
        tag.putBoolean(com.raiiiden.warborn.common.item.ArmorPlateItem.PENDING_REMOVE_TAG, true);
        tag.putInt("warborn_remove_delay", 55); // Match the animation length

        this.remainingTicks = durationTicks;
        this.isActive = true;
        this.playerUUID = playerUUID;
    }

    /**
     * Called every client tick to update the phantom render state
     */
    public void tick() {
        if (!isActive) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.getUUID().equals(playerUUID)) {
            clear();
            return;
        }

        remainingTicks--;

        if (remainingTicks <= 0) {
            clear();
        }
    }

    /**
     * Checks if we should render a phantom plate instead of the held item
     */
    public boolean shouldRenderPhantom(InteractionHand hand) {
        if (!isActive || phantomStack.isEmpty()) return false;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;

        // Only render in main hand
        if (hand != InteractionHand.MAIN_HAND) return false;

        // Check if player is actually holding a plate - if so, use normal rendering
        ItemStack held = player.getMainHandItem();
        if (!held.isEmpty() && held.getItem() instanceof com.raiiiden.warborn.common.item.ArmorPlateItem) {
            return false;
        }

        return true;
    }

    /**
     * Gets the phantom item stack to render
     */
    @Nullable
    public ItemStack getPhantomStack() {
        return isActive ? phantomStack : ItemStack.EMPTY;
    }

    /**
     * Clears the phantom render state
     */
    public void clear() {
        this.phantomStack = ItemStack.EMPTY;
        this.remainingTicks = 0;
        this.isActive = false;
        this.playerUUID = null;
    }

    public boolean isActive() {
        return isActive;
    }
}