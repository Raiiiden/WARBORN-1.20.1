package com.raiiiden.warborn.item;

import com.raiiiden.warborn.client.renderer.armor.WarbornGenericArmorRenderer;
import com.raiiiden.warborn.common.network.ModNetworking;
import com.raiiiden.warborn.common.network.OpenBackpackPacket;
import com.raiiiden.warborn.common.object.capability.BackpackCapabilityProvider;
import com.raiiiden.warborn.common.object.capability.BackpackItemStackHandler;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.function.Consumer;

public class WarbornArmorItem extends ArmorItem implements GeoItem, ICurioItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String armorType;

    public WarbornArmorItem(ArmorMaterial armorMaterial, Type type, Item.Properties properties, String armorType) {
        super(armorMaterial, type, properties);
        this.armorType = armorType;
    }

    public String getArmorType() {
        return this.armorType;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new WarbornGenericArmorRenderer(WarbornArmorItem.this);
                }
                this.renderer.prepForRender(entity, stack, slot, original);
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, 20, state -> PlayState.CONTINUE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide && isBackpackItem(itemStack)) {
            ModNetworking.openBackpack(itemStack);
        }
        return InteractionResultHolder.success(itemStack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!level.isClientSide && isBackpackItem(stack) && !stack.getOrCreateTag().contains("BackpackCap")) {
            stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
                if (cap instanceof INBTSerializable<?> serializable) {
                    CompoundTag tag = stack.getOrCreateTag();
                    tag.put("BackpackCap", (CompoundTag) serializable.serializeNBT());
                    stack.setTag(tag);
                }
            });
        }
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (isBackpackItem(stack)) {
            return new BackpackCapabilityProvider(stack);
        }
        return super.initCapabilities(stack, nbt);
    }

    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        CompoundTag tag = super.getShareTag(stack);
        final CompoundTag finalTag = tag != null ? tag : new CompoundTag();

        if (isBackpackItem(stack)) {
            LazyOptional<IItemHandler> optional = stack.getCapability(ForgeCapabilities.ITEM_HANDLER);
            optional.ifPresent(cap -> {
                if (cap instanceof BackpackItemStackHandler handler) {
                    finalTag.put("BackpackCap", handler.serializeNBT());
                }
            });
        }

        return finalTag;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag tag) {
        super.readShareTag(stack, tag);
        if (isBackpackItem(stack) && tag != null && tag.contains("BackpackCap")) {
            stack.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
                if (cap instanceof BackpackItemStackHandler handler) {
                    handler.deserializeNBT(tag.getCompound("BackpackCap"));
                }
            });
        }
    }

    public static boolean isBackpackItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && id.getPath().toLowerCase().contains("backpack");
    }
}
