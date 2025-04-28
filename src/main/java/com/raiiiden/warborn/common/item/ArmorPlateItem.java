package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.renderer.item.WarbornPlateRenderer;
import com.raiiiden.warborn.common.init.ModRegistry;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import com.raiiiden.warborn.common.util.PlateTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class ArmorPlateItem extends Item implements GeoItem {

    public static final RawAnimation INSERT_ANIMATION =
            RawAnimation.begin().thenPlay("animation.use_peny");
    public static final RawAnimation IDLE_ANIMATION =
            RawAnimation.begin().thenLoop("animation.idle");
    public static final TagKey<Item> PLATE_COMPATIBLE =
            ItemTags.create(new ResourceLocation(WARBORN.MODID, "plate_compatible"));
    private static final Logger LOGGER =
            LogManager.getLogger(WARBORN.MODID + "/ArmorPlateItem");
    private static final String CONTROLLER = "controller";

    private static final String MSG_PREFIX = "message." + WARBORN.MODID + ".plate.";
    private static final String KEY_MSG_MISSING_CHEST = MSG_PREFIX + "missing_chestplate";
    private static final String KEY_MSG_INCOMPATIBLE_CHEST = MSG_PREFIX + "incompatible_chestplate";
    private static final String KEY_MSG_PLATE_BROKEN = MSG_PREFIX + "is_broken";
    private static final String KEY_MSG_FRONT_INSTALLED = MSG_PREFIX + "front_installed";
    private static final String KEY_MSG_BACK_INSTALLED = MSG_PREFIX + "back_installed";
    private static final String KEY_MSG_SLOTS_FULL = MSG_PREFIX + "slots_full";

    private final ProtectionTier tier;
    private final MaterialType material;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ArmorPlateItem(ProtectionTier tier, MaterialType material, Properties props) {
        super(props.durability(material.getBaseDurability()));
        this.tier = tier;
        this.material = material;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public static boolean isPlateCompatible(ItemStack chest) {
        return !chest.isEmpty() && chest.is(PLATE_COMPATIBLE);
    }

    public static ItemStack createPlateWithHitsRemaining(ProtectionTier t, MaterialType m, int currentDur) {
        ItemStack stack = new ItemStack(ModRegistry.getPlateItem(t, m));
        int damage = Math.max(0, m.getBaseDurability() - currentDur);
        stack.setDamageValue(damage);
        return stack;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack held = player.getItemInHand(hand);

        if (chest.isEmpty()) {
            player.displayClientMessage(Component.translatable(KEY_MSG_MISSING_CHEST)
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(held);
        }
        if (!isPlateCompatible(chest)) {
            player.displayClientMessage(Component.translatable(KEY_MSG_INCOMPATIBLE_CHEST)
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(held);
        }

        int maxDur = this.material.getBaseDurability();
        int currentDur = maxDur > 0 ? maxDur - held.getDamageValue() : 0;

        if (currentDur <= 0) {
            player.displayClientMessage(Component.translatable(KEY_MSG_PLATE_BROKEN)
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(held);
        }

        AtomicBoolean inserted = new AtomicBoolean(false);

        chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            Plate plate = new Plate(this.tier, this.material);
            plate.setCurrentDurability(currentDur);

            if (!cap.hasFrontPlate()) {
                cap.insertFrontPlate(plate);
                player.displayClientMessage(Component.translatable(KEY_MSG_FRONT_INSTALLED)
                        .withStyle(ChatFormatting.GREEN), true);
                inserted.set(true);
            } else if (!cap.hasBackPlate()) {
                cap.insertBackPlate(plate);
                player.displayClientMessage(Component.translatable(KEY_MSG_BACK_INSTALLED)
                        .withStyle(ChatFormatting.GREEN), true);
                inserted.set(true);
            } else {
                player.displayClientMessage(Component.translatable(KEY_MSG_SLOTS_FULL)
                        .withStyle(ChatFormatting.RED), true);
            }
        });

        if (!inserted.get()) return InteractionResultHolder.fail(held);
        if (!player.isCreative()) held.shrink(1);

        if (level instanceof ServerLevel serverLevel) {
            this.triggerAnim(player, GeoItem.getOrAssignId(held, serverLevel),
                    CONTROLLER, "use");
        }
        return InteractionResultHolder.sidedSuccess(held, level.isClientSide);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> cons) {
        cons.accept(new IClientItemExtensions() {
            private WarbornPlateRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new WarbornPlateRenderer();
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
        reg.add(new AnimationController<>(this, CONTROLLER, 0, s -> PlayState.CONTINUE)
                .triggerableAnim("use", INSERT_ANIMATION));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isPerspectiveAware() {
        return true;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack o, ItemStack n, boolean slotChanged) {
        return slotChanged || o.getItem() != n.getItem();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                List<Component> tip, TooltipFlag flag) {
        PlateTooltip.addPlate(tip, tier, material, stack);
        super.appendHoverText(stack, level, tip, flag);
    }

    public ProtectionTier getTier() {
        return tier;
    }

    public MaterialType getMaterial() {
        return material;
    }
}
