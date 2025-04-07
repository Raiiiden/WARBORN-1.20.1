package com.raiiiden.warborn.mixin;

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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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
    /**
     * Completely override the render method for maximum control
     */
    @Redirect(method = "render", at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    private void redirectRender(M model, PoseStack poseStack, VertexConsumer buffer, 
                               int packedLight, int packedOverlay, 
                               float red, float green, float blue, float alpha, 
                               T entity) {
        if (!wARBORN_1_20_1$isActive()) {
            model.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        float heat = wARBORN_1_20_1$calculateEntityHeat(entity);

        int newLight = 15728880;
        int newOverlay = OverlayTexture.NO_OVERLAY;

        float[] heatColor = wARBORN_1_20_1$getHeatColor(heat);

        model.renderToBuffer(poseStack, buffer, newLight, newOverlay, 
                             heatColor[0], heatColor[1], heatColor[2], alpha);
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
    
    /**
     * Map heat value to RGB color
     * Cold = blue/purple (0.0)
     * Warm = yellow/orange (0.7)
     * Hot = white/red (1.0)
     */
    @Unique
    private float[] wARBORN_1_20_1$getHeatColor(float heat) {
        float[] color = new float[3]; // R,G,B
        
        if (heat < 0.3f) {
            color[0] = 0.0f;
            color[1] = 0.0f;
            color[2] = 2.0f;
        } else if (heat < 0.6f) {
            color[0] = 0.0f;
            color[1] = 2.0f;
            color[2] = 0.0f;
        } else {
            color[0] = 2.0f;
            color[1] = 0.0f;
            color[2] = 0.0f;
        }
        
        return color;
    }
    
    /**
     * Calculate heat level for an entity (0.0-1.0)
     */
    @Unique
    private float wARBORN_1_20_1$calculateEntityHeat(LivingEntity entity) {
        float baseHeat = 0.6f;

        if (entity instanceof Monster) {
            baseHeat = 0.7f;
        }

        if (entity instanceof Player) {
            baseHeat = 0.65f;

            if (entity.isCrouching()) {
                baseHeat *= 0.7f;
            }
        }

        float healthPercent = entity.getHealth() / entity.getMaxHealth();
        if (healthPercent < 0.5f) {
            baseHeat += (1 - healthPercent) * 0.3f;
        }

        double movementSpeed = entity.getDeltaMovement().length();
        baseHeat += Math.min(0.3f, (float)movementSpeed * 0.5f);

        if (entity.isOnFire() || entity.isCurrentlyGlowing()) {
            baseHeat = 1.0f;
        }
        
        return Math.min(1.0f, baseHeat);
    }
}