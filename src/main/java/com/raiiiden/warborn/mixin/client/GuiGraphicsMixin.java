package com.raiiiden.warborn.mixin.client;

import com.raiiiden.warborn.client.icon.WarbornIconAtlas;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Swaps in the baked geo icon before vanilla builds and draws its flat item model; every renderItem overload funnels through this one.
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Inject(
            method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void warborn$drawBakedIcon(LivingEntity entity, Level level, ItemStack stack, int x, int y,
                                       int seed, int guiOffset, CallbackInfo ci) {
        GuiGraphics graphics = (GuiGraphics) (Object) this;
        if (WarbornIconAtlas.draw(graphics.pose(), stack, x, y)) {
            ci.cancel();
        }
    }
}
