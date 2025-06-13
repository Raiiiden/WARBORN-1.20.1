package com.raiiiden.warborn.client.event;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.item.ArmorPlateItem;
import com.raiiiden.warborn.common.util.PlateTooltip;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientForgeEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent e) {
        if (ArmorPlateItem.isPlateCompatible(e.getItemStack())) {
            PlateTooltip.addChestplate(e.getToolTip(), e.getItemStack());
        }
    }
}
