package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.UkrBronPulemetchikDisguiseArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class UkrBronPulemetchikDisguiseArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public UkrBronPulemetchikDisguiseArmorRenderer() {
        super(new UkrBronPulemetchikDisguiseArmorModel());
    }
}
