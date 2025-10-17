package com.raiiiden.warborn.client.compat.autohud;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WarbornAutoHudCompat {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (!ModList.get().isLoaded("autohud")) {
            LOGGER.info("AutoHUD not found — skipping compat.");
            return;
        }

        try {
            Class<?> autoHudClass = Class.forName("mod.crend.autohud.AutoHud");
            Class<?> apiClass = Class.forName("mod.crend.autohud.api.AutoHudApi");

            Object apiInstance = java.lang.reflect.Proxy.newProxyInstance(
                    WarbornAutoHudCompat.class.getClassLoader(),
                    new Class[]{apiClass},
                    (proxy, method, args) -> {
                        switch (method.getName()) {
                            case "modId":
                                return "warborn";
                            case "tickState":
                                LocalPlayer player = (LocalPlayer) args[0];
                                if (player != null) {
                                    player.getItemBySlot(EquipmentSlot.CHEST)
                                            .getCapability(PlateHolderProvider.CAP)
                                            .ifPresent(cap -> {
                                                if ((cap.hasFrontPlate() && cap.getFrontPlate().getCurrentDurability() < cap.getFrontPlate().getMaxDurability()) ||
                                                        (cap.hasBackPlate() && cap.getBackPlate().getCurrentDurability() < cap.getBackPlate().getMaxDurability())) {
                                                    try {
                                                        Class<?> components = Class.forName("mod.crend.autohud.component.Components$Health");
                                                        components.getMethod("reveal").invoke(null);
                                                    } catch (Exception ignored) {}
                                                }
                                            });
                                }
                                return null;
                            case "init":
                                return null;
                            default:
                                return null;
                        }
                    }
            );

            var addApi = autoHudClass.getMethod("addApi", apiClass);
            addApi.invoke(null, apiInstance);
            LOGGER.info("AutoHUD compat initialized successfully.");
        } catch (Exception e) {
            LOGGER.error("Failed to initialize AutoHUD compat", e);
        }
    }
}