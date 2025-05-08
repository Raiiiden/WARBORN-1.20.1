package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.renderer.item.WarbornPlateRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
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

import java.util.function.Consumer;

public class SyringeItem extends Item implements GeoItem {

    public static final RawAnimation USE_ANIMATION =
            RawAnimation.begin().thenPlay("animation.use");
    public static final RawAnimation IDLE_ANIMATION =
            RawAnimation.begin().thenLoop("animation.idle");

    private static final String CONTROLLER = "controller";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public SyringeItem(Properties props) {
        super(props.stacksTo(1)); // likely a single-use item
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isInWater()) {
            player.displayClientMessage(Component.translatable("message." + WARBORN.MODID + ".syringe_fail").withStyle(net.minecraft.ChatFormatting.RED), true);
            return InteractionResultHolder.fail(stack);
        }

        if (!level.isClientSide) {
            // effect here: heal 4 hearts
            player.heal(8.0F);
            stack.shrink(1);

            if (level instanceof ServerLevel serverLevel) {
                this.triggerAnim(player, GeoItem.getOrAssignId(stack, serverLevel), CONTROLLER, "use");
            }
        }

        return InteractionResultHolder.success(stack);
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
        reg.add(new AnimationController<>(this, CONTROLLER, 0, state -> {
            ItemDisplayContext context = state.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

            if (context == ItemDisplayContext.GUI || context == ItemDisplayContext.GROUND || context == ItemDisplayContext.FIXED)
                return PlayState.STOP;

            if (state.isCurrentAnimation(USE_ANIMATION))
                return PlayState.CONTINUE;

            state.setAnimation(IDLE_ANIMATION);
            return PlayState.CONTINUE;
        }).triggerableAnim("use", USE_ANIMATION));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public boolean isPerspectiveAware() {
        return true;
    }
}
