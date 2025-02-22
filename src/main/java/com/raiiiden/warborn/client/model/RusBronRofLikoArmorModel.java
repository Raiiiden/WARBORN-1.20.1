package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class RusBronRofLikoArmorModel extends DefaultedItemGeoModel<WarbornArmorItem> {
    public RusBronRofLikoArmorModel() {
        super(new ResourceLocation(WARBORN.MODID, "armor/rus_bron_rof_liko"));
    }
}
