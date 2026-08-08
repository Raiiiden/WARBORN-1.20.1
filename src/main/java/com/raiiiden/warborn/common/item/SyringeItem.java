package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.model.WarbornSyringeModel;
import com.raiiiden.warborn.client.renderer.item.WarbornFirstPersonHandRenderer;
import com.raiiiden.warborn.client.sound.WarbornClientSounds;
import com.raiiiden.warborn.common.network.ModNetworking;
import com.raiiiden.warborn.common.network.PlayWarbornSoundPacket;
import com.raiiiden.warborn.common.network.StopWarbornSoundPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Set;
import java.util.function.Consumer;

public class SyringeItem extends Item implements GeoItem {

    public static final RawAnimation USE_ANIMATION =
            RawAnimation.begin().thenPlay("animation.use");
    public static final RawAnimation IDLE_ANIMATION =
            RawAnimation.begin().thenLoop("animation.idle");

    public static final String CONTROLLER = "controller";
    public static final String PENDING_USE_TAG = "warborn_pending_use";

    private static final Logger LOGGER =
            LogManager.getLogger(WARBORN.MODID + "/SyringeItem");

    private static final String MSG_PREFIX = "message." + WARBORN.MODID + ".syringe.";
    private static final String KEY_MSG_IN_WATER = MSG_PREFIX + "in_water";

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final SyringeType syringeType;

    public SyringeItem(Properties props, SyringeType type) {
        super(props.stacksTo(1));
        this.syringeType = type;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        CompoundTag tag = held.getOrCreateTag();

        // Check if already pending use
        if (tag.getBoolean(PENDING_USE_TAG)) {
            return InteractionResultHolder.pass(held);
        }

        // Universal water check
        if (player.isInWater()) {
            player.displayClientMessage(
                    Component.translatable(KEY_MSG_IN_WATER).withStyle(ChatFormatting.RED),
                    true
            );
            return InteractionResultHolder.fail(held);
        }

        // Check type-specific conditions
        if (!syringeType.canUse(player, level)) {
            player.displayClientMessage(syringeType.getFailureMessage(), true);
            return InteractionResultHolder.fail(held);
        }

        if (level instanceof ServerLevel serverLevel) {
            // Mark that we're processing a use
            tag.putBoolean(PENDING_USE_TAG, true);
            tag.putInt("warborn_use_delay", 45); // Animation delay in ticks

            this.triggerAnim(player, GeoItem.getOrAssignId(held, serverLevel), CONTROLLER, "use");

            // Play sound server-side so it syncs properly and can be cancelled
            ModNetworking.sendToPlayer(new PlayWarbornSoundPacket(PlayWarbornSoundPacket.SoundType.SYRINGE), (ServerPlayer) player);
        }

        return level.isClientSide
                ? InteractionResultHolder.pass(held)
                : InteractionResultHolder.consume(held);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide) return;

        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        if (tag.getBoolean(PENDING_USE_TAG)) {
            // If not selected anymore, cancel the operation
            if (!selected) {
                cancelPendingUse(tag, player);
                return;
            }

            int delay = tag.getInt("warborn_use_delay") - 1;
            tag.putInt("warborn_use_delay", delay);
            if (delay > 0) return;

            // Animation finished, apply effect
            tag.remove(PENDING_USE_TAG);
            tag.remove("warborn_use_delay");

            // Apply the syringe effect based on type
            syringeType.applyEffect(player, level);
            player.displayClientMessage(syringeType.getSuccessMessage(), true);

            // Consume the item
            if (!player.isCreative()) {
                stack.shrink(1);
            }

            return;
        }

        // Clean up any leftover tags
        if (tag.contains("GeckoLibID")) tag.remove("GeckoLibID");
        cancelPendingUse(tag, player);
    }

    private void cancelPendingUse(CompoundTag tag, Player player) {
        boolean wasPending = tag.getBoolean(PENDING_USE_TAG);
        tag.remove(PENDING_USE_TAG);
        tag.remove("warborn_use_delay");

        // Send packet to client to stop the sound
        if (wasPending && player instanceof ServerPlayer serverPlayer) {
            ModNetworking.sendToPlayer(new StopWarbornSoundPacket(StopWarbornSoundPacket.SoundType.SYRINGE), serverPlayer);
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> cons) {
        cons.accept(new IClientItemExtensions() {
            private WarbornFirstPersonHandRenderer<SyringeItem> renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new WarbornFirstPersonHandRenderer<>(
                            new WarbornSyringeModel(),
                            Set.of("left_hand", "right_hand"),
                            "using"
                    );
                }
                return renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar reg) {
        reg.add(new AnimationController<>(this, CONTROLLER, 0, state -> {
                    ItemDisplayContext context = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

                    if (context == ItemDisplayContext.GUI ||
                            context == ItemDisplayContext.GROUND ||
                            context == ItemDisplayContext.FIXED)
                        return PlayState.STOP;

                    if (state.isCurrentAnimation(USE_ANIMATION)) {
                        return PlayState.CONTINUE;
                    }

                    state.setAnimation(IDLE_ANIMATION);
                    return PlayState.CONTINUE;
                })
                        .triggerableAnim("use", USE_ANIMATION)
        );
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
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        CompoundTag tag = oldStack.getTag();
        if (tag != null && tag.getBoolean(PENDING_USE_TAG)) {
            tag.remove(PENDING_USE_TAG);
            tag.remove("warborn_use_delay");
        }
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    public SyringeType getSyringeType() {
        return syringeType;
    }
}
