package com.raiiiden.warborn.client.model;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.item.WarbornWeaponItem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.geckolib.model.GeoModel;

import java.util.Objects;

public class WarbornGenericWeaponModel extends GeoModel<WarbornWeaponItem> {

    @Override
    public ResourceLocation getModelResource(WarbornWeaponItem item) {
        return new ResourceLocation(WARBORN.MODID, "geo/item/weapon/" + getName(item) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WarbornWeaponItem item) {
        return new ResourceLocation(WARBORN.MODID, "textures/item/weapon/" + getName(item) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(WarbornWeaponItem item) {
        return null; // Add an animation file if needed
    }

    private String getName(WarbornWeaponItem item) {
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).getPath();
    }
}
