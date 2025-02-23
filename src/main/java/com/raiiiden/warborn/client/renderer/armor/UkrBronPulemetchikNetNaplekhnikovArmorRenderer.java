package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.UkrBronPulemetchikNetNaplekhnikovArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class UkrBronPulemetchikNetNaplekhnikovArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public UkrBronPulemetchikNetNaplekhnikovArmorRenderer() {
        super(new UkrBronPulemetchikNetNaplekhnikovArmorModel());
    }
}
