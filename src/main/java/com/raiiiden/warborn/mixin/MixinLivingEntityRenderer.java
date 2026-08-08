package com.raiiiden.warborn.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.raiiiden.warborn.client.shader.ShaderRegistry;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Makes entities readable under the vision shaders; digital only brightens, while thermal replaces the texture with a heat signature since a real imager sees emitted IR.
@Mixin(LivingEntityRenderer.class)
public abstract class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
        extends EntityRenderer<T> implements RenderLayerParent<T, M> {

    @Unique
    private static final String DVG_SHADER_ID = "dvg";
    @Unique
    private static final String DVG_SHADERT_ID = "tvg";
    @Unique
    private static final String DVG_SHADERTW_ID = "twvg";
    @Unique
    private static final String DVG_SHADERTB_ID = "tbvg";
    @Unique
    private static final int FULLBRIGHT = 15728880;

    // How much of the mob's own texture survives under thermal, leaving shape detail instead of a flat cut-out.
    @Unique
    private static final float WARBORN$TEXTURE_MIX = 0.15f;

    // Whether the local player's helmet is shut is the same answer for every entity in a frame, but this
    // used to be recomputed per entity - twice, since both injectors ask. Cached against the client tick.
    @Unique
    private static int wARBORN_1_20_1$helmetCheckedAtTick = -1;
    @Unique
    private static boolean wARBORN_1_20_1$helmetClosed;

    protected MixinLivingEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    // packedLight goes to the base model and every RenderLayer, so raising it here lights armor, backpacks, masks and shoulderpads too.
    @ModifyVariable(method = "render", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private int wARBORN_1_20_1$brightenThroughArmor(int packedLight) {
        return (wARBORN_1_20_1$isActive() && wARBORN_1_20_1$isHelmetClosed()) ? FULLBRIGHT : packedLight;
    }

    // The white overlay is the only vertex-level hook that can brighten a black skin, since the colour arguments are multiplicative.
    @WrapOperation(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"))
    private void wrapRender(EntityModel<?> model, PoseStack poseStack, VertexConsumer buffer,
                            int packedLight, int packedOverlay,
                            float red, float green, float blue, float alpha,
                            Operation<Void> original,
                            T entity) {
        if (!wARBORN_1_20_1$isActive() || !wARBORN_1_20_1$isHelmetClosed()) {
            original.call(model, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
            return;
        }

        if (!wARBORN_1_20_1$isThermalActive()) {
            // digital / light amplification: full bright, but no heat colouring, and drop
            // the overlay so the red hurt flash doesn't punch a hole through the image
            original.call(model, poseStack, buffer, packedLight, OverlayTexture.NO_OVERLAY, red, green, blue, alpha);
            return;
        }

        float signature = wARBORN_1_20_1$thermalSignature(entity);
        int heatOverlay = OverlayTexture.pack(OverlayTexture.u(signature), OverlayTexture.v(false));

        original.call(model, poseStack, buffer, packedLight, heatOverlay,
                red * WARBORN$TEXTURE_MIX, green * WARBORN$TEXTURE_MIX, blue * WARBORN$TEXTURE_MIX, alpha);
    }

    // Apparent temperature, 0 (ambient) to 1 (combusting); warm-blooded mobs read bright, arthropods sit near ambient, undead near-invisible.
    @Unique
    private static float wARBORN_1_20_1$thermalSignature(LivingEntity entity) {
        if (entity.isOnFire()
                || entity instanceof Blaze
                || entity instanceof MagmaCube
                || entity instanceof Strider) {
            return 1.0f;    // burning or molten - far above anything else on screen
        }
        if (entity instanceof SnowGolem) {
            return 0.05f;   // made of snow, below ambient
        }

        MobType type = entity.getMobType();
        if (type == MobType.UNDEAD) return 0.14f;      // no metabolism, sits at ambient
        if (type == MobType.ARTHROPOD) return 0.28f;   // cold-blooded, barely above ambient
        if (type == MobType.WATER) return 0.30f;       // cold-blooded and wet

        return 0.85f;       // warm-blooded: players, animals, villagers, creepers, endermen
    }

    // Check if any vision shader that highlights entities is active.
    @Unique
    private boolean wARBORN_1_20_1$isActive() {
        return ShaderRegistry.getInstance().isShaderActive(DVG_SHADER_ID) || wARBORN_1_20_1$isThermalActive();
    }

    // Only the two thermal palettes get the heat-signature treatment.
    @Unique
    private boolean wARBORN_1_20_1$isThermalActive() {
        ShaderRegistry registry = ShaderRegistry.getInstance();
        return registry.isShaderActive(DVG_SHADERT_ID)
                || registry.isShaderActive(DVG_SHADERTW_ID)
                || registry.isShaderActive(DVG_SHADERTB_ID);
    }

    // Check if the player's helmet is closed, at most once per client tick.
    @Unique
    private boolean wARBORN_1_20_1$isHelmetClosed() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        int tick = mc.player.tickCount;
        if (tick == wARBORN_1_20_1$helmetCheckedAtTick) return wARBORN_1_20_1$helmetClosed;

        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        boolean closed = false;
        if (helmet.getItem() instanceof WBArmorItem) {
            // getTag() rather than the item's isTopOpen, which calls getOrCreateTag and so writes to the
            // stack from the render thread just to read a flag.
            CompoundTag tag = helmet.getTag();
            closed = tag == null || !tag.getBoolean("helmet_top_open");
        }

        wARBORN_1_20_1$helmetCheckedAtTick = tick;
        wARBORN_1_20_1$helmetClosed = closed;
        return closed;
    }
}
