package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.RusBronPulemetchikDisguiseArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RusBronPulemetchikDisguiseArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public RusBronPulemetchikDisguiseArmorRenderer() {
        super(new RusBronPulemetchikDisguiseArmorModel());
    }
}
