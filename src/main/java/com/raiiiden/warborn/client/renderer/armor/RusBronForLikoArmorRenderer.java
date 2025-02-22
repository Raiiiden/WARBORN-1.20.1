package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.RusBronForLikoArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RusBronForLikoArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public RusBronForLikoArmorRenderer() {
        super(new RusBronForLikoArmorModel());
    }
}
