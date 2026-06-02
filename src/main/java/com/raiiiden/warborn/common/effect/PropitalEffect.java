package com.raiiiden.warborn.common.effect;

import com.raiiiden.warborn.common.compat.lso.LSOCompatibilityHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Propital Effect - mimics Escape from Tarkov's Propital stimulant
 *
 * In Tarkov, Propital provides health regeneration and pain removal.
 *
 * This implementation:
 * - LSO: Heals all 8 body parts by 1 HP every 6 seconds, applies Painkiller effect, prevents Headache
 * - Vanilla: Heals 1 HP every 6 seconds
 */
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