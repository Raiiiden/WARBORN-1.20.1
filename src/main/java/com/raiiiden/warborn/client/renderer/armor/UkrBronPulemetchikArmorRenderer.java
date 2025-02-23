package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.UkrBronPulemetchikArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class UkrBronPulemetchikArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public UkrBronPulemetchikArmorRenderer() {
        super(new UkrBronPulemetchikArmorModel());
    }
}
