package com.raiiiden.warborn.client.shader;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

@OnlyIn(Dist.CLIENT)
public class ShaderRegistry {
    private static final ShaderRegistry INSTANCE = new ShaderRegistry();

    private static String cachedPassesFieldName = null;

    private final Map<String, ShaderEntry> shaders = new LinkedHashMap<>(); // preserve registration order
    private String currentActiveShader = null;
    private boolean isProcessing = false;

    // External shutdown tracking
    private long lastExternalShutdownAttempt = 0;
    private int externalShutdownCount = 0;
    private static final long SHUTDOWN_ATTEMPT_WINDOW_MS = 5000; // 5 second window
    private boolean suppressWarnings = false;

    private ShaderRegistry() {}

    public static ShaderRegistry getInstance() {
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public static List<PostPass> getPasses(PostChain chain) {
        try {
            if (cachedPassesFieldName != null) {
                try {
                    Field f = PostChain.class.getDeclaredField(cachedPassesFieldName);
                    f.setAccessible(true);
                    Object v = f.get(chain);
                    if (v instanceof List<?>) return (List<PostPass>) v;
                    cachedPassesFieldName = null;
                } catch (Throwable ignored) {
                    cachedPassesFieldName = null;
                }
            }

            String[] names = {"passes", "f_110007_", "m_110007_"};
            for (String name : names) {
                try {
                    Field f = PostChain.class.getDeclaredField(name);
                    f.setAccessible(true);
                    Object v = f.get(chain);
                    if (v instanceof List<?>) {
                        cachedPassesFieldName = name;
                        return (List<PostPass>) v;
                    }
                } catch (Throwable ignored) {}
            }

            for (Field f : PostChain.class.getDeclaredFields()) {
                try {
                    if (List.class.isAssignableFrom(f.getType())) {
                        f.setAccessible(true);
                        Object v = f.get(chain);
                        if (v instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof PostPass) {
                            cachedPassesFieldName = f.getName();
                            return (List<PostPass>) v;
                        }
                    }
                } catch (Throwable ignored) {}
            }
        } catch (Throwable ignored) {}
        return List.of();
    }

    public Set<String> getRegisteredShaderIds() {
        return new HashSet<>(shaders.keySet());
    }

    public boolean isShaderActive(String id) {
        ShaderEntry entry = shaders.get(id);
        if (entry == null) return false;
        return entry.activationCondition.test(Minecraft.getInstance());
    }

    public boolean isShaderForceEnabled(String id) {
        ShaderEntry entry = shaders.get(id);
        if (entry == null) return false;
        return Boolean.TRUE.equals(entry.forceEnabled);
    }

    public boolean setShaderEnabled(String id, boolean enabled) {
        ShaderEntry entry = shaders.get(id);
        if (entry == null) return false;

        entry.forceEnabled = enabled;

        if (!enabled && id.equals(currentActiveShader)) {
            try { Minecraft.getInstance().gameRenderer.shutdownEffect(); } catch (Throwable ignored) {}
            currentActiveShader = null;
        }

        return true;
    }

    public int removeShadersByPrefix(String prefix) {
        List<String> toRemove = new ArrayList<>();
        for (String id : shaders.keySet()) if (id.startsWith(prefix)) toRemove.add(id);
        for (String id : toRemove) unregisterShader(id);
        return toRemove.size();
    }

    public boolean registerShader(String id, ResourceLocation shaderLocation,
                                  Predicate<Minecraft> activationCondition,
                                  Consumer<PostChain> configurer) {
        if (shaders.containsKey(id)) return false;
        shaders.put(id, new ShaderEntry(shaderLocation, activationCondition, configurer));
        return true;
    }

    public void unregisterShader(String id) {
        ShaderEntry entry = shaders.remove(id);
        if (entry != null && id.equals(currentActiveShader)) {
            try { Minecraft.getInstance().gameRenderer.shutdownEffect(); } catch (Throwable ignored) {}
            currentActiveShader = null;
        }
    }

    /**
     * Called by the mixin when an external mod tries to shut down our shader
     */
    public void onExternalShutdownAttempt() {
        long now = System.currentTimeMillis();

        // Reset counter if outside the time window
        if (now - lastExternalShutdownAttempt > SHUTDOWN_ATTEMPT_WINDOW_MS) {
            externalShutdownCount = 0;
            suppressWarnings = false;
        }

        lastExternalShutdownAttempt = now;
        externalShutdownCount++;

        // After 10 attempts in 5 seconds, suppress warnings to avoid log spam
        if (externalShutdownCount >= 1 && !suppressWarnings) {
            System.err.println("[Warborn] Shader shutdown attempts detected " + externalShutdownCount +
                    " times. Suppressing further warnings for this session.");
            suppressWarnings = true;

            // Log the culprit once
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stack) {
                String className = element.getClassName();
                if (!className.startsWith("com.raiiiden.warborn") &&
                        !className.startsWith("net.minecraft") &&
                        !className.startsWith("java.") &&
                        !className.contains("EventBus") &&
                        !className.contains("mixin")) {
                    System.err.println("[Warborn] Likely culprit: " + className + "." + element.getMethodName());
                    break;
                }
            }
        } else if (!suppressWarnings) {
            System.err.println("[Warborn] External shader shutdown blocked (" + externalShutdownCount + " attempts)");
        }
    }

