package com.raiiiden.warborn.client.renderer.layer;

import com.raiiiden.warborn.client.renderer.armor.WarbornGenericArmorRenderer;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class WarbornUniformLayer<T extends LivingEntity, M extends HumanoidModel<T>>
        extends WarbornCurioRenderLayer<T, M, WBArmorItem> {
    public WarbornUniformLayer(RenderLayerParent<T, M> renderer) {
        super(renderer, WBArmorItem.class,
                WBArmorItem::isUniform,
                slot -> slot.slotContext().visible(),
                EquipmentSlot.CHEST,
                WarbornGenericArmorRenderer::new,
                (layerRenderer, entity) -> {});
    }
}
