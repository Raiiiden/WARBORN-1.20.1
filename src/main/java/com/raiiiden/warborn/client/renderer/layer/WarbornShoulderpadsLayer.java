package com.raiiiden.warborn.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.raiiiden.warborn.client.renderer.armor.WarbornGenericArmorRenderer;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

public class WarbornShoulderpadsLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public WarbornShoulderpadsLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        // Find all shoulderpads in curios
        List<SlotResult> shoulderpads = CuriosApi.getCuriosHelper().findCurios(entity,
                stack -> stack.getItem() instanceof WBArmorItem armor && armor.getArmorType().contains("shoulderpads"));

        for (SlotResult slotResult : shoulderpads) {
            // Only render if the slot is visible
            if (slotResult.slotContext().visible()) {
                ItemStack itemStack = slotResult.stack();
                WBArmorItem shoulderpadsItem = (WBArmorItem) itemStack.getItem();

                WarbornGenericArmorRenderer shoulderpadsRenderer = new WarbornGenericArmorRenderer(shoulderpadsItem);
                this.getParentModel().copyPropertiesTo(shoulderpadsRenderer);
                shoulderpadsRenderer.prepForRender(entity, itemStack, EquipmentSlot.CHEST, shoulderpadsRenderer);
                shoulderpadsRenderer.renderToBuffer(
                        poseStack,
                        bufferSource.getBuffer(RenderType.armorCutoutNoCull(shoulderpadsRenderer.getTextureLocation(shoulderpadsItem))),
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        1.0F, 1.0F, 1.0F, 1.0F
                );
            }
        }
    }
}