    public void processShaders() {
        if (isProcessing) return;
        isProcessing = true;
        try {
            processShaders_Internal();
        } finally {
            isProcessing = false;
        }
    }

    /*
     * Optimized hot path:
     * - Only reload shader when it actually needs to change
     * - Mixin blocks external shutdowns, so no need to check currentEffect()
     * - Minimal rebinds when switching
     * - No logging
     */
    private void processShaders_Internal() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        GameRenderer renderer = mc.gameRenderer;
        RenderTarget mainTarget = mc.getMainRenderTarget();

        // Helmet top-open disables shader immediately
        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.getItem() instanceof WBArmorItem wbArmorItem && wbArmorItem.isTopOpen(helmet)) {
            if (currentActiveShader != null) {
                try { renderer.shutdownEffect(); } catch (Throwable ignored) {}
                currentActiveShader = null;
            }
            return;
        }

        // Find first shader that should be active (respect registration order)
        String shaderToActivate = null;
        ShaderEntry entryToActivate = null;
        for (Map.Entry<String, ShaderEntry> e : shaders.entrySet()) {
            ShaderEntry se = e.getValue();
            boolean forceEnabled = Boolean.TRUE.equals(se.forceEnabled);
            boolean forceDisabled = Boolean.FALSE.equals(se.forceEnabled);
            boolean cond = se.activationCondition.test(mc);
            boolean shouldBeActive = (cond && !forceDisabled) || forceEnabled;
            if (shouldBeActive) {
                shaderToActivate = e.getKey();
                entryToActivate = se;
                break;
            }
        }

        // Nothing should be active -> disable if currently active
        if (shaderToActivate == null) {
            if (currentActiveShader != null) {
                try { renderer.shutdownEffect(); } catch (Throwable ignored) {}
                currentActiveShader = null;
            }
            return;
        }

        // Already active, nothing to do (mixin prevents external shutdowns)
        if (shaderToActivate.equals(currentActiveShader)) return;

        // Activation path (only when shader actually changes)
        try {
            try { renderer.shutdownEffect(); } catch (Throwable ignored) {}

            try {
                renderer.loadEffect(entryToActivate.shaderLocation);
            } catch (Throwable ignored) {
                currentActiveShader = null;
                return;
            }

            PostChain effect = renderer.currentEffect();
            if (effect != null) {
                try { entryToActivate.configurer.accept(effect); } catch (Throwable ignored) {}
            }

            try { mainTarget.bindWrite(true); } catch (Throwable ignored) {}
            try { RenderSystem.bindTextureForSetup(mainTarget.getColorTextureId()); } catch (Throwable ignored) {}
            try { RenderSystem.bindTextureForSetup(mainTarget.getDepthTextureId()); } catch (Throwable ignored) {}

            currentActiveShader = shaderToActivate;
        } finally {
            // intentionally minimal
        }
    }

    private static class ShaderEntry {
        final ResourceLocation shaderLocation;
        final Predicate<Minecraft> activationCondition;
        final Consumer<PostChain> configurer;
        Boolean forceEnabled = null;

        ShaderEntry(ResourceLocation shaderLocation,
                    Predicate<Minecraft> activationCondition,
                    Consumer<PostChain> configurer) {
            this.shaderLocation = shaderLocation;
            this.activationCondition = activationCondition;
            this.configurer = configurer;
        }
    }
}