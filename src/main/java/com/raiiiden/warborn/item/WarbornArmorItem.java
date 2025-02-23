package com.raiiiden.warborn.item;

import com.raiiiden.warborn.client.renderer.armor.*;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class WarbornArmorItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String armorType;

    public WarbornArmorItem(ArmorMaterial armorMaterial, Type type, Item.Properties properties, String armorType) {
        super(armorMaterial, type, properties);
        this.armorType = armorType;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = getRendererForArmorType(armorType);
                }
                this.renderer.prepForRender(entity, stack, slot, original);
                return this.renderer;
            }
        });
    }

    private GeoArmorRenderer<?> getRendererForArmorType(String type) {
        switch (type) {
            case "rusbron":
                return new RusBronArmorRenderer();
            case "rusbron_rof_liko":
                return new RusBronRofLikoArmorRenderer();
            case "rusbron_for_liko":
                return new RusBronForLikoArmorRenderer();
            case "rusbron_pulemetchik":
                return new RusBronPulemetchikArmorRenderer();
            case "rusbron_pulemetchik_disguise":
                return new RusBronPulemetchikDisguiseArmorRenderer();
            case "rusbron_pulemetchik_net_naplekhnikov":
                return new RusBronPulemetchikNetNaplekhnikovArmorRenderer();
            case "rusbron_shturmovik":
                return new RusBronShturmovikArmorRenderer();
            case "rusbron_shturmovik_disguise":
                return new RusBronShturmovikDisguiseArmorRenderer();
            case "rusbron_shturmovik_net_naplekhnikov":
                return new RusBronShturmovikNetNaplekhnikovArmorRenderer();
            case "ukrbron_pulemetchik":
                return new UkrBronPulemetchikArmorRenderer();
            case "ukrbron_pulemetchik_disguise":
                return new UkrBronPulemetchikDisguiseArmorRenderer();
            case "ukrbron_pulemetchik_net_naplekhnikov":
                return new UkrBronArmorRenderer();
            case "ukrbron":
                return new UkrBronArmorRenderer();
            default:
                return new WarbornArmorRenderer();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 20, state -> PlayState.CONTINUE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
