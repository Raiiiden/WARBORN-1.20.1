package com.raiiiden.warborn.client.event;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.renderer.PhantomPlateRenderManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles client-side ticking for phantom plate rendering
 */
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PhantomPlateRenderEvent {

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        PhantomPlateRenderManager.getInstance().tick();
    }
}