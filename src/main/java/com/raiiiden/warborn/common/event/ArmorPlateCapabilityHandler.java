package com.raiiiden.warborn.common.event;

import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.raiiiden.warborn.WARBORN.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class ArmorPlateCapabilityHandler {
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<ItemStack> event) {
        ItemStack stack = event.getObject();
        if (stack.getItem() instanceof ArmorItem armor && armor.getType() == ArmorItem.Type.CHESTPLATE) {
            event.addCapability(new ResourceLocation(MODID, "plate_holder"), new PlateHolderProvider(stack));
        }
    }
}
