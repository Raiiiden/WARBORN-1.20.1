package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.NVGHandItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class WarbornNVGHandModel extends DefaultedItemGeoModel<NVGHandItem> {
    public WarbornNVGHandModel() {
        super(new ResourceLocation(WARBORN.MODID, "nvg_hand"));
    }

    @Override
    public ResourceLocation getModelResource(NVGHandItem item) {
        return new ResourceLocation(WARBORN.MODID, "geo/item/nvg_hand.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NVGHandItem item) {
        return new ResourceLocation(WARBORN.MODID, "textures/item/nvg_hand.png");
    }

    @Override
    public ResourceLocation getAnimationResource(NVGHandItem item) {
        return new ResourceLocation(WARBORN.MODID, "animations/item/nvg_hand.animation.json");
    }
}
