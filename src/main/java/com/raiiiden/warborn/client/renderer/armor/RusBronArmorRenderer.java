package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.RusBronArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RusBronArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public RusBronArmorRenderer() {
        super(new RusBronArmorModel());
    }
}
