package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.model.RusBronShturmovikDisguiseArmorModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class RusBronShturmovikDisguiseArmorRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public RusBronShturmovikDisguiseArmorRenderer() {
        super(new RusBronShturmovikDisguiseArmorModel());
    }
}
