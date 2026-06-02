package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.WarbornGenericArmorModel;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.Optional;

public class WarbornGenericArmorRenderer extends GeoArmorRenderer<WBArmorItem> {

    public WarbornGenericArmorRenderer(WBArmorItem item) {
        super(new WarbornGenericArmorModel(item));
    }

    public void applySlimArmScaleIfNeeded(LivingEntity entity) {
        if (isSlimArmed(entity)) {
            scaleArmBone("armorRightArm", 1.0f, 1.0f, 1.0f);
            scaleArmBone("armorLeftArm",  1.0f, 1.0f, 1.0f);
        }
    }

    private void scaleArmBone(String boneName, float scaleX, float scaleY, float scaleZ) {
        Optional<GeoBone> bone = getGeoModel().getBone(boneName);
        bone.ifPresent(b -> {
            b.setScaleX(scaleX);
            b.setScaleY(scaleY);
            b.setScaleZ(scaleZ);
        });
    }

    public static boolean isSlimArmed(LivingEntity entity) {
        if (!(entity instanceof AbstractClientPlayer player)) return false;
        return "slim".equals(player.getModelName());
    }
}
