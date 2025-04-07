package com.raiiiden.warborn.client.renderer.weapon;

import com.raiiiden.warborn.client.model.WarbornGenericWeaponModel;
import com.raiiiden.warborn.item.WarbornWeaponItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WarbornGenericWeaponRenderer extends GeoItemRenderer<WarbornWeaponItem> {
    public WarbornGenericWeaponRenderer() {
        super(new WarbornGenericWeaponModel());
    }

    @Override
    public RenderType getRenderType(WarbornWeaponItem animatable, ResourceLocation texture,
                                    @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.itemEntityTranslucentCull(texture);
    }
}
