package com.raiiiden.warborn.client.icon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.renderer.armor.WarbornBackpackRenderer;
import com.raiiiden.warborn.client.renderer.armor.WarbornGenericArmorRenderer;
import com.raiiiden.warborn.common.item.BackpackItem;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

import java.util.IdentityHashMap;
import java.util.Map;

// Draws gear outside of an entity, reusing GeckoLib's own renderers so icon and worn piece can't drift; each item is measured once via IconBounds to auto-frame it.
public final class WarbornArmorIcons {
    // 180 to turn the model to face the camera, plus 30 for a three-quarter view.
    private static final float VIEW_YAW = 210.0f;
    private static final float VIEW_PITCH = 12.0f;
    // A backpack is modelled on the wearer's back, so spin it round to show the pack rather than the straps.
    private static final float BACK_WORN_YAW = 180.0f;
    // Leaves a sliver of empty space around the piece so it never touches the edge of its cell.
    private static final float MARGIN = 1.06f;

    private static final Map<Item, GeoArmorRenderer<?>> RENDERERS = new IdentityHashMap<>();
    private static final Map<Item, Fit> FITS = new IdentityHashMap<>();

    private WarbornArmorIcons() {}

    // The framing for one item, in the view space produced by {@link #applyView}.
    public record Fit(float centerX, float centerY, float half) {}

    // Cached "this item has nothing to draw", so a bad model is measured once rather than every frame.
    private static final Fit NOTHING = new Fit(0.0f, 0.0f, -1.0f);

    public static boolean isSupported(Item item) {
        return item instanceof WBArmorItem || item instanceof BackpackItem;
    }

    // Camera for rendering outside an entity; the trailing flip cancels GeoArmorRenderer's un-flip, without which the piece comes out upside down and unlit. Measure and render must share this.
    public static void applyView(PoseStack poseStack, Item item) {
        float yaw = VIEW_YAW + (item instanceof BackpackItem ? BACK_WORN_YAW : 0.0f);

        poseStack.mulPose(Axis.XP.rotationDegrees(VIEW_PITCH));
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.scale(-1.0f, -1.0f, 1.0f);
        poseStack.translate(0.0f, -1.5f, 0.0f);
    }

    // Returns how to frame this item, or null if it isn't ours or has nothing visible in its slot.
    @Nullable
    public static Fit fit(Item item) {
        Fit cached = FITS.get(item);
        if (cached != null) return cached.half() > 0.0f ? cached : null;
        if (!isSupported(item)) return null;

        Fit fit = measure(item);
        FITS.put(item, fit == null ? NOTHING : fit);
        return fit;
    }

    @Nullable
    private static Fit measure(Item item) {
        GeoArmorRenderer<?> renderer = renderer(item);
        if (renderer == null) return null;

        IconBounds bounds = new IconBounds();
        PoseStack poseStack = new PoseStack();
        applyView(poseStack, item);

        try {
            prepare(renderer, item);
            render(renderer, item, poseStack, bounds, bounds);
        } catch (Exception exception) {
            WARBORN.LOGGER.error("Failed to measure gear model for {}", item, exception);
            return null;
        }

        if (bounds.isEmpty()) return null;

        float half = Math.max(bounds.maxX - bounds.minX, bounds.maxY - bounds.minY) * 0.5f * MARGIN;
        if (!(half > 1.0e-4f)) return null;

        return new Fit((bounds.minX + bounds.maxX) * 0.5f, (bounds.minY + bounds.maxY) * 0.5f, half);
    }

    // Renders the item's geo model into {@code buffers}. The caller owns the camera.
    public static void draw(Item item, PoseStack poseStack, MultiBufferSource buffers) {
        GeoArmorRenderer<?> renderer = renderer(item);
        if (renderer == null) return;

        prepare(renderer, item);
        render(renderer, item, poseStack, buffers, null);
    }

    public static void clear() {
        RENDERERS.clear();
        FITS.clear();
    }

    @Nullable
    private static GeoArmorRenderer<?> renderer(Item item) {
        GeoArmorRenderer<?> cached = RENDERERS.get(item);
        if (cached != null) return cached;

        GeoArmorRenderer<?> created;
        if (item instanceof WBArmorItem armor) {
            created = new WarbornGenericArmorRenderer(armor);
        } else if (item instanceof BackpackItem backpack) {
            created = new WarbornBackpackRenderer(backpack);
        } else {
            return null;
        }

        RENDERERS.put(item, created);
        return created;
    }

    private static void prepare(GeoArmorRenderer<?> renderer, Item item) {
        EquipmentSlot slot = item instanceof ArmorItem armor ? armor.getEquipmentSlot() : EquipmentSlot.CHEST;
        ((IconPreparable) renderer).prepForIcon(new ItemStack(item), slot);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void render(GeoArmorRenderer renderer, Item item, PoseStack poseStack,
                               MultiBufferSource buffers, @Nullable VertexConsumer buffer) {
        renderer.defaultRender(poseStack, (GeoAnimatable) item, buffers, null, buffer,
                0.0f, 0.0f, LightTexture.FULL_BRIGHT);
    }
}
