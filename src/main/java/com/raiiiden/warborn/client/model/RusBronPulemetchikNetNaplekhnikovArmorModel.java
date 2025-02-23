package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class RusBronPulemetchikNetNaplekhnikovArmorModel extends DefaultedItemGeoModel<WarbornArmorItem> {
    public RusBronPulemetchikNetNaplekhnikovArmorModel() {
        super(new ResourceLocation(WARBORN.MODID, "armor/rus_bron_pulemetchik_net_naplekhnikov"));
    }

    @Override
    public ResourceLocation getTextureResource(WarbornArmorItem item) {
        return new ResourceLocation(WARBORN.MODID, "textures/item/armor/texture_rus_bron.png");
    }
}
