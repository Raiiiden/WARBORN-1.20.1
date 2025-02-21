package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class WarbornArmorModel extends DefaultedItemGeoModel<WarbornArmorItem> {
    public WarbornArmorModel() {
        super(new ResourceLocation(WARBORN.MODID, "armor/rus_armor"));
    }
}
