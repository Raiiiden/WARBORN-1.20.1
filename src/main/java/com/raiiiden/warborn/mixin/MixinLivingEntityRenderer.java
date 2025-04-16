package com.raiiiden.warborn.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.raiiiden.warborn.client.shader.ShaderRegistry;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Mixin to make entities glow when thermal vision is active
 */
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
        extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    @Unique
    private static final String DVG_SHADER_ID = "dvg";
    @Unique
    private static final String DVG_SHADERT_ID = "tvg";

    protected MixinLivingEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @WrapOperation(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    private void wrapRender(EntityModel<?> model, PoseStack poseStack, VertexConsumer buffer,
                            int packedLight, int packedOverlay,
                            float red, float green, float blue, float alpha,
                            Operation<Void> original,
                            T entity) {
        if (!wARBORN_1_20_1$isActive()) {
            original.call(model, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        int newLight = 15728880;
        int newOverlay = OverlayTexture.NO_OVERLAY;

        original.call(model, poseStack, buffer, newLight, newOverlay, red, green, blue, alpha);
    }

    /**
     * Check if thermal vision is active
     */
    @Unique
    private boolean wARBORN_1_20_1$isActive() {
        boolean dvgActive = ShaderRegistry.getInstance().isShaderActive(DVG_SHADER_ID);
        boolean tvgActive = ShaderRegistry.getInstance().isShaderActive(DVG_SHADERT_ID);

        return dvgActive || tvgActive;
    }
}