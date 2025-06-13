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

    @Inject(method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V", at = @At("TAIL"))
    public void warborn$injectArmAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        if (!(entity instanceof Player player)) return;

        if (player == Minecraft.getInstance().cameraEntity &&
                Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            return;
        }

        int animTick = player.getPersistentData().getInt("NVG_ANIM_TICK");
        if (animTick <= 0) return;

        PlayerModel<?> model = (PlayerModel<?>) (Object) this;

        if (animTick == 1) {
            player.getPersistentData().putFloat("NVG_ANIM_START_X", model.leftArm.xRot);
            player.getPersistentData().putFloat("NVG_ANIM_START_Y", model.leftArm.yRot);
            player.getPersistentData().putFloat("NVG_ANIM_START_Z", model.leftArm.zRot);
        }

        float startX = player.getPersistentData().getFloat("NVG_ANIM_START_X");
        float startY = player.getPersistentData().getFloat("NVG_ANIM_START_Y");
        float startZ = player.getPersistentData().getFloat("NVG_ANIM_START_Z");

        float duration = 8.0f;
        float rawProgress = Math.min(animTick / duration, 1.0f);
        float progress = rawProgress * rawProgress * (3 - 2 * rawProgress); // cubic easing

        float headPitchRadians = (float) Math.toRadians(headPitch);
        float headYawRadians = (float) Math.toRadians(netHeadYaw);

        float baseRot = -140.0f;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.getItem() instanceof WBArmorItem helmetItem && helmet.is(ClientKeyEvents.HAS_TOGGLE_TAG)) {
            if (helmetItem.isTopOpen(helmet)) {
                baseRot = -180.0f;
            }
        }

        float xRotTarget = headPitchRadians + (float) Math.toRadians(baseRot);
        float yRotTarget = headYawRadians * 0.5f;
        float zRotTarget = 0;

        float xRot = lerp(progress, startX, xRotTarget);
        float yRot = lerp(progress, startY, yRotTarget);
        float zRot = lerp(progress, startZ, zRotTarget);

        model.leftArm.xRot = xRot;
        model.leftArm.yRot = yRot;
        model.leftArm.zRot = zRot;

        model.leftSleeve.xRot = xRot;
        model.leftSleeve.yRot = yRot;
        model.leftSleeve.zRot = zRot;
    }

    private static float lerp(float t, float a, float b) {
        return a + t * (b - a);
    }
}
