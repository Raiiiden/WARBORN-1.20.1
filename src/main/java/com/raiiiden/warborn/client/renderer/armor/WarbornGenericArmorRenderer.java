package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.WarbornGenericArmorModel;
import com.raiiiden.warborn.common.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WarbornGenericArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public WarbornGenericArmorRenderer(WarbornArmorItem item) {
        super(new WarbornGenericArmorModel(item));
    }
}