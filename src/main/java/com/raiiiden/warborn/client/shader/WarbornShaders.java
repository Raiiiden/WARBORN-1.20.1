package com.raiiiden.warborn.client.shader;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.WBArmorItem;
import com.raiiiden.warborn.common.util.HelmetVisionHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class WarbornShaders {
    private static final String NVG_SHADER_ID = "nvg";
    private static final String SIMPLE_NVG_SHADER_ID = "snvg";
    private static final String DVG_SHADER_ID = "dvg";
    private static final String TVG_SHADER_ID = "tvg";
    private static final String GDVG_SHADER_ID = "dnvg";
    private static final Logger LOGGER = LogManager.getLogger();

    private static final boolean DEBUG = false;

    private static boolean isNvgEnabled(Minecraft mc) {
        boolean active = HelmetVisionHandler.isVisionActive(mc, WBArmorItem.TAG_NVG);
        if (active && DEBUG) {
            LOGGER.info("NVG shader activation requested");
        }
        return active;
    }

    private static boolean isSimpleNvgEnabled(Minecraft mc) {
        boolean active = HelmetVisionHandler.isVisionActive(mc, WBArmorItem.TAG_SIMPLE_NVG);
        if (active && DEBUG) {
            LOGGER.info("Simple NVG shader activation requested");
        }
        return active;
    }

    private static boolean isThermalEnabled(Minecraft mc) {
        boolean active = HelmetVisionHandler.isVisionActive(mc, WBArmorItem.TAG_THERMAL);
        if (active && DEBUG) {
            LOGGER.info("Thermal shader activation requested");
        }
        return active;
    }

    private static boolean isDigitalEnabled(Minecraft mc) {
        boolean active = HelmetVisionHandler.isVisionActive(mc, WBArmorItem.TAG_DIGITAL);
        if (active && DEBUG) {
            LOGGER.info("Digital shader activation requested");
        }
        return active;
    }

    private static boolean isNever(Minecraft minecraft) {
        return false;
    }

    @SubscribeEvent
    public static void renderNVG(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        ShaderRegistry.getInstance().processShaders();
    }

    @Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientInit {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                LOGGER.info("Registering NVG shader");

                LOGGER.info("Shader ID constants:");
                LOGGER.info("  NVG_SHADER_ID: {}", NVG_SHADER_ID);
                LOGGER.info("  SIMPLE_NVG_SHADER_ID: {}", SIMPLE_NVG_SHADER_ID);
                LOGGER.info("  DVG_SHADER_ID: {}", DVG_SHADER_ID);
                LOGGER.info("  TVG_SHADER_ID: {}", TVG_SHADER_ID);
                LOGGER.info("  GDVG_SHADER_ID: {}", GDVG_SHADER_ID);

                boolean nvgRegistered = ShaderRegistry.getInstance().registerShader(
                        NVG_SHADER_ID,
                        ShaderPresets.NIGHT_VISION,
                        WarbornShaders::isNvgEnabled,
                        ShaderPresets.greenNightVision(1.0f)
                );
                LOGGER.info("NVG shader registered: {}", nvgRegistered);

                boolean simpleNvgRegistered = ShaderRegistry.getInstance().registerShader(
                        SIMPLE_NVG_SHADER_ID,
                        ShaderPresets.SIMPLE_NIGHT_VISION,
                        WarbornShaders::isSimpleNvgEnabled,
                        postChain -> {
                        }
                );
                LOGGER.info("Simple NVG shader registered: {}", simpleNvgRegistered);

                boolean dvgRegistered = ShaderRegistry.getInstance().registerShader(
                        DVG_SHADER_ID,
                        ShaderPresets.DIGITAL_WHITE_VISION,
                        WarbornShaders::isDigitalEnabled,
                        ShaderPresets.whitePhosphorVision(0.7f)
                );
                LOGGER.info("Digital shader registered: {}", dvgRegistered);

                boolean dvgGRegistered = ShaderRegistry.getInstance().registerShader(
                        GDVG_SHADER_ID,
                        ShaderPresets.DIGITAL_GREEN_VISION,
                        WarbornShaders::isNever,
                        ShaderPresets.greenDigitalVision(0.7f)
                );
                LOGGER.info("Digital green shader registered: {}", dvgGRegistered);

                boolean tvgRegistered = ShaderRegistry.getInstance().registerShader(
                        TVG_SHADER_ID,
                        ShaderPresets.THERMAL_VISION,
                        WarbornShaders::isThermalEnabled,
                        ShaderPresets.thermalVision(0.7f)
                );
                LOGGER.info("Thermal shader registered: {}", tvgRegistered);

                Set<String> registeredShaders = ShaderRegistry.getInstance().getRegisteredShaderIds();
                LOGGER.info("Total registered shaders: {}", registeredShaders.size());
                LOGGER.info("Registered shaders: {}", registeredShaders);
            });
        }
    }
}