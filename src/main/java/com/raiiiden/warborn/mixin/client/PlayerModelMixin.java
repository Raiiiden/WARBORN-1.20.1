package com.raiiiden.warborn.mixin.client;

import com.raiiiden.warborn.client.event.ClientKeyEvents;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin<T extends LivingEntity> {

    @Inject(method = "setupAnim", at = @At("TAIL"), remap = true)
    public void warborn$injectArmAnim(
            T entity, float limbSwing, float limbSwingAmount,
            float ageInTicks, float netHeadYaw, float headPitch,
            CallbackInfo ci) {

        if (!(entity instanceof Player player)) return;
        if (player == Minecraft.getInstance().cameraEntity &&
                Minecraft.getInstance().options.getCameraType().isFirstPerson()) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!helmet.is(ClientKeyEvents.HAS_TOGGLE_TAG)) return;

        int animTick = player.getPersistentData().getInt("NVG_ANIM_TICK");
        if (animTick <= 0) return;

        PlayerModel<?> model = (PlayerModel<?>)(Object)this;

        // Save starting pose
        if (animTick == 1) {
            player.getPersistentData().putFloat("NVG_ANIM_START_X", model.leftArm.xRot);
            player.getPersistentData().putFloat("NVG_ANIM_START_Y", model.leftArm.yRot);
        }

        float startX = player.getPersistentData().getFloat("NVG_ANIM_START_X");
        float startY = player.getPersistentData().getFloat("NVG_ANIM_START_Y");

        // Animation duration (full out-and-back)
        final float duration = 16f;
        float half = duration / 2f;
        float progress;

        // Progress goes from 0->1->0
        if (animTick <= half) {
            progress = animTick / half;
        } else {
            progress = (duration - animTick) / half;
        }
        progress = cubicEase(progress);

        // Up/Down rotation
        float baseRot = (helmet.getItem() instanceof WBArmorItem hi && hi.isTopOpen(helmet)) ? -160f : -130f;
        float targetX = (float)Math.toRadians(headPitch + baseRot);
        targetX = clamp(targetX, (float)Math.toRadians(-220), (float)Math.toRadians(130));
        float newX = startX + (targetX - startX) * progress;

        // Left/Right sway
        float maxSway = 30f;
        float targetY = (float)Math.toRadians(netHeadYaw * 0.5f);
        targetY = clamp(targetY, (float)Math.toRadians(-maxSway), (float)Math.toRadians(maxSway));
        float newY = startY + (targetY - startY) * progress;

        // Z static
        float newZ = 0f;

        // Apply rotations
        model.leftArm.xRot = newX;
        model.leftArm.yRot = newY;
        model.leftArm.zRot = newZ;
        model.leftSleeve.xRot = newX;
        model.leftSleeve.yRot = newY;
        model.leftSleeve.zRot = newZ;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float cubicEase(float t) {
        return t * t * (3 - 2 * t);
    }
}