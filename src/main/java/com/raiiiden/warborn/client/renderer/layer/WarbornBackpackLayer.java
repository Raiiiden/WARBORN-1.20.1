package com.raiiiden.warborn.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.raiiiden.warborn.client.renderer.armor.WarbornBackpackRenderer;
import com.raiiiden.warborn.common.item.WarbornBackpackItem;
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

public class WarbornBackpackLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public WarbornBackpackLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                       T entity, float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        // Get equipped backpack
        List<SlotResult> backpacks = CuriosApi.getCuriosHelper().findCurios(entity,
                stack -> stack.getItem() instanceof WarbornBackpackItem);

        for (SlotResult slotResult : backpacks) {
            // Only render if:
            // 1. It's in the back slot
            // 2. The slot is visible (using the visible() method from SlotContext)
            if (slotResult.slotContext().identifier().equals("back") &&
                    slotResult.slotContext().visible()) {

                renderBackpack(poseStack, bufferSource, packedLight, entity,
                        (WarbornBackpackItem) slotResult.stack().getItem(),
                        slotResult.stack());
            }
        }
    }

    private void renderBackpack(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                                T entity, WarbornBackpackItem backpackItem, ItemStack stack) {
        WarbornBackpackRenderer renderer = new WarbornBackpackRenderer(backpackItem);
        this.getParentModel().copyPropertiesTo(renderer);
        renderer.prepForRender(entity, stack, EquipmentSlot.CHEST, renderer);
        renderer.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.armorCutoutNoCull(renderer.getTextureLocation(backpackItem))),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );
    }
}