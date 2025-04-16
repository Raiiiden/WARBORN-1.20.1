package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.WarbornPlateItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

/**
 * GeckoLib model for the Warborn Plate item
 * WIP NOT DONE
 */
public class WarbornPlateModel extends GeoModel<WarbornPlateItem> {
    private static final ResourceLocation MODEL_LOCATION = new ResourceLocation(WARBORN.MODID, "geo/item/armor/plate_insert.geo.json");
    private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation(WARBORN.MODID, "textures/item/armor/plate_insert.png");
    private static final ResourceLocation ANIMATION_LOCATION = new ResourceLocation(WARBORN.MODID, "animations/item/armor/plate_insert.animation.json");

    @Override
    public ResourceLocation getModelResource(WarbornPlateItem animatable) {
        return MODEL_LOCATION;
    }

    @Override
    public ResourceLocation getTextureResource(WarbornPlateItem animatable) {
        return TEXTURE_LOCATION;
    }

    @Override
    public ResourceLocation getAnimationResource(WarbornPlateItem animatable) {
        return ANIMATION_LOCATION;
    }
}
