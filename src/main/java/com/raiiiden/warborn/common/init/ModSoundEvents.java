package com.raiiiden.warborn.common.init;

import com.raiiiden.warborn.WARBORN;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, WARBORN.MODID);

    public static final RegistryObject<SoundEvent> WARBORN_ARMOR_EQUIP = registerSoundEvent("item.armor.warborn_equip");
    public static final RegistryObject<SoundEvent> WARBORN_ARMOR_BREAK = registerSoundEvent("item.armor.warborn_break");
    public static final RegistryObject<SoundEvent> WARBORN_NVG_TOGGLE = registerSoundEvent("item.armor.warborn_nvg_toggle");
    public static final RegistryObject<SoundEvent> WARBORN_PLATE_INSERT = registerSoundEvent("item.armor.warborn_plate_insert");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> {
            ResourceLocation location = new ResourceLocation(WARBORN.MODID, name);
            return SoundEvent.createVariableRangeEvent(location);
        });
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}