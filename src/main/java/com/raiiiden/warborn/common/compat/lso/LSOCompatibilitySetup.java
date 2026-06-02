package com.raiiiden.warborn.common.compat.lso;

import com.raiiiden.warborn.WARBORN;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Setup handler for LSO (Legendary Survival Overhaul) compatibility
 * This class ensures LSO reflection is initialized during mod setup
 */
@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LSOCompatibilitySetup {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Initializing LSO compatibility...");
            LSOCompatibilityHelper.initialize(event);

            if (LSOCompatibilityHelper.isInitialized()) {
                LOGGER.info("LSO compatibility ready - Propital will now heal body parts!");
            }
        });
    }
}