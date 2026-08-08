package com.raiiiden.warborn.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class WarbornCurioRenderLayer<T extends LivingEntity, M extends HumanoidModel<T>, I extends Item & GeoItem>
        extends RenderLayer<T, M> {
    private final Class<I> itemClass;
    private final Predicate<I> itemMatcher;
    private final Predicate<SlotResult> slotMatcher;
    private final EquipmentSlot equipmentSlot;
    private final Function<I, GeoArmorRenderer<I>> rendererFactory;
    private final BiConsumer<GeoArmorRenderer<I>, T> rendererSetup;

    public WarbornCurioRenderLayer(RenderLayerParent<T, M> parent,
                                   Class<I> itemClass,
                                   Predicate<I> itemMatcher,
                                   Predicate<SlotResult> slotMatcher,
                                   EquipmentSlot equipmentSlot,
                                   Function<I, GeoArmorRenderer<I>> rendererFactory,
                                   BiConsumer<GeoArmorRenderer<I>, T> rendererSetup) {
        super(parent);
        this.itemClass = itemClass;
        this.itemMatcher = itemMatcher;
        this.slotMatcher = slotMatcher;
        this.equipmentSlot = equipmentSlot;
        this.rendererFactory = rendererFactory;
        this.rendererSetup = rendererSetup;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        List<SlotResult> curios = CuriosApi.getCuriosHelper().findCurios(entity, this::matchesItem);

        for (SlotResult slotResult : curios) {
            if (slotMatcher.test(slotResult)) {
                renderCurio(poseStack, bufferSource, packedLight, entity, slotResult.stack());
            }
        }
    }

    private boolean matchesItem(ItemStack stack) {
        return itemClass.isInstance(stack.getItem()) && itemMatcher.test(itemClass.cast(stack.getItem()));
    }

    private void renderCurio(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight,
                             T entity, ItemStack stack) {
        I item = itemClass.cast(stack.getItem());
        GeoArmorRenderer<I> renderer = rendererFactory.apply(item);

        this.getParentModel().copyPropertiesTo(renderer);
        renderer.prepForRender(entity, stack, equipmentSlot, renderer);
        rendererSetup.accept(renderer, entity);
        renderer.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.armorCutoutNoCull(renderer.getTextureLocation(item))),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );
    }
}
