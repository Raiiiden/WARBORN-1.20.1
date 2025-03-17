package com.raiiiden.warborn.client.renderer;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.model.WarbornBackpackModel;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WarbornBackpackRenderer extends GeoArmorRenderer<WarbornArmorItem> {
    public WarbornBackpackRenderer() {
        super(new WarbornBackpackModel());
    }

    @Override
    public RenderType getRenderType(WarbornArmorItem animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }

    @Override
    public ResourceLocation getTextureLocation(WarbornArmorItem item) {
        return new ResourceLocation(WARBORN.MODID, "textures/item/armor/squad_lider_ru_backpack.png");
    }
}
