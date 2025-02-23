package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.UkrBronArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class UkrBronArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public UkrBronArmorRenderer() {
        super(new UkrBronArmorModel());
    }
}
