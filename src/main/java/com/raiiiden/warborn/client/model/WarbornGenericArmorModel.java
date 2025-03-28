package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class WarbornGenericArmorModel extends DefaultedItemGeoModel<WarbornArmorItem> {
    public WarbornGenericArmorModel(WarbornArmorItem item) {
        super(new ResourceLocation(WARBORN.MODID, "armor/" + (item != null ? item.getArmorType() : "default")));
    }

    @Override
    public ResourceLocation getTextureResource(WarbornArmorItem item) {
        return new ResourceLocation(WARBORN.MODID, "textures/item/armor/" + (item != null ? item.getArmorType() : "default") + ".png");
    }
}
