// This is a temp class until I can figure out the
package com.raiiiden.warborn.mixin.client;

import com.raiiiden.warborn.common.item.WarbornArmorItem;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final Logger LOGGER = LogManager.getLogger("Warborn");

    private PlayerRendererMixin() {
        super(null, null, 0f);
    }

    @Inject(method = "setModelProperties", at = @At("TAIL"))
    private void warborn$shrinkVanillaArms(AbstractClientPlayer player, CallbackInfo ci) {
        PlayerModel<AbstractClientPlayer> model = this.getModel();

        if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WarbornArmorItem armor &&
                armor.getArmorType().equals("insurgency_commander")) {

            // Shrink vanilla arms to hide them
            model.leftArm.xScale = 0.0001F;
            model.leftArm.yScale = 0.0001F;
            model.leftArm.zScale = 0.0001F;

            model.rightArm.xScale = 0.0001F;
            model.rightArm.yScale = 0.0001F;
            model.rightArm.zScale = 0.0001F;

        } else {
            model.leftArm.xScale = model.leftArm.yScale = model.leftArm.zScale = 1.0F;
            model.rightArm.xScale = model.rightArm.yScale = model.rightArm.zScale = 1.0F;
        }
    }
}
