package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class UkrBronPulemetchikDisguiseArmorModel extends DefaultedItemGeoModel<WarbornArmorItem> {
    public UkrBronPulemetchikDisguiseArmorModel() {
        super(new ResourceLocation(WARBORN.MODID, "armor/ukr_bron_pulemetchik_disguise"));
    }

    @Override
    public ResourceLocation getTextureResource(WarbornArmorItem item) {
        return new ResourceLocation(WARBORN.MODID, "textures/item/armor/texture_ukr_broon.png");
    }
}
