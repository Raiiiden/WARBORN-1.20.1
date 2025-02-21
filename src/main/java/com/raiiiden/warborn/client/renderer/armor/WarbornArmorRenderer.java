package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.WarbornArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WarbornArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public WarbornArmorRenderer() {
        super(new WarbornArmorModel());
    }
}
