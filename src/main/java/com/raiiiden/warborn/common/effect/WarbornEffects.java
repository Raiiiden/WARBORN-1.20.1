package com.raiiiden.warborn.common.effect;

import com.raiiiden.warborn.WARBORN;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WarbornEffects {

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, WARBORN.MODID);

    public static final RegistryObject<MobEffect> PROPITAL =
            EFFECTS.register("propital", PropitalEffect::new);
    public static final RegistryObject<MobEffect> CURE =
            EFFECTS.register("cure", CureEffect::new);

    // Add more Tarkov medical effects here
    // public static final RegistryObject<MobEffect> ADRENALINE =
    //         EFFECTS.register("adrenaline", AdrenalineEffect::new);

    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}