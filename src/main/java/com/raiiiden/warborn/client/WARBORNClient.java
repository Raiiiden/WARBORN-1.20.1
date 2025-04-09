package com.raiiiden.warborn.client;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.screen.BackpackScreen;
import com.raiiiden.warborn.common.init.MenuTypeInit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WARBORNClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            if (MenuTypeInit.BACKPACK_MENU.isPresent()) {
                MenuScreens.register(MenuTypeInit.BACKPACK_MENU.get(), BackpackScreen::new);
            }
        });
    }
}