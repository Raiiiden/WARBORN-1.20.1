package com.raiiiden.warborn.client.shader;

import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Registry for managing and applying post-processing shaders
 */
@OnlyIn(Dist.CLIENT)
public class ShaderRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShaderRegistry.class);

    private static final ShaderRegistry INSTANCE = new ShaderRegistry();

    private final Map<String, ShaderEntry> shaders = new HashMap<>();
    private String currentActiveShader = null; // Track which shader is currently loaded

    public static ShaderRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Helper to get PostPass list from PostChain
     */
    public static List<PostPass> getPasses(PostChain chain) {
        try {
            Field field = PostChain.class.getDeclaredField("passes");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<PostPass> passes = (List<PostPass>) field.get(chain);
            return passes;
        } catch (Exception e) {
            LOGGER.error("Failed to get passes from PostChain", e);
            return List.of();
        }
    }

    public Set<String> getRegisteredShaderIds() {
        return new HashSet<>(shaders.keySet());
    }

    public boolean isShaderActive(String id) {
        ShaderEntry entry = shaders.get(id);
        if (entry == null) {
            LOGGER.debug("Shader {} not found in registry", id);
            return false;
        }
        return (entry.activationCondition.test(Minecraft.getInstance()));
    }

    public boolean isShaderForceEnabled(String id) {
        ShaderEntry entry = shaders.get(id);
        if (entry == null) {
            LOGGER.debug("Shader {} not found in registry for force check", id);
            return false;
        }
        return Boolean.TRUE.equals(entry.forceEnabled);
    }

    public boolean setShaderEnabled(String id, boolean enabled) {
        ShaderEntry entry = shaders.get(id);
        if (entry == null) return false;

        entry.forceEnabled = enabled;

        // If disabling and this is the current shader, shut it down
        if (!enabled && id.equals(currentActiveShader)) {
            Minecraft.getInstance().gameRenderer.shutdownEffect();
            currentActiveShader = null;
        }

        return true;
    }

    public int removeShadersByPrefix(String prefix) {
        List<String> toRemove = shaders.keySet().stream()
                .filter(id -> id.startsWith(prefix))
                .toList();

        for (String id : toRemove) {
            unregisterShader(id);
        }

        return toRemove.size();
    }

    public boolean registerShader(String id, ResourceLocation shaderLocation,
                                  Predicate<Minecraft> activationCondition,
                                  Consumer<PostChain> configurer) {
        if (shaders.containsKey(id)) {
            return false;
        }

        shaders.put(id, new ShaderEntry(shaderLocation, activationCondition, configurer));
        return true;
    }

    public void unregisterShader(String id) {
        ShaderEntry entry = shaders.remove(id);
        if (entry != null && id.equals(currentActiveShader)) {
            Minecraft.getInstance().gameRenderer.shutdownEffect();
            currentActiveShader = null;
        }
    }

    /**
     * Process all active shaders - NEW APPROACH using GameRenderer
     */
    public void processShaders() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // Check helmet top state first
        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.getItem() instanceof WBArmorItem wbArmorItem) {
            if (wbArmorItem.isTopOpen(helmet)) {
                // Helmet open → disable all shaders
                if (currentActiveShader != null) {
                    mc.gameRenderer.shutdownEffect();
                    currentActiveShader = null;
                }
                return;
            }
        }

        // Find the highest priority shader that should be active
        String shaderToActivate = null;
        ShaderEntry entryToActivate = null;

        for (Map.Entry<String, ShaderEntry> entry : shaders.entrySet()) {
            ShaderEntry shaderEntry = entry.getValue();
            String shaderId = entry.getKey();

            boolean activatedByCondition = shaderEntry.activationCondition.test(mc);
            boolean forceDisabled = Boolean.FALSE.equals(shaderEntry.forceEnabled);
            boolean forceEnabled = Boolean.TRUE.equals(shaderEntry.forceEnabled);

            boolean shouldBeActive = (activatedByCondition && !forceDisabled) || forceEnabled;

            if (shouldBeActive) {
                shaderToActivate = shaderId;
                entryToActivate = shaderEntry;
                break; // Use first active shader found (you may want to add priority logic)
            }
        }

        // Handle shader switching
        if (shaderToActivate != null && !shaderToActivate.equals(currentActiveShader)) {
            // Need to switch to a different shader
            LOGGER.info("Switching to shader: {}", shaderToActivate);

            GameRenderer gameRenderer = mc.gameRenderer;
            gameRenderer.loadEffect(entryToActivate.shaderLocation);
            currentActiveShader = shaderToActivate;

            // Apply configurer after a short delay to ensure shader is loaded
            PostChain currentEffect = gameRenderer.currentEffect();
            if (currentEffect != null) {
                try {
                    entryToActivate.configurer.accept(currentEffect);
                } catch (Exception e) {
                    LOGGER.error("Failed to configure shader: {}", shaderToActivate, e);
                }
            }
        } else if (shaderToActivate == null && currentActiveShader != null) {
            // Need to disable current shader
            LOGGER.info("Disabling shader: {}", currentActiveShader);
            mc.gameRenderer.shutdownEffect();
            currentActiveShader = null;
        } else if (shaderToActivate != null && shaderToActivate.equals(currentActiveShader)) {
            // Same shader is active, just update uniforms if needed
            GameRenderer gameRenderer = mc.gameRenderer;
            PostChain currentEffect = gameRenderer.currentEffect();
            if (currentEffect != null && entryToActivate != null) {
                try {
                    entryToActivate.configurer.accept(currentEffect);
                } catch (Exception e) {
                    LOGGER.error("Failed to update shader uniforms: {}", shaderToActivate, e);
                }
            }
        }
    }

    /**
     * Internal class to store shader information
     */
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