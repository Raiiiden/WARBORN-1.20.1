package com.raiiiden.warborn.item;

import com.raiiiden.warborn.client.renderer.WarbornPlateRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class WarbornPlateItem extends Item implements GeoItem {
    public static final RawAnimation INSERT_ANIMATION = RawAnimation.begin().thenPlay("insert");
    public static final RawAnimation IDLE_ANIMATION = RawAnimation.begin().thenLoop("idle");

    private static final String MAIN_CONTROLLER = "controller";
    private static final int ANIMATION_DURATION = 40;
    public final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public WarbornPlateItem(Properties properties) {
        super(properties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private WarbornPlateRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new WarbornPlateRenderer();
                }
                return this.renderer;
            }
        });
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level instanceof ServerLevel serverLevel) {
            this.triggerAnim(player, GeoItem.getOrAssignId(stack, serverLevel),
                    MAIN_CONTROLLER, "insert");
        }

        stack.getOrCreateTag().putBoolean("inserting", true);

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, MAIN_CONTROLLER, 0, (event) -> shouldPlayAnimation() ? PlayState.CONTINUE : PlayState.STOP).triggerableAnim("insert", INSERT_ANIMATION).triggerableAnim("idle", IDLE_ANIMATION));
    }

    private boolean shouldPlayAnimation() {
        Minecraft minecraft = Minecraft.getInstance();
        return minecraft.options.getCameraType().isFirstPerson();
    }

    @Nullable
    private Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
