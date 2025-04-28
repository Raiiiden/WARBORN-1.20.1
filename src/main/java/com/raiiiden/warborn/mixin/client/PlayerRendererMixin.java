package com.raiiiden.warborn.mixin.client;

import com.raiiiden.warborn.client.renderer.WBRenderState;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final Set<String> VALID_ARMOR_TYPES = Set.of("insurgency_commander", "beta7", "beta7_ash", "beta7_slate");

    private PlayerRendererMixin() {
        super(null, null, 0f);
    }

    @Inject(method = "setModelProperties", at = @At("TAIL"))
    private void warborn$shrinkVanillaArms(AbstractClientPlayer player, CallbackInfo ci) {
        PlayerModel<AbstractClientPlayer> model = this.getModel();

        WBRenderState.HIDDEN_PARTS.clear();

        if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof WBArmorItem armor &&
                VALID_ARMOR_TYPES.contains(armor.getArmorType())) {

            // Hide leftArm, rightArm, AND leftSleeve, rightSleeve
            WBRenderState.HIDDEN_PARTS.add(model.leftArm);
            WBRenderState.HIDDEN_PARTS.add(model.rightArm);
            WBRenderState.HIDDEN_PARTS.add(model.leftSleeve);
            WBRenderState.HIDDEN_PARTS.add(model.rightSleeve);

        }
    }
}
