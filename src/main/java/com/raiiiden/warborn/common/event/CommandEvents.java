package com.raiiiden.warborn.common.event;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.shader.ShaderCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles registration of all mod commands
 */
@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandEvents {
    
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ShaderCommand.register(event.getDispatcher());
    }
} 