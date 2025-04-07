//package com.raiiiden.warborn.client.renderer;
//
//import com.raiiiden.warborn.WARBORN;
//import com.raiiiden.warborn.client.shader.ShaderRegistry;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.renderer.entity.LivingEntityRenderer;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.monster.Monster;
//import net.minecraft.world.entity.player.Player;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.EntityRenderersEvent;
//import net.minecraftforge.client.event.RenderLivingEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Handles emissive rendering for entities during thermal vision
// */
//@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
//public class ThermalEntityRenderer {
//    private static final Logger LOGGER = LogManager.getLogger();
//    private static final String THERMAL_SHADER_ID = "warborn_tvg";
//
//    // Store original brightness values to restore them later
//    private static final Map<EntityType<?>, Float> originalBrightness = new HashMap<>();
//    private static final Map<EntityType<?>, Integer> originalOverlay = new HashMap<>();
//
//    // For storing if thermal is currently active
//    private static boolean thermalActive = false;
//
//    /**
//     * Pre-render event for living entities
//     */
//    @SubscribeEvent
//    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
//        if (!isThermalActive()) return;
//
//        LivingEntity entity = event.getEntity();
//
//        // Calculate emissive brightness based on entity type/state
//        float emissiveBrightness = calculateEmissiveBrightness(entity);
//
//        // Store original values if not already stored
//        if (!originalBrightness.containsKey(entity.getType())) {
//            originalBrightness.put(entity.getType(), entity.getBrightness());
//        }
//
//        // Override entity brightness for thermal vision
//        // This makes the entity emit light in thermal vision mode
//        entity.setGlowingTag(true); // Make entity outline visible
//
//        // Use the overlay color for heat visualization
//        int overlayColor = getHeatColor(entity);
//        event.getPoseStack().pushPose();
//    }
//
//    /**
//     * Post-render event for living entities
//     */
//    @SubscribeEvent
//    public static void onRenderLivingPost(RenderLivingEvent.Post<?, ?> event) {
//        if (!isThermalActive()) return;
//
//        LivingEntity entity = event.getEntity();
//
//        // Reset glowing state
//        entity.setGlowingTag(false);
//
//        event.getPoseStack().popPose();
//    }
//
//    /**
//     * Check if thermal vision is currently active
//     */
//    private static boolean isThermalActive() {
//        // Cache the result to avoid checking every frame for every entity
//        if (Minecraft.getInstance().getDeltaFrameTime() % 20 == 0) {
//            thermalActive = ShaderRegistry.getInstance().isShaderActive(THERMAL_SHADER_ID);
//        }
//        return thermalActive;
//    }
//
//    /**
//     * Calculate how bright/emissive an entity should appear in thermal vision
//     */
//    private static float calculateEmissiveBrightness(LivingEntity entity) {
//        float baseEmission = 0.6f; // Default heat level
//
//        // Monsters are hotter
//        if (entity instanceof Monster) {
//            baseEmission = 0.8f;
//        }
//
//        // Players have distinctive heat signature
//        if (entity instanceof Player) {
//            baseEmission = 0.75f;
//
//            // Sneaking players are cooler/harder to see
//            if (((Player) entity).isCrouching()) {
//                baseEmission *= 0.7f;
//            }
//        }
//
//        // Injured entities are hotter
//        float healthPercent = entity.getHealth() / entity.getMaxHealth();
//        if (healthPercent < 0.5f) {
//            baseEmission += (1 - healthPercent) * 0.3f;
//        }
//
//        // Moving entities are hotter
//        double movementSpeed = entity.getDeltaMovement().length();
//        baseEmission += Math.min(0.3f, (float)movementSpeed * 0.5f);
//
//        // Entities on fire are very hot
//        if (entity.isOnFire()) {
//            baseEmission = 1.0f;
//        }
//
//        return Math.min(1.0f, baseEmission);
//    }
//
//    /**
//     * Get heat color for the entity overlay
//     */
//    private static int getHeatColor(LivingEntity entity) {
//        // Calculate heat level from 0.0 to 1.0
//        float heat = calculateEmissiveBrightness(entity);
//
//        // Convert heat to color
//        int r, g, b;
//
//        if (heat < 0.3f) {
//            // Cold (purple)
//            r = (int)(174 * heat / 0.3f);
//            g = (int)(77 * heat / 0.3f);
//            b = 255;
//        } else if (heat < 0.5f) {
//            // Cool (magenta)
//            float normalizedHeat = (heat - 0.3f) / 0.2f;
//            r = (int)(174 + (179 - 174) * normalizedHeat);
//            g = (int)(77 + (18 - 77) * normalizedHeat);
//            b = (int)(255 + (148 - 255) * normalizedHeat);
//        } else if (heat < 0.7f) {
//            // Medium (red)
//            float normalizedHeat = (heat - 0.5f) / 0.2f;
//            r = (int)(179 + (226 - 179) * normalizedHeat);
//            g = (int)(18 + (72 - 18) * normalizedHeat);
//            b = (int)(148 + (81 - 148) * normalizedHeat);
//        } else if (heat < 0.95f) {
//            // Warm (yellow)
//            float normalizedHeat = (heat - 0.7f) / 0.25f;
//            r = (int)(226 + (250 - 226) * normalizedHeat);
//            g = (int)(72 + (235 - 72) * normalizedHeat);
//            b = (int)(81 + (57 - 81) * normalizedHeat);
//        } else {
//            // Hot (white)
//            float normalizedHeat = (heat - 0.95f) / 0.05f;
//            r = (int)(250 + (255 - 250) * normalizedHeat);
//            g = (int)(235 + (255 - 235) * normalizedHeat);
//            b = (int)(57 + (255 - 57) * normalizedHeat);
//        }
//
//        // Create ARGB color (fully opaque)
//        return (255 << 24) | (r << 16) | (g << 8) | b;
//    }
//}