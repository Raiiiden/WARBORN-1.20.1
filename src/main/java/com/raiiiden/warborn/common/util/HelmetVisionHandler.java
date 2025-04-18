package com.raiiiden.warborn.common.util;

import com.raiiiden.warborn.client.shader.ClientVisionState;
import com.raiiiden.warborn.client.shader.ShaderRegistry;
import com.raiiiden.warborn.common.item.WarbornArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

public class HelmetVisionHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String NVG_SHADER_ID = "nvg";
    private static final String SIMPLE_NVG_SHADER_ID = "snvg";
    private static final String THERMAL_SHADER_ID = "tvg";
    private static final String DIGITAL_SHADER_ID = "dvg";

    private static final boolean DEBUG_MODE = false;

    public static boolean isAllowedHelmet(ItemStack helmet) {
        return WarbornArmorItem.hasVisionCapability(helmet);
    }

    public static String getShaderIdFromVisionType(String visionType) {
        return switch (visionType) {
            case WarbornArmorItem.TAG_NVG -> "nvg";
            case WarbornArmorItem.TAG_SIMPLE_NVG -> "snvg";
            case WarbornArmorItem.TAG_THERMAL -> "tvg";
            case WarbornArmorItem.TAG_DIGITAL -> "dvg";
            default -> "";
        };
    }

    public static String getActiveVisionType(ItemStack helmet) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return "";
        return ClientVisionState.getActive(player.getUUID());
    }

    public static void setActiveVisionType(ItemStack helmet, String visionType) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;
        ClientVisionState.setActive(player.getUUID(), visionType);
    }

    public static boolean hasVisionType(ItemStack helmet, String visionType) {
        if (!isAllowedHelmet(helmet)) {
            if (DEBUG_MODE) {
                LOGGER.info("Helmet {} doesn't have basic vision capability", helmet.getDisplayName().getString());
            }
            return false;
        }

        if (helmet.getTag() != null && helmet.getTag().contains(visionType)) {
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
                if (helmet.getTag() != null) {
                    LOGGER.info("  NBT: {}", helmet.getTag().toString());
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

    private static String visionTypeToShaderId(String visionType) {
        return switch (visionType) {
            case WarbornArmorItem.TAG_NVG -> NVG_SHADER_ID;
            case WarbornArmorItem.TAG_SIMPLE_NVG -> SIMPLE_NVG_SHADER_ID;
            case WarbornArmorItem.TAG_THERMAL -> THERMAL_SHADER_ID;
            case WarbornArmorItem.TAG_DIGITAL -> DIGITAL_SHADER_ID;
            default -> "";
        };
    }

    private static void enableVisionShader(String visionType) {
        disableAllShaders();

        if (DEBUG_MODE) {
            LOGGER.info("Enabling shader for vision type: {}", visionType);
        }

        String shaderId = visionTypeToShaderId(visionType);
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
    }

    private static void disableVisionShader(String visionType) {
        String shaderId = visionTypeToShaderId(visionType);
        ShaderRegistry.getInstance().setShaderEnabled(shaderId, false);
    }

    private static void disableAllShaders() {
        ShaderRegistry.getInstance().setShaderEnabled(NVG_SHADER_ID, false);
        ShaderRegistry.getInstance().setShaderEnabled(SIMPLE_NVG_SHADER_ID, false);
        ShaderRegistry.getInstance().setShaderEnabled(THERMAL_SHADER_ID, false);
        ShaderRegistry.getInstance().setShaderEnabled(DIGITAL_SHADER_ID, false);
    }

    public static boolean isVisionActive(Minecraft mc, String visionType) {
        if (mc.player == null) return false;
        return ClientVisionState.isActive(mc.player.getUUID(), visionType);
    }

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

        info.append("\nRegistered shaders:\n");
        Set<String> shaderIds = ShaderRegistry.getInstance().getRegisteredShaderIds();
        for (String id : shaderIds) {
            boolean active = ShaderRegistry.getInstance().isShaderActive(id);
            info.append("- ").append(id).append(": ").append(active ? "ACTIVE" : "inactive").append("\n");
        }

        return info.toString();
    }
}