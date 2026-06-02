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

public class WarbornMaskLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public WarbornMaskLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        // Find all masks in curios
        List<SlotResult> masks = CuriosApi.getCuriosHelper().findCurios(entity,
                stack -> stack.getItem() instanceof WBArmorItem armor && armor.getArmorType().contains("ratnik_pmk-4"));

        for (SlotResult slotResult : masks) {
            // Only render if the slot is visible
            if (slotResult.slotContext().visible()) {
                ItemStack itemStack = slotResult.stack();
                WBArmorItem maskItem = (WBArmorItem) itemStack.getItem();

                WarbornGenericArmorRenderer maskRenderer = new WarbornGenericArmorRenderer(maskItem);
                this.getParentModel().copyPropertiesTo(maskRenderer);
                maskRenderer.prepForRender(entity, itemStack, EquipmentSlot.HEAD, maskRenderer);
                maskRenderer.renderToBuffer(
                        poseStack,
                        bufferSource.getBuffer(RenderType.armorCutoutNoCull(maskRenderer.getTextureLocation(maskItem))),
                        packedLight,
                        OverlayTexture.NO_OVERLAY,
                        1.0F, 1.0F, 1.0F, 1.0F
                );
            }
        }
    }
}