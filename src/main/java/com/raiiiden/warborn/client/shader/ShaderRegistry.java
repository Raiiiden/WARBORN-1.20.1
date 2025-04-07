package com.raiiiden.warborn.client.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Registry for managing and applying post-processing shaders
 */
@OnlyIn(Dist.CLIENT)
public class ShaderRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShaderRegistry.class);

    //this is a singleton so we can just call it anywhere.
    private static final ShaderRegistry INSTANCE = new ShaderRegistry();
    
    private final Map<String, ShaderEntry> shaders = new HashMap<>();
    private int lastWidth = -1;
    private int lastHeight = -1;
    
    public static ShaderRegistry getInstance() {
        return INSTANCE;
    }
    
    /**
     * Get a set of all registered shader IDs
     * @return Set of shader IDs
     *
     * This is used to check if a shader is registered and active
     */
    public Set<String> getRegisteredShaderIds() {
        return new HashSet<>(shaders.keySet());
    }
    
    /**
     * Check if a shader is currently active (both registered and its condition evaluates to true)
     * @param id Unique identifier for the shader
     * @return True if the shader is active, false otherwise
     */
    public boolean isShaderActive(String id) {
        ShaderEntry entry = shaders.get(id);
        if (entry == null) {
            LOGGER.debug("Shader {} not found in registry", id);
            return false;
        }
        return (entry.activationCondition.test(Minecraft.getInstance()));
    }
    
    /**
     * Check if a shader is currently force enabled
     * @param id Unique identifier for the shader
     * @return True if the shader is force enabled, false otherwise
     */
    public boolean isShaderForceEnabled(String id) {
        ShaderEntry entry = shaders.get(id);
        if (entry == null) {
            LOGGER.debug("Shader {} not found in registry for force check", id);
            return false;
        }
        return Boolean.TRUE.equals(entry.forceEnabled);
    }
    
    /**
     * Force enable or disable a specific shader regardless of its condition
     * @param id Unique identifier for the shader
     * @param enabled True to enable, false to disable
     * @return true if shader exists and was updated, false if shader not found
     */
    public boolean setShaderEnabled(String id, boolean enabled) {
        ShaderEntry entry = shaders.get(id);
        if (entry == null) return false;
        
        entry.forceEnabled = enabled;
        
        // If disabling, clean up resources immediately
        if (!enabled && entry.shader != null) {
            entry.shader.close();
            entry.shader = null;
        }
        
        return true;
    }
    
    /**
     * Remove shaders whose IDs start with the given prefix
     * @param prefix Prefix to match shader IDs
     * @return number of shaders removed
     *
     * This is useful for cleaning up temporary shaders or those that are no longer needed
     */
    public int removeShadersByPrefix(String prefix) {
        List<String> toRemove = shaders.keySet().stream()
            .filter(id -> id.startsWith(prefix))
            .toList();
            
        for (String id : toRemove) {
            unregisterShader(id);
        }
        
        return toRemove.size();
    }

    // to be honest we could have used neoforge for this but I wanted to do it myself because more control...
    /**
     * Registers a shader with the registry
     * 
     * @param id Unique identifier for the shader
     * @param shaderLocation Resource location for the shader JSON
     * @param activationCondition Predicate to determine when shader should be active
     * @param configurer Consumer to configure shader parameters when applied
     * @return True if registration succeeded
     */
    public boolean registerShader(String id, ResourceLocation shaderLocation, 
                                 Predicate<Minecraft> activationCondition,
                                 Consumer<PostChain> configurer) {
        if (shaders.containsKey(id)) {
            return false;
        }
        
        shaders.put(id, new ShaderEntry(shaderLocation, activationCondition, configurer));
        return true;
    }
    
    /**
     * Unregisters a shader from the registry
     */
    public void unregisterShader(String id) {
        ShaderEntry entry = shaders.remove(id);
        if (entry != null && entry.shader != null) {
            entry.shader.close();
        }
    }
    
    /**
     * Process all active shaders
     */
    public void processShaders() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;
        
        int width = mc.getWindow().getWidth();
        int height = mc.getWindow().getHeight();
        boolean resized = width != lastWidth || height != lastHeight;
        
        if (resized) {
            lastWidth = width;
            lastHeight = height;
        }
        
        for (Map.Entry<String, ShaderEntry> entry : shaders.entrySet()) {
            ShaderEntry shaderEntry = entry.getValue();
            String shaderId = entry.getKey();

            // DEBUG STUFF
            boolean activatedByCondition = shaderEntry.activationCondition.test(mc);
            boolean forceDisabled = Boolean.FALSE.equals(shaderEntry.forceEnabled);
            boolean forceEnabled = Boolean.TRUE.equals(shaderEntry.forceEnabled);

            boolean shouldBeActive = (activatedByCondition && !forceDisabled) || forceEnabled;

            if (shouldBeActive && shaderEntry.shader == null) {
                LOGGER.info("Activating shader: {}", shaderId);
                LOGGER.info("  - activatedByCondition: {}", activatedByCondition);
                LOGGER.info("  - forceEnabled: {}", forceEnabled);
                LOGGER.info("  - forceDisabled: {}", forceDisabled);
            } else if (!shouldBeActive && shaderEntry.shader != null) {
                LOGGER.info("Deactivating shader: {}", shaderId);
            }

            if (shouldBeActive) {
                if (shaderEntry.shader == null) {
                    try {
                        shaderEntry.shader = new PostChain(
                            mc.textureManager,
                            mc.getResourceManager(),
                            mc.getMainRenderTarget(),
                            shaderEntry.shaderLocation
                        );
                        shaderEntry.shader.resize(width, height);
                    } catch (Exception e) {
                        LOGGER.error("Failed to initialize shader: {}", entry.getKey());
                        LOGGER.error(e.getMessage(), e);
                        continue;
                    }
                }

                // adapt to resize, if needed
                if (resized && shaderEntry.shader != null) {
                    shaderEntry.shader.resize(width, height);
                }

                try {
                    shaderEntry.configurer.accept(shaderEntry.shader);
                    shaderEntry.shader.process(mc.getFrameTime());

                    mc.getMainRenderTarget().bindWrite(false);
                } catch (Exception e) {
                    LOGGER.error("Failed to process shader: {}", entry.getKey());
                    LOGGER.error(e.getMessage(), e);
                }
            } else if (shaderEntry.shader != null) {
                shaderEntry.shader.close();
                shaderEntry.shader = null;
            }
        }
    }

    // this is your stuff
    
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
    
    /**
     * Internal class to store shader information
     */
    private static class ShaderEntry {
        final ResourceLocation shaderLocation;
        final Predicate<Minecraft> activationCondition;
        final Consumer<PostChain> configurer;
        PostChain shader;
        Boolean forceEnabled = null; // null=use condition, true=force on, false=force off (for myself)
        ShaderEntry(ResourceLocation shaderLocation, 
                   Predicate<Minecraft> activationCondition,
                   Consumer<PostChain> configurer) {
            this.shaderLocation = shaderLocation;
            this.activationCondition = activationCondition;
            this.configurer = configurer;
        }
    }
} 