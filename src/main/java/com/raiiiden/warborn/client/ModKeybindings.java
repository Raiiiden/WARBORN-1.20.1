package com.raiiiden.warborn.client;

import com.raiiiden.warborn.WARBORN;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModKeybindings {

    public static final KeyMapping TOGGLE_HELMET_TOP = new KeyMapping(
            "key.warborn.toggle_helmet_top",
            GLFW.GLFW_KEY_H,  // Default key H
            "key.categories.warborn"
    );

    public static final KeyMapping OPEN_BACKPACK = new KeyMapping(
            "key.warborn.open_backpack",
            GLFW.GLFW_KEY_B,
            "key.categories.warborn"
    );

    public static final KeyMapping TOGGLE_SPECIAL_VISION = new KeyMapping(
            "key.warborn.toggle_special_vision",
            GLFW.GLFW_KEY_N,
            "key.categories.warborn"
    );

    public static final KeyMapping REMOVE_PLATE_MENU = new KeyMapping(
            "key.warborn.remove_plate_menu",
            GLFW.GLFW_KEY_U,
            "key.categories.warborn"
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_BACKPACK);
        event.register(TOGGLE_SPECIAL_VISION);
        event.register(TOGGLE_HELMET_TOP);
        //TODO clean up this
//        event.register(REMOVE_PLATE_MENU);
    }
}