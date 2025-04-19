package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.WarbornBackpackModel;
import com.raiiiden.warborn.common.item.WarbornBackpackItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WarbornBackpackRenderer extends GeoArmorRenderer<WarbornBackpackItem> {
    public WarbornBackpackRenderer(WarbornBackpackItem item) {
        super(new WarbornBackpackModel(item));
    }
}