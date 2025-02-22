package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.RusBronRofLikoArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RusBronRofLikoArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public RusBronRofLikoArmorRenderer() {
        super(new RusBronRofLikoArmorModel());
    }
}
