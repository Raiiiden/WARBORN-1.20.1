package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.model.GeoModel;

public class WarbornBackpackModel extends GeoModel<WarbornArmorItem> {
    @Override
    public ResourceLocation getModelResource(WarbornArmorItem animatable) {
        return new ResourceLocation(WARBORN.MODID, "geo/item/armor/squad_lider_ru_backpack.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WarbornArmorItem animatable) {
        return new ResourceLocation(WARBORN.MODID, "textures/item/armor/squad_lider_ru_backpack.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WarbornArmorItem animatable) {
        return new ResourceLocation(WARBORN.MODID, "animations/armor/squad_lider_ru_backpack.animation.json");
    }
}
