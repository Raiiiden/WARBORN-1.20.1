package com.raiiiden.warborn.client.compat.autohud;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import mod.crend.autohud.api.AutoHudApi;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class WarbornAutoHudCompat implements AutoHudApi {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        if (ModList.get().isLoaded("autohud")) {
            try {
                Class<?> autoHudClass = Class.forName("mod.crend.autohud.AutoHud");
                var method = autoHudClass.getMethod("addApi", AutoHudApi.class);
                method.invoke(null, new WarbornAutoHudCompat());
            } catch (Exception e) {
                // Silent fail
            }
        }
    }

    @Override
    public String modId() {
        return "warborn";
    }

    @Override
    public void tickState(LocalPlayer player) {
        if (player == null) return;

        player.getItemBySlot(EquipmentSlot.CHEST).getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            if ((cap.hasFrontPlate() && cap.getFrontPlate().getCurrentDurability() < cap.getFrontPlate().getMaxDurability()) ||
                    (cap.hasBackPlate() && cap.getBackPlate().getCurrentDurability() < cap.getBackPlate().getMaxDurability())) {
                mod.crend.autohud.component.Components.Health.reveal();
            }
        });
    }

    @Override
    public void init() {
    }
}