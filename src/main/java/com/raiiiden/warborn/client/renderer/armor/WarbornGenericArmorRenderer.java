package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.WarbornGenericArmorModel;
import com.raiiiden.warborn.common.item.WBArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WarbornGenericArmorRenderer extends GeoArmorRenderer<WBArmorItem> {
    public WarbornGenericArmorRenderer(WBArmorItem item) {
        super(new WarbornGenericArmorModel(item));
    }
}