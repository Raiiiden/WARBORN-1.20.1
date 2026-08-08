package com.raiiiden.warborn.client.renderer.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.raiiiden.warborn.client.icon.IconPreparable;
import com.raiiiden.warborn.client.model.WarbornGenericArmorModel;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.Map;
import java.util.Set;

public class WarbornGenericArmorRenderer extends GeoArmorRenderer<WBArmorItem> implements IconPreparable {
    private static final Map<String, Set<String>> INSERTED_GOGGLE_BONES = Map.ofEntries(
            Map.entry("beta7_nvg", Set.of("bone5")),
            Map.entry("beta7_nvg_ash", Set.of("bone5")),
            Map.entry("beta7_nvg_slate", Set.of("bone5")),
            Map.entry("fsb_squad_leader", Set.of("bone4")),
            Map.entry("insurgency_shturmovik", Set.of("anpvs14")),
            Map.entry("iz_armor", Set.of("NVG")),
            Map.entry("nato_sqad_leader", Set.of("bone2")),
            Map.entry("nato_sqad_leader_woodland", Set.of("bone2")),
            Map.entry("nato_ukr", Set.of("anpvs14")),
            Map.entry("nato_ukr_woodland", Set.of("anpvs14")),
            Map.entry("sso_squad_leader", Set.of("bone6"))
    );

    private final WBArmorItem item;
    // Set once by {@link #prepForIcon}; icon renderers are cached separately from worn ones.
    private boolean iconMode;

    public WarbornGenericArmorRenderer(WBArmorItem item) {
        super(new WarbornGenericArmorModel(item));
        this.item = item;
    }

    @Override
    public void prepForIcon(ItemStack stack, EquipmentSlot slot) {
        this.baseModel = this;
        this.currentEntity = null;
        this.currentStack = stack;
        this.animatable = this.item;
        this.currentSlot = slot;
        this.iconMode = true;
    }

    // Icon mode has no entity for GeckoLib to key animation state on; see IconPreparable.
    @Override
    public long getInstanceId(WBArmorItem animatable) {
        if (this.currentEntity == null) {
            return IconPreparable.iconInstanceId(this.item);
        }
        return super.getInstanceId(animatable);
    }

    // A worn uniform covers the whole body, so show every bone but the head; icons keep the default chest masking or auto-fit frames a tall, narrow box.
    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        if (!this.iconMode && this.item != null && this.item.isUniform()) {
            setAllBonesVisible(true);
            setBoneVisible(this.head, false);
            return;
        }
        super.applyBoneVisibilityBySlot(currentSlot);
    }

    // A slim skin's arms are 3px wide instead of 4, and lose that pixel from the outer face - the
    // inner face stays put. Sleeves are modelled for the 4px arm, so they float off a slim one.
    // GeckoLib scales a bone about its pivot, which sits at the shoulder rather than the inner
    // face, so the quarter-pixel shift is what re-anchors the squeeze to the face that didn't move.
    private static final float SLIM_ARM_SCALE = 0.75F;
    private static final float SLIM_ARM_SHIFT = 0.25F;

    @Override
    protected void applyBaseTransformations(HumanoidModel<?> baseModel) {
        super.applyBaseTransformations(baseModel);

        boolean slim = isSlimArmed(this.currentEntity);
        // The baked model is cached per geo file and shared by every wearer, so the classic width
        // has to be written back too - otherwise one slim player leaves narrow sleeves on everyone.
        fitArmToSkin(this.rightArm, slim, SLIM_ARM_SHIFT);
        fitArmToSkin(this.leftArm, slim, -SLIM_ARM_SHIFT);
    }

    private static void fitArmToSkin(GeoBone arm, boolean slim, float shift) {
        if (arm == null) return;

        arm.setScaleX(slim ? SLIM_ARM_SCALE : 1.0F);
        if (slim) arm.setPosX(arm.getPosX() + shift);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, WBArmorItem animatable, GeoBone bone, RenderType renderType,
                                  MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender,
                                  float partialTick, int packedLight, int packedOverlay,
                                  float red, float green, float blue, float alpha) {
        Set<String> insertedGoggleBones = INSERTED_GOGGLE_BONES.get(animatable.getArmorType());

        if (insertedGoggleBones != null && insertedGoggleBones.contains(bone.getName())) {
            bone.setHidden(shouldHideInsertedGoggleBones());
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender,
                partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    private boolean shouldHideInsertedGoggleBones() {
        ItemStack stack = getCurrentStack();
        return stack != null && !stack.isEmpty() && WBArmorItem.canHaveGoggles(stack) && !WBArmorItem.hasInsertedGoggles(stack);
    }

    public static boolean isSlimArmed(Entity entity) {
        if (!(entity instanceof AbstractClientPlayer player)) return false;
        return "slim".equals(player.getModelName());
    }
}
