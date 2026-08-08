package com.raiiiden.warborn.client.renderer.layer;

import com.raiiiden.warborn.client.renderer.armor.WarbornBackpackRenderer;
import com.raiiiden.warborn.common.item.BackpackItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public class WarbornBackpackLayer<T extends LivingEntity, M extends HumanoidModel<T>>
        extends WarbornCurioRenderLayer<T, M, BackpackItem> {
    public WarbornBackpackLayer(RenderLayerParent<T, M> renderer) {
        super(renderer, BackpackItem.class,
                item -> true,
                slot -> slot.slotContext().visible()
                        && ("backpack".equals(slot.slotContext().identifier())
                        || "back".equals(slot.slotContext().identifier())),
                EquipmentSlot.CHEST,
                WarbornBackpackRenderer::new,
                (layerRenderer, entity) -> {});
    }
}
