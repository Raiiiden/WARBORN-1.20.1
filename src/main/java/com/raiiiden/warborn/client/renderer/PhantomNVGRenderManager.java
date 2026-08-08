package com.raiiiden.warborn.client.renderer;

import com.raiiiden.warborn.client.gui.NVGScreenFadeOverlay;
import com.raiiiden.warborn.common.init.ModItemRegistry;
import com.raiiiden.warborn.common.item.NVGHandItem;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import software.bernie.geckolib.animatable.GeoItem;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class PhantomNVGRenderManager extends AbstractPhantomRenderManager {
    private static PhantomNVGRenderManager INSTANCE;

    private int elapsedTicks = 0;
    private String currentAnimName = "";
    private boolean fadeTriggerred = false;

    // Trigger the screen fade during the first-person motion. Helmet state now changes
    // immediately when that animation starts instead of waiting for the fade midpoint.
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
        ItemStack stack = ModItemRegistry.NVG_HAND_ITEM.get().getDefaultInstance();
        activate(stack, durationTicks, playerUUID);
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

    @Override
    protected void tickActive() {
        elapsedTicks++;

        if (!fadeTriggerred) {
            int triggerTick = FADE_TRIGGER_TICK;
            if (elapsedTicks >= triggerTick) {
                NVGScreenFadeOverlay.triggerFade();
                fadeTriggerred = true;
            }
        }
    }

    @Override
    protected void resetExtraState() {
        this.elapsedTicks = 0;
        this.currentAnimName = "";
        this.fadeTriggerred = false;
    }
}
