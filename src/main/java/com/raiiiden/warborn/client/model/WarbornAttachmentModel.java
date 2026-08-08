package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

// A model pinned to one set of assets rather than to the item wearing it.
//
// WarbornGenericArmorModel resolves its geo, texture and animation from the item's own armorType,
// which is what you want for a piece that owns its model. An attachment is the opposite case: a
// part borrowed from another set and rendered over a helmet whose geo has nothing like it.
public class WarbornAttachmentModel extends DefaultedItemGeoModel<WBArmorItem> {
    private final ResourceLocation texture;

    public WarbornAttachmentModel(String assetName) {
        super(new ResourceLocation(WARBORN.MODID, "armor/" + assetName));
        this.texture = new ResourceLocation(WARBORN.MODID, "textures/item/armor/" + assetName + ".png");
    }

    @Override
    public ResourceLocation getTextureResource(WBArmorItem item) {
        return this.texture;
    }
}
