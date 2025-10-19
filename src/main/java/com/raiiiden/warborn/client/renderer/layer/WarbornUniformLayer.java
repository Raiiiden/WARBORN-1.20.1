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

public class WarbornUniformLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public WarbornUniformLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        // Find all uniform in curios
        List<SlotResult> uniform = CuriosApi.getCuriosHelper().findCurios(entity,
                stack -> stack.getItem() instanceof WBArmorItem armor && armor.getArmorType().contains("uniform"));

        for (SlotResult slotResult : uniform) {
            // Only render if the slot is visible
            if (slotResult.slotContext().visible()) {
                ItemStack itemStack = slotResult.stack();
                WBArmorItem uniformItem = (WBArmorItem) itemStack.getItem();

                WarbornGenericArmorRenderer uniformRenderer = new WarbornGenericArmorRenderer(uniformItem);
                this.getParentModel().copyPropertiesTo(uniformRenderer);
                uniformRenderer.prepForRender(entity, itemStack, EquipmentSlot.CHEST, uniformRenderer);
                uniformRenderer.renderToBuffer(
                        poseStack,
                        bufferSource.getBuffer(RenderType.armorCutoutNoCull(uniformRenderer.getTextureLocation(uniformItem))),
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        1.0F, 1.0F, 1.0F, 1.0F
                );
            }
        }
    }
}