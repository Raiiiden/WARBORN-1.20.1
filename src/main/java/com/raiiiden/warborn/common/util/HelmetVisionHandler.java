package com.raiiiden.warborn.common.util;

import com.raiiiden.warborn.client.shader.ShaderRegistry;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * Handles helmet vision effects based on item tags for now
 */
public class HelmetVisionHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String NVG_SHADER_ID = "warborn_nvg";
    private static final String SIMPLE_NVG_SHADER_ID = "warborn_simple_nvg";
    private static final String THERMAL_SHADER_ID = "tvg";
    private static final String DIGITAL_SHADER_ID = "warborn_dvg";

    private static final boolean DEBUG_MODE = false;

    /**
     * Checks if a helmet is allowed to use vision modes
     */
    public static boolean isAllowedHelmet(ItemStack helmet) {
        return WarbornArmorItem.hasVisionCapability(helmet);
    }

    /**
     * Gets the current active vision type for a helmet
     *
     * @return The vision type tag, or empty string if none active
     */
    public static String getActiveVisionType(ItemStack helmet) {
        if (!isAllowedHelmet(helmet)) return "";

        CompoundTag tag = helmet.getTag();
        if (tag != null && tag.contains("ActiveVision")) {
            return tag.getString("ActiveVision");
        }
        return "";
    }

    /**
     * Sets the active vision type for a helmet
     */
    public static void setActiveVisionType(ItemStack helmet, String visionType) {
        if (!isAllowedHelmet(helmet)) return;

        CompoundTag tag = helmet.getOrCreateTag();
        if (visionType.isEmpty()) {
            tag.remove("ActiveVision");
        } else {
            tag.putString("ActiveVision", visionType);
        }
    }

    /**
     * Checks if a helmet has a specific vision type capability
     */
    public static boolean hasVisionType(ItemStack helmet, String visionType) {
        if (!isAllowedHelmet(helmet)) {
            if (DEBUG_MODE) {
                LOGGER.info("Helmet {} doesn't have basic vision capability", helmet.getDisplayName().getString());
            }
            return false;
        }

        CompoundTag tag = helmet.getTag();
        if (tag != null && tag.contains(visionType)) {
            if (DEBUG_MODE) {
                LOGGER.info("Found {} capability in NBT for {}", visionType, helmet.getDisplayName().getString());
            }
            return true;
        }

        if (helmet.isEmpty() || !(helmet.getItem() instanceof ArmorItem)) return false;

        ResourceLocation tagId = new ResourceLocation("warborn", "has_" + visionType);
        boolean hasTag = helmet.is(TagKey.create(Registries.ITEM, tagId));

        if (DEBUG_MODE && hasTag) {
            LOGGER.info("Found {} capability in item tag for {}", visionType, helmet.getDisplayName().getString());
        }

        return hasTag;
    }

    /**
     * Toggles vision mode when the key is pressed
     *
     * @return true if vision was toggled successfully
     */
    public static boolean toggleVision(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        LOGGER.info("TOGGLE VISION called with helmet: {}", helmet);

        if (DEBUG_MODE) {
            LOGGER.info("==== VISION TOGGLE DEBUG ====");
            LOGGER.info("Helmet: {}", helmet);
            LOGGER.info("Is allowed helmet: {}", isAllowedHelmet(helmet));
            if (!helmet.isEmpty()) {
                LOGGER.info("Item tags for {}: {}", helmet.getDisplayName().getString(), helmet.getTags().toString());
                LOGGER.info("  Has NVG: {}", hasVisionType(helmet, WarbornArmorItem.TAG_NVG));
                LOGGER.info("  Has Simple NVG: {}", hasVisionType(helmet, WarbornArmorItem.TAG_SIMPLE_NVG));
                LOGGER.info("  Has Thermal: {}", hasVisionType(helmet, WarbornArmorItem.TAG_THERMAL));
                LOGGER.info("  Has Digital: {}", hasVisionType(helmet, WarbornArmorItem.TAG_DIGITAL));

                CompoundTag tag = helmet.getTag();
                if (tag != null) {
                    LOGGER.info("  NBT: {}", tag.toString());
                }
            }
        }

        if (!isAllowedHelmet(helmet)) {
            LOGGER.debug("No suitable helmet equipped");
            return false;
        }

        String currentType = getActiveVisionType(helmet);

        if (DEBUG_MODE) {
            LOGGER.info("Current active vision type: {}", currentType.isEmpty() ? "NONE" : currentType);
        }

        if (!currentType.isEmpty()) {
            LOGGER.info("Disabling vision mode: {}", currentType);
            disableVisionShader(currentType);
            setActiveVisionType(helmet, "");
            return true;
        }

        String primaryVisionType = getPrimaryVisionType(helmet);

        if (DEBUG_MODE) {
            LOGGER.info("Primary vision type for helmet: {}", primaryVisionType.isEmpty() ? "NONE" : primaryVisionType);
        }

        if (!primaryVisionType.isEmpty()) {
            LOGGER.info("*** ACTIVATING VISION: {} ***", primaryVisionType);
            enableVisionShader(primaryVisionType);
            setActiveVisionType(helmet, primaryVisionType);
            LOGGER.info("Enabled vision mode: {}", primaryVisionType);

            String shaderId = visionTypeToShaderId(primaryVisionType);
            boolean enabled = ShaderRegistry.getInstance().isShaderForceEnabled(shaderId);
            LOGGER.info("Shader {} force enabled: {}", shaderId, enabled);

            return true;
        }

        LOGGER.debug("No vision types available on helmet");
        return false;
    }

    /**
     * Converts a vision type to its corresponding shader ID
     */
    private static String visionTypeToShaderId(String visionType) {
        return switch (visionType) {
            case WarbornArmorItem.TAG_NVG -> NVG_SHADER_ID;
            case WarbornArmorItem.TAG_SIMPLE_NVG -> SIMPLE_NVG_SHADER_ID;
            case WarbornArmorItem.TAG_THERMAL -> THERMAL_SHADER_ID;
            case WarbornArmorItem.TAG_DIGITAL -> DIGITAL_SHADER_ID;
            default -> "";
        };
    }

    /**
     * Determines the primary vision type for a helmet
     * Each helmet should have exactly one vision type
     */
    private static String getPrimaryVisionType(ItemStack helmet) {
        if (hasVisionType(helmet, WarbornArmorItem.TAG_DIGITAL)) {
            LOGGER.info("Helmet has digital vision capability");
            return WarbornArmorItem.TAG_DIGITAL;
        }

        if (hasVisionType(helmet, WarbornArmorItem.TAG_THERMAL)) {
            LOGGER.info("Helmet has thermal vision capability");
            return WarbornArmorItem.TAG_THERMAL;
        }

        if (hasVisionType(helmet, WarbornArmorItem.TAG_NVG)) {
            LOGGER.info("Helmet has standard NVG capability");
            return WarbornArmorItem.TAG_NVG;
        }

        if (hasVisionType(helmet, WarbornArmorItem.TAG_SIMPLE_NVG)) {
            LOGGER.info("Helmet has simple NVG capability");
            return WarbornArmorItem.TAG_SIMPLE_NVG;
        }

        LOGGER.warn("Helmet has no vision capabilities");
        return "";
    }

    /**
     * Enables the correct shader for a vision type
     */
    private static void enableVisionShader(String visionType) {
        disableAllShaders();

        if (DEBUG_MODE) {
            LOGGER.info("Enabling shader for vision type: {}", visionType);
        }

        String shaderId = "";
        switch (visionType) {
            case WarbornArmorItem.TAG_NVG:
                shaderId = NVG_SHADER_ID;
                if (DEBUG_MODE) LOGGER.info("Activating NVG shader: {}", shaderId);
                break;
            case WarbornArmorItem.TAG_SIMPLE_NVG:
                shaderId = SIMPLE_NVG_SHADER_ID;
                if (DEBUG_MODE) LOGGER.info("Activating Simple NVG shader: {}", shaderId);
                break;
            case WarbornArmorItem.TAG_THERMAL:
                shaderId = THERMAL_SHADER_ID;
                if (DEBUG_MODE) LOGGER.info("Activating Thermal shader: {}", shaderId);
                break;
            case WarbornArmorItem.TAG_DIGITAL:
                shaderId = DIGITAL_SHADER_ID;
                if (DEBUG_MODE) LOGGER.info("Activating Digital shader: {}", shaderId);
                break;
            default:
                LOGGER.warn("Unknown vision type: {}", visionType);
                return;
        }

        if (DEBUG_MODE) {
            LOGGER.info("Setting shader {} to be force enabled", shaderId);
        }
        boolean result = ShaderRegistry.getInstance().setShaderEnabled(shaderId, true);
        if (DEBUG_MODE) {
            LOGGER.info("Shader enable result: {}", result);
        }

        Set<String> allShaders = ShaderRegistry.getInstance().getRegisteredShaderIds();
        if (allShaders.contains(shaderId)) {
            boolean shaderActive = ShaderRegistry.getInstance().isShaderForceEnabled(shaderId);
            LOGGER.info("After enabling: shader {} is force enabled: {}", shaderId, shaderActive);
        } else {
            LOGGER.error("SHADER NOT FOUND: {} is not registered!", shaderId);
        }

        if (DEBUG_MODE) {
            LOGGER.info("Shader activation complete");
        }
    }

    /**
     * Disables the shader for a specific vision type
     */
    private static void disableVisionShader(String visionType) {
        switch (visionType) {
            case WarbornArmorItem.TAG_NVG:
                ShaderRegistry.getInstance().setShaderEnabled(NVG_SHADER_ID, false);
                break;
            case WarbornArmorItem.TAG_SIMPLE_NVG:
                ShaderRegistry.getInstance().setShaderEnabled(SIMPLE_NVG_SHADER_ID, false);
                break;
            case WarbornArmorItem.TAG_THERMAL:
                ShaderRegistry.getInstance().setShaderEnabled(THERMAL_SHADER_ID, false);
                break;
            case WarbornArmorItem.TAG_DIGITAL:
                ShaderRegistry.getInstance().setShaderEnabled(DIGITAL_SHADER_ID, false);
                break;
        }
    }

    /**
     * Disables all vision shaders
     */
    private static void disableAllShaders() {
        ShaderRegistry.getInstance().setShaderEnabled(NVG_SHADER_ID, false);
        ShaderRegistry.getInstance().setShaderEnabled(SIMPLE_NVG_SHADER_ID, false);
        ShaderRegistry.getInstance().setShaderEnabled(THERMAL_SHADER_ID, false);
        ShaderRegistry.getInstance().setShaderEnabled(DIGITAL_SHADER_ID, false);
    }

    /**
     * For automatic shader activation checks
     */
    public static boolean isVisionActive(Minecraft mc, String visionType) {
        if (mc.player == null) return false;

        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        if (!isAllowedHelmet(helmet)) return false;

        String activeType = getActiveVisionType(helmet);
        boolean isActive = activeType.equals(visionType);

        if (DEBUG_MODE && isActive) {
            LOGGER.info("Vision active check: {} is active for helmet {}",
                    visionType, helmet.getDisplayName().getString());
        }

        return isActive;
    }

    /**
     * Debug method to provide information about the vision system
     *
     * @return A string with debug info
     */
    public static String getDebugInfo(Player player) {
        StringBuilder info = new StringBuilder();
        info.append("=== Vision System Debug ===\n");

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        info.append("Current helmet: ").append(helmet.isEmpty() ? "NONE" : helmet.getDisplayName().getString()).append("\n");

        if (!helmet.isEmpty()) {
            info.append("Is allowed helmet: ").append(isAllowedHelmet(helmet)).append("\n");
            info.append("Has NVG: ").append(hasVisionType(helmet, WarbornArmorItem.TAG_NVG)).append("\n");
            info.append("Has Simple NVG: ").append(hasVisionType(helmet, WarbornArmorItem.TAG_SIMPLE_NVG)).append("\n");
            info.append("Has Thermal: ").append(hasVisionType(helmet, WarbornArmorItem.TAG_THERMAL)).append("\n");
            info.append("Has Digital: ").append(hasVisionType(helmet, WarbornArmorItem.TAG_DIGITAL)).append("\n");

            String activeType = getActiveVisionType(helmet);
            info.append("Active vision: ").append(activeType.isEmpty() ? "NONE" : activeType).append("\n");
        }

        // Check shader registry
        info.append("\nRegistered shaders:\n");
        Set<String> shaderIds = com.raiiiden.warborn.client.shader.ShaderRegistry.getInstance().getRegisteredShaderIds();
        for (String id : shaderIds) {
            boolean active = com.raiiiden.warborn.client.shader.ShaderRegistry.getInstance().isShaderActive(id);
            info.append("- ").append(id).append(": ").append(active ? "ACTIVE" : "inactive").append("\n");
        }

        return info.toString();
    }
}
