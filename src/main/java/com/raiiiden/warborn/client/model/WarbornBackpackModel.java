package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.BackpackItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class WarbornBackpackModel extends DefaultedItemGeoModel<BackpackItem> {
    public WarbornBackpackModel(BackpackItem item) {
        super(new ResourceLocation(WARBORN.MODID, "armor/" + (item != null ? item.getArmorType() : "default")));
    }

    @Override
    public ResourceLocation getTextureResource(BackpackItem item) {
        return new ResourceLocation(WARBORN.MODID, "textures/item/armor/" + (item != null ? item.getArmorType() : "default") + ".png");
    }
}