package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class RusBronArmorModel extends DefaultedItemGeoModel<WarbornArmorItem> {
    public RusBronArmorModel() {
        super(new ResourceLocation(WARBORN.MODID, "armor/rus_bron"));
    }
}
