package com.raiiiden.warborn.common.effect;

import com.raiiiden.warborn.common.compat.lso.LSOCompatibilityHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

// Propital stimulant: heals 1 HP every 6 seconds, and under LSO heals all 8 body parts plus painkiller and headache prevention.
public class PropitalEffect extends MobEffect {

    public PropitalEffect() {
        super(MobEffectCategory.BENEFICIAL, 0x00FF88);
    }

    @Override
    public void applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (!(entity instanceof Player player)) {
            return;
        }

        if (player.getHealth() < player.getMaxHealth()) {
            player.heal(1.0f);
        }

        if (LSOCompatibilityHelper.isInitialized()) {
            LSOCompatibilityHelper.applyPainkillerEffect(player);
            LSOCompatibilityHelper.applyPropitalHealing(player);
            LSOCompatibilityHelper.cancelHeadTrauma(player);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % 120 == 0;
    }
}