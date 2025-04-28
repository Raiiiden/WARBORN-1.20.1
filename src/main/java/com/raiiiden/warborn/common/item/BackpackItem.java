package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.client.renderer.armor.WarbornBackpackRenderer;
import com.raiiiden.warborn.common.object.BackpackMenu;
import com.raiiiden.warborn.common.object.capability.BackpackCapabilityProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.function.Consumer;

public class BackpackItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String armorType;

    public BackpackItem(ArmorMaterial material, Type type, Properties properties, String armorType) {
        super(material, type, properties);
        this.armorType = armorType;
    }

    public static boolean isBackpackItem(ItemStack stack) {
        return stack.getItem() instanceof BackpackItem;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) {
            // Shift+Right Click: Equip to Curios
            if (!level.isClientSide) {
                equipToCurios(player, stack);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        } else {
            // Regular Right Click: Open Backpack
            if (!level.isClientSide) {
                openBackpackScreen(player, stack);
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
    }

    private void openBackpackScreen(Player player, ItemStack stack) {
        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(
                    serverPlayer,
                    new SimpleMenuProvider(
                            (id, inv, p) -> new BackpackMenu(id, inv, stack),
                            Component.literal("Backpack")
                    ),
                    buf -> buf.writeItem(stack)
            );
            playOpenSound(player);
        }
    }

    private void equipToCurios(Player player, ItemStack stack) {
        CuriosApi.getCuriosInventory(player).ifPresent(curios -> {
            curios.getCurios().forEach((id, handler) -> {
                if (!id.equals("back")) return;

                for (int i = 0; i < handler.getSlots(); i++) {
                    if (handler.getStacks().getStackInSlot(i).isEmpty()) {
                        handler.getStacks().setStackInSlot(i, stack.copy());
                        stack.shrink(1);
                        player.displayClientMessage(
                                Component.literal("Equipped backpack to Curios 'back' slot")
                                        .withStyle(ChatFormatting.GRAY),
                                true
                        );
                        playEquipSound(player);
                        return;
                    }
                }
            });
        });
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new BackpackCapabilityProvider(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        components.add(Component.literal("Right Click: Open Backpack").withStyle(ChatFormatting.GRAY));
        components.add(Component.literal("Shift+Right Click: Equip to Curios").withStyle(ChatFormatting.GRAY));
    }

    // GeckoLib Animation Methods
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 20, state -> PlayState.CONTINUE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    // Client-side rendering
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
                                                                   EquipmentSlot slot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new WarbornBackpackRenderer(BackpackItem.this);
                }
                this.renderer.prepForRender(entity, stack, slot, original);
                return this.renderer;
            }
        });
    }

    public String getArmorType() {
        return this.armorType;
    }

    // Sound effects
    private void playOpenSound(Entity entity) {
        entity.playSound(SoundEvents.ARMOR_EQUIP_LEATHER, 0.8F, 1.0F);
    }

    private void playEquipSound(Entity entity) {
        entity.playSound(SoundEvents.ARMOR_EQUIP_GENERIC, 0.8F, 1.0F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}