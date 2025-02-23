package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.RusBronShturmovikArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RusBronShturmovikArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public RusBronShturmovikArmorRenderer() {
        super(new RusBronShturmovikArmorModel());
    }
}
