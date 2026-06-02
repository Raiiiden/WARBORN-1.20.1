package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.SyringeItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class WarbornSyringeModel extends DefaultedItemGeoModel<SyringeItem> {
    public WarbornSyringeModel() {
        super(new ResourceLocation(WARBORN.MODID, "syringe")); // placeholder
    }

    @Override
    public ResourceLocation getModelResource(SyringeItem item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        return new ResourceLocation(WARBORN.MODID, "geo/item/" + id.getPath() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SyringeItem item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        return new ResourceLocation(WARBORN.MODID, "textures/item/" + id.getPath() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(SyringeItem item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        return new ResourceLocation(WARBORN.MODID, "animations/item/" + id.getPath() + ".animation.json");
    }
}