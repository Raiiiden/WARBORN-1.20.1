package com.raiiiden.warborn.client;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.icon.WarbornIconAtlas;
import com.raiiiden.warborn.client.util.ArmorLayerCoverage;
import com.raiiiden.warborn.client.screen.BackpackScreen;
import com.raiiiden.warborn.client.screen.WorkstationScreen;
import com.raiiiden.warborn.common.init.MenuTypeInit;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WARBORNClient {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            if (MenuTypeInit.BACKPACK_MENU.isPresent()) {
                MenuScreens.register(MenuTypeInit.BACKPACK_MENU.get(), BackpackScreen::new);
            }
            MenuScreens.register(MenuTypeInit.WORKSTATION_MENU.get(), WorkstationScreen::new);
        });
    }

    // Baked gear icons hold onto models and textures from the previous reload, so drop them all.
    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener((ResourceManagerReloadListener) (@NotNull ResourceManager manager) -> {
            WarbornIconAtlas.invalidate();
            ArmorLayerCoverage.clear();
        });
    }
}
