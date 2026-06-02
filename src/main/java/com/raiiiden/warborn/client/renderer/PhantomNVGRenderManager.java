package com.raiiiden.warborn.client.renderer;

import com.raiiiden.warborn.client.gui.NVGScreenFadeOverlay;
import com.raiiiden.warborn.common.init.ModItemRegistry;
import com.raiiiden.warborn.common.item.NVGHandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;

import javax.annotation.Nullable;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class PhantomNVGRenderManager {
    private static PhantomNVGRenderManager INSTANCE;

    private ItemStack phantomStack = ItemStack.EMPTY;
    private int remainingTicks = 0;
    private boolean isActive = false;
    private UUID playerUUID = null;
    private int elapsedTicks = 0;
    private String currentAnimName = "";
    private boolean fadeTriggerred = false;

    // Trigger the screen fade so its peak coincides with the helmet toggle (triggerTick=15 in NVGArmAnimationTicker).
    // Pre-offset by FADE_IN_TICKS (3) so fade peaks at tick 15.
    private static final int FADE_IN_TICKS = 3; // must match NVGScreenFadeOverlay.FADE_IN_TICKS
    private static final int FADE_TRIGGER_TICK = 20 - FADE_IN_TICKS; // = 12

    private PhantomNVGRenderManager() {}

    public static PhantomNVGRenderManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PhantomNVGRenderManager();
        }
        return INSTANCE;
    }

    public void startPhantomRender(String animName, int durationTicks, UUID playerUUID) {
        this.phantomStack = ModItemRegistry.NVG_HAND_ITEM.get().getDefaultInstance();
        this.remainingTicks = durationTicks;
        this.isActive = true;
        this.playerUUID = playerUUID;
        this.elapsedTicks = 0;
        this.currentAnimName = animName;
        this.fadeTriggerred = false;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || phantomStack.isEmpty()) return;

        NVGHandItem nvgItem = (NVGHandItem) phantomStack.getItem();

        CompoundTag tag = phantomStack.getOrCreateTag();
        long geckoId;
        if (tag.contains("GeckoLibID")) {
            geckoId = tag.getLong("GeckoLibID");
        } else {
            geckoId = GeoItem.getId(phantomStack);
            tag.putLong("GeckoLibID", geckoId);
        }

        var animatableManager = nvgItem.getAnimatableInstanceCache().getManagerForId(geckoId);
        if (animatableManager != null) {
            var controller = animatableManager.getAnimationControllers().get(NVGHandItem.CONTROLLER);
            if (controller != null) {
                controller.forceAnimationReset();
            }
        }

        nvgItem.triggerAnim(mc.player, geckoId, NVGHandItem.CONTROLLER, animName);
    }

    public void tick() {
        if (!isActive) return;

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !player.getUUID().equals(playerUUID)) {
            clear();
            return;
        }

        elapsedTicks++;
        remainingTicks--;

        if (!fadeTriggerred) {
            int triggerTick = FADE_TRIGGER_TICK;
            if (elapsedTicks >= triggerTick) {
                NVGScreenFadeOverlay.triggerFade();
                fadeTriggerred = true;
            }
        }

        if (remainingTicks <= 0) {
            clear();
        }
    }

    public boolean shouldRenderPhantom(InteractionHand hand) {
        if (!isActive || phantomStack.isEmpty()) return false;
        if (Minecraft.getInstance().player == null) return false;
        return hand == InteractionHand.MAIN_HAND;
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
        this.elapsedTicks = 0;
        this.currentAnimName = "";
        this.fadeTriggerred = false;
    }

    public boolean isActive() {
        return isActive;
    }
}
