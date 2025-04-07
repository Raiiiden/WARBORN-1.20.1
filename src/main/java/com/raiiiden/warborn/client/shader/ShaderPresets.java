package com.raiiiden.warborn.client.shader;

import com.raiiiden.warborn.WARBORN;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

/**
 * Preset configurations for commonly used shaders
 * All do better docs later...
 */
public class ShaderPresets {
    public static final ResourceLocation NIGHT_VISION = new ResourceLocation(WARBORN.MODID, "shaders/post/night-vision.json");
    public static final ResourceLocation SIMPLE_NIGHT_VISION = new ResourceLocation(WARBORN.MODID, "shaders/post/simple-night-vision.json");
    public static final ResourceLocation DIGITAL_GREEN_VISION = new ResourceLocation(WARBORN.MODID, "shaders/post/digital-green-vision.json");
    public static final ResourceLocation DIGITAL_WHITE_VISION = new ResourceLocation(WARBORN.MODID, "shaders/post/digital-white-vision.json");
    public static final ResourceLocation THERMAL_VISION = new ResourceLocation(WARBORN.MODID, "shaders/post/thermal-vision.json");

    public static Consumer<PostChain> nightVision(float brightness, float redTint, float greenTint, float blueTint) {
        return shader -> {
            List<PostPass> passes = ShaderRegistry.getPasses(shader);
            for (PostPass pass : passes) {
                if (pass.getEffect().getUniform("NightVisionEnabled") != null) {
                    pass.getEffect().safeGetUniform("NightVisionEnabled").set(1.0f);
                    pass.getEffect().safeGetUniform("Brightness").set(brightness);
                    pass.getEffect().safeGetUniform("RedValue").set(redTint);
                    pass.getEffect().safeGetUniform("GreenValue").set(greenTint);
                    pass.getEffect().safeGetUniform("BlueValue").set(blueTint);
                }
            }
        };
    }

    public static Consumer<PostChain> greenDigitalVision(float intensity) {
        return shader -> {
            List<PostPass> passes = ShaderRegistry.getPasses(shader);
            for (PostPass pass : passes) {
                pass.getEffect().safeGetUniform("Brightness").set(intensity);
                pass.getEffect().safeGetUniform("NoiseAmplification").set(intensity);
            }
        };
    }

    public static Consumer<PostChain> whitePhosphorVision(float intensity) {
        return shader -> {
            List<PostPass> passes = ShaderRegistry.getPasses(shader);
            for (PostPass pass : passes) {
                pass.getEffect().safeGetUniform("Brightness").set(intensity);
                pass.getEffect().safeGetUniform("NoiseAmplification").set(intensity);
            }
        };
    }

    public static Consumer<PostChain> thermalVision(float intensity) {
        return shader -> {
            List<PostPass> passes = ShaderRegistry.getPasses(shader);
            for (PostPass pass : passes) {
                pass.getEffect().safeGetUniform("Brightness").set(intensity);
                pass.getEffect().safeGetUniform("NoiseAmplification").set(intensity);
            }
        };
    }

    public static Consumer<PostChain> greenNightVision(float intensity) {
        return nightVision(0.6f * intensity, 0.2f, 1.0f, 0.2f);
    }

    public static Consumer<PostChain> blueNightVision(float intensity) {
        return nightVision(0.5f * intensity, 0.2f, 0.5f, 1.0f);
    }

    public static Consumer<PostChain> whiteNightVision(float intensity) {
        return nightVision(0.8f * intensity, 0.9f, 0.9f, 0.9f);
    }
} 