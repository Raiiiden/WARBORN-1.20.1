package com.raiiiden.warborn.client.renderer.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.renderer.armor.WarbornGoggleMountRenderer;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

// Draws a borrowed NVG mount on helmets tagged borrows_nvg_mount, once goggles are installed.
//
// Helmets that model their own goggles just hide the bone until goggles go in - see
// INSERTED_GOGGLE_BONES in WarbornGenericArmorRenderer. A helmet with no such bone needs the part
// supplied from elsewhere, which is what this layer does.
public class WarbornGoggleMountLayer<T extends LivingEntity, M extends HumanoidModel<T>>
        extends RenderLayer<T, M> {

    private static final TagKey<Item> BORROWS_NVG_MOUNT =
            TagKey.create(Registries.ITEM, new ResourceLocation(WARBORN.MODID, "borrows_nvg_mount"));

    // The donor part, and how it has to sit to suit the Ratnik shell rather than the NATO one it was
    // modelled against. Both are in bone-space pixels: positive up raises the mount, positive back
    // pushes it off the face. Measured off the two helmets' forehead cubes, so expect to nudge them.
    private static final String DONOR_SET = "nato_sqad_leader";
    private static final String DONOR_BONE = "bone5";
    private static final float OFFSET_UP = -0.37F;
    private static final float OFFSET_BACK = 1.44F;

    private WarbornGoggleMountRenderer renderer;

    public WarbornGoggleMountLayer(RenderLayerParent<T, M> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T entity,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack helmet = entity.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof WBArmorItem item)) return;
        if (!helmet.is(BORROWS_NVG_MOUNT) || !WBArmorItem.hasInsertedGoggles(helmet)) return;

        if (this.renderer == null) {
            this.renderer = new WarbornGoggleMountRenderer(DONOR_SET, DONOR_BONE, OFFSET_UP, OFFSET_BACK);
        }

        getParentModel().copyPropertiesTo(this.renderer);
        this.renderer.prepForRender(entity, helmet, EquipmentSlot.HEAD, this.renderer);
        this.renderer.renderToBuffer(
                poseStack,
                bufferSource.getBuffer(RenderType.armorCutoutNoCull(this.renderer.getTextureLocation(item))),
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F, 1.0F, 1.0F, 1.0F
        );
    }
}
