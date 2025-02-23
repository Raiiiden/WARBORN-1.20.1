package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class RusBronPulemetchikDisguiseArmorModel extends DefaultedItemGeoModel<WarbornArmorItem> {
    public RusBronPulemetchikDisguiseArmorModel() {
        super(new ResourceLocation(WARBORN.MODID, "armor/rus_bron_pulemetchik_disguise"));
    }

    @Override
    public ResourceLocation getTextureResource(WarbornArmorItem item) {
        return new ResourceLocation(WARBORN.MODID, "textures/item/armor/rus_bron_rof_liko.png");
    }
}
