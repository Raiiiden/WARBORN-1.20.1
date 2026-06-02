package com.raiiiden.warborn.common.compat.lso;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class LSOCompatibilityHelper {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final String LSO_CAPABILITY_CLASS = "sfiomn.legendarysurvivaloverhaul.api.bodydamage.IBodyDamageCapability";
    private static final String LSO_BODY_PART_ENUM = "sfiomn.legendarysurvivaloverhaul.api.bodydamage.BodyPartEnum";
    private static final String LSO_CAPABILITY_PROVIDER = "sfiomn.legendarysurvivaloverhaul.common.capabilities.bodydamage.BodyDamageProvider";
    private static final String LSO_HEADACHE_EFFECT = "sfiomn.legendarysurvivaloverhaul.common.effects.HeadacheEffect";

    private static final String[] BODY_PARTS = {
            "HEAD", "CHEST", "LEFT_ARM", "RIGHT_ARM",
            "LEFT_LEG", "RIGHT_LEG", "LEFT_FOOT", "RIGHT_FOOT"
    };

    private static boolean isLSOLoaded = false;
    private static Object capabilityKey = null;
    private static Class<?> capabilityClass = null;
    private static Class<?> bodyPartEnumClass = null;
    private static Method getCapabilityMethod = null;
    private static Method healMethod = null;
    private static Method setManualDirtyMethod = null;

    public static void initialize(FMLCommonSetupEvent event) {
        if (!ModList.get().isLoaded("legendarysurvivaloverhaul")) {
            LOGGER.info("Legendary Survival Overhaul not found — skipping compat.");
            return;
        }

        try {
            capabilityClass = Class.forName(LSO_CAPABILITY_CLASS);
            bodyPartEnumClass = Class.forName(LSO_BODY_PART_ENUM);
            Class<?> capabilityProviderClass = Class.forName(LSO_CAPABILITY_PROVIDER);

            Field capKeyField = capabilityProviderClass.getDeclaredField("BODY_DAMAGE_CAPABILITY");
            capabilityKey = capKeyField.get(null);

            Class<?> forgeCapabilityClass = Class.forName("net.minecraftforge.common.capabilities.Capability");

            getCapabilityMethod = LivingEntity.class.getMethod(
                    "getCapability",
                    forgeCapabilityClass,
                    net.minecraft.core.Direction.class
            );

            healMethod = capabilityClass.getMethod("heal", bodyPartEnumClass, float.class);

            try {
                setManualDirtyMethod = capabilityClass.getMethod("setManualDirty");
            } catch (NoSuchMethodException ignored) {}

            isLSOLoaded = true;
            LOGGER.info("LSO compatibility initialized successfully.");

        } catch (Exception e) {
            LOGGER.error("Failed to initialize LSO compatibility", e);
            isLSOLoaded = false;
        }
    }

    private static Object getLSOCapability(Player player) {
        if (!isLSOLoaded || capabilityKey == null || getCapabilityMethod == null) {
            return null;
        }

        try {
            Object lazyOptional = getCapabilityMethod.invoke(player, capabilityKey, null);
            Method orElseMethod = lazyOptional.getClass().getMethod("orElse", Object.class);
            return orElseMethod.invoke(lazyOptional, (Object) null);
        } catch (Exception e) {
            LOGGER.error("Failed to retrieve LSO capability from player", e);
            return null;
        }
    }

    public static void applyPropitalHealing(Player player) {
        if (!isLSOLoaded) return;

        Object capability = getLSOCapability(player);
        if (capability == null) return;

        try {
            Method valueOfMethod = bodyPartEnumClass.getMethod("valueOf", String.class);

            for (String partName : BODY_PARTS) {
                Object bodyPartEnum = valueOfMethod.invoke(null, partName);
                healMethod.invoke(capability, bodyPartEnum, 1.0f);
            }

            if (setManualDirtyMethod != null) {
                setManualDirtyMethod.invoke(capability);
            }

        } catch (Exception e) {
            LOGGER.error("Failed to apply Propital healing via LSO", e);
        }
    }

    public static void applyPainkillerEffect(Player player) {
        if (!isLSOLoaded) return;

        try {
            Class<?> modEffectsClass = Class.forName("sfiomn.legendarysurvivaloverhaul.registry.MobEffectRegistry");
            Field painkillerField = modEffectsClass.getDeclaredField("PAINKILLER");
            Object painkillerSupplier = painkillerField.get(null);

            Method getMethod = painkillerSupplier.getClass().getMethod("get");
            MobEffect painkillerEffect = (MobEffect) getMethod.invoke(painkillerSupplier);

            player.addEffect(new MobEffectInstance(painkillerEffect, 4800, 0, false, true, true));

        } catch (Exception e) {
            LOGGER.error("Failed to apply LSO painkiller effect", e);
        }
    }

    public static void cancelHeadTrauma(Player player) {
        if (!isLSOLoaded) return;

        try {
            Class<?> headacheClass = Class.forName(LSO_HEADACHE_EFFECT);

            Field activeEffectsField = LivingEntity.class.getDeclaredField("f_19853_");
            activeEffectsField.setAccessible(true);

            @SuppressWarnings("unchecked")
            Map<MobEffect, MobEffectInstance> activeEffects =
                    (Map<MobEffect, MobEffectInstance>) activeEffectsField.get(player);

            MobEffect effectToRemove = null;
            for (MobEffect mobEffect : activeEffects.keySet()) {
                if (headacheClass.isInstance(mobEffect)) {
                    effectToRemove = mobEffect;
                    break;
                }
            }

            if (effectToRemove != null) {
                player.removeEffect(effectToRemove);
            }

        } catch (Exception ignored) {}
    }

    public static boolean isInitialized() {
        return isLSOLoaded;
    }
}