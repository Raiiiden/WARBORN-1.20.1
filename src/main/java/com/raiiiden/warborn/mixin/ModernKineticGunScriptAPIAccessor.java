package com.raiiiden.warborn.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(targets = "com.tacz.guns.item.ModernKineticGunScriptAPI", remap = false)
public interface ModernKineticGunScriptAPIAccessor {
    @Accessor("shooter")
    LivingEntity warborn$getShooter();

    @Accessor("itemStack")
    ItemStack warborn$getItemStack();
}