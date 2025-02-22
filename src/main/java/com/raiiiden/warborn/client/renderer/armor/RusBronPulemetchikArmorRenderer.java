package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.RusBronPulemetchikArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RusBronPulemetchikArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public RusBronPulemetchikArmorRenderer() {
        super(new RusBronPulemetchikArmorModel());
    }
}