package com.raiiiden.warborn.common.effect;

import com.raiiiden.warborn.common.config.WarbornCommonConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public class CureEffect extends MobEffect {

    public CureEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xFFFFFF); // white-ish
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {

        // Load configured effects to cancel
        List<? extends String> blocked = WarbornCommonConfig.CURE_CANCELED_EFFECTS.get();

        if (blocked == null || blocked.isEmpty()) {
            return;
        }

        for (String id : blocked) {

            ResourceLocation rl = ResourceLocation.tryParse(id);
            if (rl == null) continue;

            MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(rl);
            if (effect == null) continue;

            // If entity has the effect → remove it
            if (entity.hasEffect(effect)) {
                entity.removeEffect(effect);
            }
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // Run every tick to guarantee removal
        return true;
    }
}
