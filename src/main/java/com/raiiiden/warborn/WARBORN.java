package com.raiiiden.warborn;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;

@Mod(WARBORN.MODID)
public class WARBORN {
  public static final String MODID = "warborn";

  public WARBORN() {
    GeckoLib.initialize(); // Ensure Geckolib is initialized

    IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    ModRegistry.ITEMS.register(eventBus);
    ModRegistry.CREATIVE_MODE_TABS.register(eventBus); // Register the creative tab
  }
}
