package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.WarbornBackpackModel;
import com.raiiiden.warborn.common.item.BackpackItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WarbornBackpackRenderer extends GeoArmorRenderer<BackpackItem> {
    public WarbornBackpackRenderer(BackpackItem item) {
        super(new WarbornBackpackModel(item));
    }
}