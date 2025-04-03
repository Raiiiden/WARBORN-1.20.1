package com.raiiiden.warborn.item;

import com.raiiiden.warborn.common.object.capability.ChestplateBundleCapabilityProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.SlotAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.raiiiden.warborn.client.renderer.armor.WarbornGenericArmorRenderer;
import com.raiiiden.warborn.common.network.ModNetworking;
import com.raiiiden.warborn.common.object.capability.BackpackCapabilityProvider;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class WarbornArmorItem extends ArmorItem implements GeoItem, ICurioItem {
    private static final int MAX_STACK_SIZE = 100;
    private static final int MAX_SLOTS = 4;
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String armorType;

    public WarbornArmorItem(ArmorMaterial armorMaterial, Type type, Item.Properties properties, String armorType) {
        super(armorMaterial, type, properties.stacksTo(1));
        this.armorType = armorType;
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return true;
    }

    public static boolean isChestplateItem(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && id.getPath().toLowerCase().contains("chestplate");
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack chestplate, Slot slot, ClickAction action, Player player) {
        if (!(chestplate.getItem() instanceof WarbornArmorItem) || !isChestplateItem(chestplate)) return false;
        if (action != ClickAction.SECONDARY) return false;

        ItemStack slotStack = slot.getItem();

        if (slotStack.isEmpty()) {
            if (player.isShiftKeyDown()) {
                Optional<ItemStack> extracted = removeItem(chestplate);
                if (extracted.isPresent()) {
                    ItemStack result = extracted.get();
                    slot.set(result);
                    playRemoveOneSound(player);
                    return true;
                }
            } else {
                return false;
            }
        } else {
            if (!slotStack.getItem().canFitInsideContainerItems()) return false;

            int canAdd = getAvailableSpace(chestplate, slotStack);
            if (canAdd > 0) {
                ItemStack toAdd = slotStack.copyWithCount(Math.min(canAdd, slotStack.getCount()));
                int actuallyAdded = addItems(chestplate, toAdd);
                if (actuallyAdded > 0) {
                    slotStack.shrink(actuallyAdded);
                    if (slotStack.isEmpty()) slot.set(ItemStack.EMPTY);
                    playInsertSound(player);
                    return true;
                }
            }
        }

        return false;
    }
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack chestplate, ItemStack itemBeingMoved, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (!(chestplate.getItem() instanceof WarbornArmorItem) || !isChestplateItem(chestplate)) return false;
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

        if (itemBeingMoved.isEmpty()) {
            removeItem(chestplate).ifPresent(removed -> {
                access.set(removed);
                playRemoveOneSound(player);
            });
            return true;
        }

        if (!itemBeingMoved.getItem().canFitInsideContainerItems()) return false;

        int canAdd = getAvailableSpace(chestplate, itemBeingMoved);
        if (canAdd <= 0) return false;

        ItemStack toAdd = itemBeingMoved.copyWithCount(Math.min(canAdd, itemBeingMoved.getCount()));
        int added = addItems(chestplate, toAdd);

        if (added > 0) {
            itemBeingMoved.shrink(added);
            access.set(itemBeingMoved.isEmpty() ? ItemStack.EMPTY : itemBeingMoved);
            playInsertSound(player);
            return true;
        }

        return false;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (!isChestplateItem(stack)) return Optional.empty();

        List<ItemStack> contents = getContents(stack).limit(MAX_SLOTS).toList();
        NonNullList<ItemStack> result = NonNullList.create();
        result.addAll(contents);

        int totalWeight = contents.stream().mapToInt(ItemStack::getCount).sum();
        return Optional.of(new BundleTooltip(result, totalWeight));
    }



    private static int getTotalStoredItems(ItemStack chestplate) {
        CompoundTag tag = chestplate.getTag();
        if (tag == null || !tag.contains("Items")) return 0;

        int total = 0;
        ListTag items = tag.getList("Items", 10);
        for (int i = 0; i < items.size(); i++) {
            total += ItemStack.of(items.getCompound(i)).getCount();
        }
        return total;
    }

    public static int getAvailableSpace(ItemStack chestplate, ItemStack stackToAdd) {
        if (isArmor(stackToAdd)) return 0;
        CompoundTag tag = chestplate.getOrCreateTag();
        ListTag items = tag.getList("Items", 10);
        int totalStored = 0;
        int matchingStackSpace = 0;

        for (int i = 0; i < items.size(); i++) {
            ItemStack existing = ItemStack.of(items.getCompound(i));
            totalStored += existing.getCount();
            if (ItemStack.isSameItemSameTags(existing, stackToAdd)) {
                matchingStackSpace += MAX_STACK_SIZE - existing.getCount();
            }
        }

        int totalSpace = MAX_SLOTS * MAX_STACK_SIZE;
        int remainingSpace = totalSpace - totalStored;

        if (items.size() < MAX_SLOTS) {
            remainingSpace = Math.max(remainingSpace, MAX_STACK_SIZE);
        }

        return Math.min(remainingSpace, stackToAdd.getCount());
    }

    public static int addItems(ItemStack chestplate, ItemStack stackToAdd) {
        if (stackToAdd.isEmpty() || !stackToAdd.getItem().canFitInsideContainerItems()) return 0;
        if (isArmor(stackToAdd)) return 0;

        CompoundTag tag = chestplate.getOrCreateTag();
        ListTag items = tag.getList("Items", 10);

        if (items.size() >= MAX_SLOTS &&
                !items.stream()
                        .map(CompoundTag.class::cast)
                        .map(ItemStack::of)
                        .anyMatch(s -> ItemStack.isSameItemSameTags(s, stackToAdd))) {
            return 0;
        }

        int remaining = stackToAdd.getCount();
        int added = 0;

        for (int i = 0; i < items.size() && remaining > 0; i++) {
            CompoundTag itemTag = items.getCompound(i);
            ItemStack existing = ItemStack.of(itemTag);

            if (ItemStack.isSameItemSameTags(existing, stackToAdd)) {
                int spaceInStack = MAX_STACK_SIZE - existing.getCount();
                int toAdd = Math.min(remaining, spaceInStack);

                if (toAdd > 0) {
                    existing.grow(toAdd);
                    // Update NBT
                    CompoundTag updatedTag = new CompoundTag();
                    updatedTag.putString("id", BuiltInRegistries.ITEM.getKey(existing.getItem()).toString());
                    updatedTag.putInt("Count", existing.getCount());
                    if (existing.hasTag()) {
                        updatedTag.put("tag", existing.getTag());
                    }
                    items.set(i, updatedTag);
                    added += toAdd;
                    remaining -= toAdd;
                }
            }
        }

        while (remaining > 0 && items.size() < MAX_SLOTS) {
            int toAdd = Math.min(remaining, MAX_STACK_SIZE);
            ItemStack newStack = stackToAdd.copyWithCount(toAdd);

            CompoundTag newTag = new CompoundTag();
            newTag.putString("id", BuiltInRegistries.ITEM.getKey(newStack.getItem()).toString());
            newTag.putInt("Count", newStack.getCount());
            if (newStack.hasTag()) {
                newTag.put("tag", newStack.getTag());
            }

            items.add(newTag);
            added += toAdd;
            remaining -= toAdd;
        }

        if (added > 0) {
            tag.put("Items", items);
        }

        return added;
    }

    public static Optional<ItemStack> removeItem(ItemStack chestplate) {
        if (!isChestplateItem(chestplate)) return Optional.empty();

        CompoundTag tag = chestplate.getOrCreateTag();
        if (!tag.contains("Items")) return Optional.empty();

        ListTag items = tag.getList("Items", 10);
        if (items.isEmpty()) return Optional.empty();

        CompoundTag itemTag = items.getCompound(items.size() - 1);
        ItemStack removed = ItemStack.of(itemTag);
        items.remove(items.size() - 1);

        if (items.isEmpty()) {
            chestplate.removeTagKey("Items");
        } else {
            tag.put("Items", items);
        }

        return Optional.of(removed);
    }

    private static Stream<ItemStack> getContents(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("Items")) {
            return Stream.empty();
        }
        ListTag items = tag.getList("Items", 10);
        return items.stream()
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .map(ItemStack::of);
    }

    public static boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return isChestplateItem(stack) && getContents(stack).findAny().isPresent();
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        if (!isChestplateItem(stack)) return super.getBarWidth(stack);
        long count = getContents(stack).count();
        return Math.min(13, 1 + (int)(12 * ((double)count / MAX_SLOTS)));
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return isChestplateItem(stack) ? BAR_COLOR : super.getBarColor(stack);
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    public String getArmorType() {
        return this.armorType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (isChestplateItem(stack) && player.isShiftKeyDown()) {
            if (!level.isClientSide && stack.getTag() != null && stack.getTag().contains("Items")) {
                ListTag items = stack.getTag().getList("Items", 10);
                for (int i = 0; i < items.size(); i++) {
                    player.drop(ItemStack.of(items.getCompound(i)), true);
                }
                stack.removeTagKey("Items");
                playDropContentsSound(player);
                return InteractionResultHolder.success(stack);
            }

            if (level.isClientSide && isBackpackItem(stack)) {
                ModNetworking.openBackpack(stack);
            }
        }

        EquipmentSlot slot = this.getEquipmentSlot();
        if (player.getItemBySlot(slot).isEmpty()) {
            player.setItemSlot(slot, stack.copy());
            stack.setCount(0);
            return InteractionResultHolder.success(stack);
        }

        return super.use(level, player, hand);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
                                                                   EquipmentSlot slot, HumanoidModel<?> original) {
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
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (isBackpackItem(stack)) {
            return new BackpackCapabilityProvider(stack);
        } else if (isChestplateItem(stack)) {
            return new ChestplateBundleCapabilityProvider(stack);
        }
        return super.initCapabilities(stack, nbt);
    }

    public static boolean isBackpackItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id != null && id.getPath().toLowerCase().contains("backpack");
    }
}