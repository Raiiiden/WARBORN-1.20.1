package com.raiiiden.warborn.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.raiiiden.warborn.client.renderer.WarbornBackpackRenderer;
import com.raiiiden.warborn.item.WarbornArmorItem;
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

public class WarbornBackpackLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private final WarbornBackpackRenderer backpackRenderer;

    public WarbornBackpackLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
        this.backpackRenderer = new WarbornBackpackRenderer();
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {

        CuriosApi.getCuriosInventory(entity).ifPresent(curios -> {
            curios.getCurios().forEach((slot, handler) -> {
                for (int i = 0; i < handler.getStacks().getSlots(); i++) {
                    ItemStack itemStack = handler.getStacks().getStackInSlot(i);
                    if (itemStack.getItem() instanceof WarbornArmorItem backpackItem) {

                        this.getParentModel().copyPropertiesTo(backpackRenderer);
                        backpackRenderer.prepForRender(entity, itemStack, EquipmentSlot.CHEST, backpackRenderer);
                        backpackRenderer.renderToBuffer(
                                poseStack,
                                bufferSource.getBuffer(RenderType.armorCutoutNoCull(backpackRenderer.getTextureLocation(backpackItem))),
                                packedLight,
                                OverlayTexture.NO_OVERLAY,
                                1.0F, 1.0F, 1.0F, 1.0F
                        );
                    }
                }
            });
        });
    }
}
