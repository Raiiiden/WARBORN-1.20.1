package com.raiiiden.warborn;

import com.raiiiden.warborn.client.WarbornClientEventSubscriber;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib.GeckoLib;

@Mod(WARBORN.MODID)
public class WARBORN {
  public static final String MODID = "warborn";

  public WARBORN() {

    var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    // Register mod items and creative tabs
    ModRegistry.ITEMS.register(modEventBus);
    ModRegistry.CREATIVE_MODE_TABS.register(modEventBus);

    MinecraftForge.EVENT_BUS.register(this);
  }
}
