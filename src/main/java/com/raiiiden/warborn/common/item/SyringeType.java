package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.common.effect.WarbornEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public enum SyringeType {

    // Propital, a Tarkov regenerative stimulant: removes pain and contusion, regenerates health with skill bonuses, and causes tremor as a late side effect.
    PROPITAL("propital") {
        @Override
        public void applyEffect(Player player, Level level) {
            // Main effect: Health regeneration for 300 seconds (5 minutes)
            // Heals 1 HP (0.5 hearts) per second = 300 HP total
            player.addEffect(new MobEffectInstance(
                    WarbornEffects.PROPITAL.get(),
                    6000,  // 300 seconds * 20 ticks
                    0,     // Amplifier 0
                    false, // Not ambient
                    true,  // Show particles
                    true   // Show icon
            ));
        }
    },
    CURE("cure") {
        @Override
        public void applyEffect(Player player, Level level) {
            // Apply Cure effect for 5 minutes (300 seconds)
            player.addEffect(new MobEffectInstance(
                    WarbornEffects.CURE.get(),
                    6000,  // 5 minutes
                    0,     // Amplifier
                    false, // Not ambient
                    true,  // Show particles
                    true   // Show icon
            ));
        }
    };


    private final String name;

    SyringeType(String name) {
        this.name = name;
    }

    // Apply the effect when the syringe is used.
    public abstract void applyEffect(Player player, Level level);

    // Check if this syringe can be used; override if needed.
    public boolean canUse(Player player, Level level) {
        return true;
    }

    // Get the success message when used.
    public Component getSuccessMessage() {
        return Component.translatable("message.fracturepoint.syringe." + name + ".used")
                .withStyle(ChatFormatting.GREEN);
    }

    // Get the failure message when it can't be used.
    public Component getFailureMessage() {
        return Component.translatable("message.fracturepoint.syringe." + name + ".cannot_use")
                .withStyle(ChatFormatting.RED);
    }

    public String getName() {
        return name;
    }
}