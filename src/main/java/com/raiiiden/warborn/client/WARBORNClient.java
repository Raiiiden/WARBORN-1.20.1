package com.raiiiden.warborn.client;

import com.raiiiden.warborn.client.screen.BackpackScreen;
import com.raiiiden.warborn.common.init.MenuTypeInit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = "warborn", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WARBORNClient {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            if (MenuTypeInit.BACKPACK_MENU.isPresent()) {
                MenuScreens.register(MenuTypeInit.BACKPACK_MENU.get(), BackpackScreen::new);
            }
        });
    }
}
