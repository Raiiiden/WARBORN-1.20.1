package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.client.renderer.item.WarbornNVGHandRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
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

public class NVGHandItem extends Item implements GeoItem {

    public static final RawAnimation USE_ANIMATION =
            RawAnimation.begin().thenPlay("animation.use");
    public static final RawAnimation REMOVE_ANIMATION =
            RawAnimation.begin().thenPlay("animation.remove");

    public static final String CONTROLLER = "controller";

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public NVGHandItem(Properties props) {
        super(props.stacksTo(1));
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> cons) {
        cons.accept(new IClientItemExtensions() {
            private WarbornNVGHandRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new WarbornNVGHandRenderer();
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

                    if (state.isCurrentAnimation(USE_ANIMATION) ||
                            state.isCurrentAnimation(REMOVE_ANIMATION)) {
                        return PlayState.CONTINUE;
                    }

                    return PlayState.STOP;
                })
                .triggerableAnim("use", USE_ANIMATION)
                .triggerableAnim("remove", REMOVE_ANIMATION)
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
}
