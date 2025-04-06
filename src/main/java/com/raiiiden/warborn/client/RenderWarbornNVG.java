package com.raiiiden.warborn.client;

import com.raiiiden.warborn.common.util.HelmetVisionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.List;

@Mod.EventBusSubscriber(modid = "warborn", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class RenderWarbornNVG {

    private static PostChain nvgShader = null;
    private static int lastWidth = -1;
    private static int lastHeight = -1;

    @SubscribeEvent
    public static void renderNVG(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.level == null) return;

        ItemStack helmet = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
        boolean enabled = HelmetVisionHandler.isAllowedHelmet(helmet) && helmet.getOrCreateTag().getBoolean("NvgCheck");

        if (!enabled) return;

        try {
            if (nvgShader == null) {
                nvgShader = new PostChain(mc.textureManager, mc.getResourceManager(),
                        mc.getMainRenderTarget(), new ResourceLocation("minecraft", "shaders/post/night-vision.json"));
                lastWidth = mc.getWindow().getWidth();
                lastHeight = mc.getWindow().getHeight();
                nvgShader.resize(lastWidth, lastHeight);
            }

            int width = mc.getWindow().getWidth();
            int height = mc.getWindow().getHeight();
            if (width != lastWidth || height != lastHeight) {
                lastWidth = width;
                lastHeight = height;
                nvgShader.resize(width, height);
            }

            for (PostPass pass : getPasses(nvgShader)) {
                if (pass.getEffect().getUniform("NightVisionEnabled") != null) {
                    pass.getEffect().safeGetUniform("NightVisionEnabled").set(1.0f);
                    pass.getEffect().safeGetUniform("Brightness").set(0.6f);
                    pass.getEffect().safeGetUniform("RedValue").set(0.2f);
                    pass.getEffect().safeGetUniform("GreenValue").set(1.0f);
                    pass.getEffect().safeGetUniform("BlueValue").set(0.2f);
                }
            }

            nvgShader.process(mc.getFrameTime());
            mc.getMainRenderTarget().bindWrite(false);

        } catch (Exception e) {
            System.err.println("can't process NVG shader:");
            e.printStackTrace();
        }
    }

    private static List<PostPass> getPasses(PostChain chain) {
        try {
            Field field = PostChain.class.getDeclaredField("passes");
            field.setAccessible(true);
            return (List<PostPass>) field.get(chain);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
