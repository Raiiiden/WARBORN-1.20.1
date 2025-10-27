package com.raiiiden.warborn.mixin.client;

import com.raiiiden.warborn.client.shader.ShaderRegistry;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class ShaderProtectionMixin {

    @Shadow
    public PostChain postEffect;

    @Inject(method = "checkEntityPostEffect", at = @At("HEAD"), cancellable = true)
    private void protectWarbornShaderCheckEntity(Entity pEntity, CallbackInfo ci) {
        PostChain effect = this.postEffect;

        if (effect == null) return;

        String effectName = effect.getName();
        if (effectName == null || !effectName.contains("fracturepoint")) return;

        ShaderRegistry.getInstance().onExternalShutdownAttempt();
        ci.cancel();
    }

    @Inject(method = "shutdownEffect", at = @At("HEAD"), cancellable = true)
    private void protectWarbornShader(CallbackInfo ci) {
        PostChain effect = this.postEffect;

        if (effect == null) return;

        String effectName = effect.getName();
        if (effectName == null || !effectName.contains("fracturepoint")) return;

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();

            if (className.startsWith("com.raiiiden.warborn")) {
                return;
            }
        }

        ShaderRegistry.getInstance().onExternalShutdownAttempt();
        ci.cancel();
    }
}