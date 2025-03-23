package com.raiiiden.warborn.client;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "warborn", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeybindings {

    public static final KeyMapping OPEN_BACKPACK = new KeyMapping(
            "key.warborn.open_backpack",
            GLFW.GLFW_KEY_B,
            "key.categories.inventory"
    );

    public static final KeyMapping TOGGLE_NIGHT_VISION = new KeyMapping(
            "key.warborn.toggle_night_vision",
            GLFW.GLFW_KEY_N,
            "key.categories.inventory"
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_BACKPACK);
        event.register(TOGGLE_NIGHT_VISION);
    }
}
