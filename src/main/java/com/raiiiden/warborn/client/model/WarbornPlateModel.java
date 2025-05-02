package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.ArmorPlateItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.model.DefaultedItemGeoModel;

public class WarbornPlateModel extends DefaultedItemGeoModel<ArmorPlateItem> {
    public WarbornPlateModel() {
        super(new ResourceLocation(WARBORN.MODID, "steel_plate_level_iii")); // placeholder, will use item below
    }

    @Override
    public ResourceLocation getModelResource(ArmorPlateItem item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        return new ResourceLocation(WARBORN.MODID, "geo/item/" + id.getPath() + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ArmorPlateItem item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        return new ResourceLocation(WARBORN.MODID, "textures/item/" + id.getPath() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(ArmorPlateItem item) {
        ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
        return new ResourceLocation(WARBORN.MODID, "animations/item/" + id.getPath() + ".animation.json");
    }
}
