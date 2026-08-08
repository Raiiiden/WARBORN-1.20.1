package com.raiiiden.warborn.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.raiiiden.warborn.client.icon.WarbornArmorIcons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

// Renders gear as its geo model outside inventory slots - held, dropped, in an item frame; slots are served by WarbornIconAtlas.
public class WarbornArmorItemRenderer extends BlockEntityWithoutLevelRenderer {
    public WarbornArmorItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffers,
                             int light, int overlay) {
        Item item = stack.getItem();
        WarbornArmorIcons.Fit fit = WarbornArmorIcons.fit(item);
        if (fit == null) return;

        poseStack.pushPose();
        // Normalise the piece into the unit cube an item model is expected to occupy, using the same
        // measurement the baked icon uses so held and inventory views frame it identically.
        poseStack.translate(0.5f, 0.5f, 0.5f);
        float scale = 0.5f / fit.half();
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-fit.centerX(), -fit.centerY(), 0.0f);
        WarbornArmorIcons.applyView(poseStack, item);

        WarbornArmorIcons.draw(item, poseStack, buffers);
        poseStack.popPose();
    }
}
