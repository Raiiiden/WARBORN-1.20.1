package com.raiiiden.warborn;

import com.raiiiden.warborn.common.config.WarbornCommonConfig;
import com.raiiiden.warborn.common.config.WarbornArmorConfig;
import com.raiiiden.warborn.common.init.MenuTypeInit;
import com.raiiiden.warborn.common.init.ModRegistry;
import com.raiiiden.warborn.common.network.ModNetworking;
import com.raiiiden.warborn.common.init.ModSoundEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(WARBORN.MODID)
public class WARBORN {
  public static final String MODID = "warborn";
  public static final Logger LOGGER = LogManager.getLogger();

  public WARBORN() {
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    ModRegistry.ITEMS.register(modEventBus);
    ModRegistry.CREATIVE_MODE_TABS.register(modEventBus);
    ModSoundEvents.SOUND_EVENTS.register(modEventBus);

    modEventBus.addListener(this::setup);
    MenuTypeInit.register(modEventBus);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WarbornCommonConfig.SPEC);
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WarbornArmorConfig.SPEC, "warborn-armor.toml");

    MinecraftForge.EVENT_BUS.register(this);
  }

  private void setup(final FMLCommonSetupEvent event) {
    ModNetworking.registerPackets();
  }
}
