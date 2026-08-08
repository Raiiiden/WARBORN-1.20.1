package com.raiiiden.warborn.client.renderer.layer;

import com.raiiiden.warborn.client.renderer.armor.WarbornGenericArmorRenderer;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class WarbornMaskLayer<T extends LivingEntity, M extends HumanoidModel<T>>
        extends WarbornCurioRenderLayer<T, M, WBArmorItem> {
    public WarbornMaskLayer(RenderLayerParent<T, M> renderer) {
        super(renderer, WBArmorItem.class,
                item -> item.getArmorType().equals("ratnik_pmk-4") || item.isBalaclava(),
                slot -> slot.slotContext().visible(),
                EquipmentSlot.HEAD,
                WarbornGenericArmorRenderer::new,
                (layerRenderer, entity) -> {});
    }
}
