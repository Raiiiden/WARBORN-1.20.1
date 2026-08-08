package com.raiiiden.warborn.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractPhantomRenderManager {
    protected ItemStack phantomStack = ItemStack.EMPTY;
    protected int remainingTicks = 0;
    protected boolean isActive = false;
    protected UUID playerUUID = null;

    protected void activate(ItemStack stack, int durationTicks, UUID playerUUID) {
        this.phantomStack = stack;
        this.remainingTicks = durationTicks;
        this.isActive = true;
        this.playerUUID = playerUUID;
    }

    public void tick() {
        if (!isActive) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.getUUID().equals(playerUUID)) {
            clear();
            return;
        }

        tickActive();
        remainingTicks--;

        if (remainingTicks <= 0) {
            clear();
        }
    }

    protected void tickActive() {
    }

    public boolean shouldRenderPhantom(InteractionHand hand) {
        return isActive && !phantomStack.isEmpty() && Minecraft.getInstance().player != null && hand == InteractionHand.MAIN_HAND;
    }

    @Nullable
    public ItemStack getPhantomStack() {
        return isActive ? phantomStack : ItemStack.EMPTY;
    }

    public void clear() {
        this.phantomStack = ItemStack.EMPTY;
        this.remainingTicks = 0;
        this.isActive = false;
        this.playerUUID = null;
        resetExtraState();
    }

    protected void resetExtraState() {
    }

    public boolean isActive() {
        return isActive;
    }
}
