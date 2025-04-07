package com.raiiiden.warborn.item;

import com.raiiiden.warborn.client.renderer.armor.WarbornGenericArmorRenderer;
import com.raiiiden.warborn.common.network.ModNetworking;
import com.raiiiden.warborn.common.object.capability.BackpackCapabilityProvider;
import com.raiiiden.warborn.common.object.capability.ChestplateBundleCapabilityProvider;
import com.raiiiden.warborn.common.object.capability.ChestplateBundleHandler;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
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

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WarbornArmorItem extends ArmorItem implements GeoItem, ICurioItem {
    private static final int MAX_STACK_SIZE = 100;
    private static final int MAX_SLOTS = 4;
    private static final int BAR_COLOR = Mth.color(0.4F, 0.4F, 1.0F);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String armorType;

    // Vision-related tags
    public static final String TAG_GOGGLE = "goggle";         // Base tag for all vision-capable helmets
    public static final String TAG_NVG = "nvg";               // Night vision
    public static final String TAG_SIMPLE_NVG = "simple_nvg"; // Simple night vision
    public static final String TAG_THERMAL = "thermal";       // Thermal vision
    public static final String TAG_DIGITAL = "digital";       // Digital vision

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
            }
        } else {
            if (!isValidInsert(slotStack)) return false;

            chestplate.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                ItemStack remaining = slotStack.copy();

                for (int i = 0; i < handler.getSlots(); i++) {
                    if (remaining.isEmpty()) break;

                    ItemStack leftover = handler.insertItem(i, remaining.copy(), false);
                    int inserted = remaining.getCount() - leftover.getCount();
                    remaining.shrink(inserted);

                    if (inserted > 0) {
                        playInsertSound(player);
                        if (handler instanceof ChestplateBundleHandler cbh) cbh.saveToItem(chestplate);
                    }
                }

                slot.set(remaining.isEmpty() ? ItemStack.EMPTY : remaining);
            });

            return true;
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

        if (!isValidInsert(itemBeingMoved)) return false;

        chestplate.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            ItemStack remaining = itemBeingMoved.copy();

            for (int i = 0; i < handler.getSlots(); i++) {
                if (remaining.isEmpty()) break;

                ItemStack leftover = handler.insertItem(i, remaining.copy(), false);
                int inserted = remaining.getCount() - leftover.getCount();
                remaining.shrink(inserted);

                if (inserted > 0) {
                    playInsertSound(player);
                    if (handler instanceof ChestplateBundleHandler cbh) cbh.saveToItem(chestplate);
                }
            }

            access.set(remaining.isEmpty() ? ItemStack.EMPTY : remaining);
        });

        return true;
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

    public static int getTotalStoredItems(ItemStack chestplate) {
        return chestplate.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
            int total = 0;
            for (int i = 0; i < handler.getSlots(); i++) {
                total += handler.getStackInSlot(i).getCount();
            }
            return total;
        }).orElse(0);
    }

    public static boolean isValidInsert(ItemStack stack) {
        return stack.getItem().canFitInsideContainerItems() && !isArmor(stack);
    }

    public static Optional<ItemStack> removeItem(ItemStack chestplate) {
        if (!isChestplateItem(chestplate)) return Optional.empty();

        return chestplate.getCapability(ForgeCapabilities.ITEM_HANDLER).map(handler -> {
            for (int i = handler.getSlots() - 1; i >= 0; i--) {
                ItemStack inSlot = handler.getStackInSlot(i);
                if (!inSlot.isEmpty()) {
                    ItemStack removed = handler.extractItem(i, inSlot.getCount(), false);

                    if (handler instanceof ChestplateBundleHandler cbh) {
                        cbh.saveToItem(chestplate);
                    }

                    return removed;
                }
            }
            return ItemStack.EMPTY;
        }).filter(stack -> !stack.isEmpty()).map(Optional::of).orElse(Optional.empty());
    }

    private static Stream<ItemStack> getContents(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(handler -> IntStream.range(0, handler.getSlots())
                        .mapToObj(handler::getStackInSlot)
                        .filter(s -> !s.isEmpty()))
                .orElse(Stream.empty());
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

        int total = getContents(stack).mapToInt(ItemStack::getCount).sum();
        int max = MAX_SLOTS * MAX_STACK_SIZE;

        return Math.min(13, 1 + (int)(12 * ((double) total / max)));
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

    // Apply tags depending on the helmet type when an item is created
    @Override
    public void onCraftedBy(ItemStack stack, Level level, Player player) {
        super.onCraftedBy(stack, level, player);
        
        // NOTE: This method is kept for backward compatibility.
        // Vision capabilities are now primarily defined through item tags in the data folder:
        // - data/warborn/tags/items/has_goggle.json
        // - data/warborn/tags/items/has_nvg.json
        // - data/warborn/tags/items/has_simple_nvg.json
        // - data/warborn/tags/items/has_thermal.json
        // - data/warborn/tags/items/has_digital.json
        
        // Only applies to helmet items
        if (this.getType() != Type.HELMET) return;
        
        CompoundTag tag = stack.getOrCreateTag();
        
        // Set basic vision capabilities based on the armor type
        if (this.armorType.contains("tactical") || this.armorType.contains("operator")) {
            // Tactical/Operator helmets have all vision types
            tag.putBoolean(TAG_GOGGLE, true);
            tag.putBoolean(TAG_NVG, true);
            tag.putBoolean(TAG_SIMPLE_NVG, true);
            tag.putBoolean(TAG_THERMAL, true);
            tag.putBoolean(TAG_DIGITAL, true);
        } else if (this.armorType.contains("combat")) {
            // Combat helmets have basic night vision
            tag.putBoolean(TAG_GOGGLE, true);
            tag.putBoolean(TAG_NVG, true);
            tag.putBoolean(TAG_SIMPLE_NVG, true);
        } else if (this.armorType.contains("scout")) {
            // Scout helmets have thermal vision
            tag.putBoolean(TAG_GOGGLE, true);
            tag.putBoolean(TAG_THERMAL, true);
        }
    }
    
    /**
     * Checks if a helmet has vision capabilities (has the goggle tag)
     */
    public static boolean hasVisionCapability(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem)) return false;
        if (((ArmorItem)stack.getItem()).getType() != Type.HELMET) return false;
        
        // First check NBT tags (for backwards compatibility)
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_GOGGLE)) {
            return true;
        }
        
        // Then check data-defined item tags
        ResourceLocation goggleTagId = new ResourceLocation("warborn", "has_" + TAG_GOGGLE);
        
        // Also check if the helmet has any specific vision tag
        ResourceLocation nvgTagId = new ResourceLocation("warborn", "has_" + TAG_NVG);
        ResourceLocation simpleNvgTagId = new ResourceLocation("warborn", "has_" + TAG_SIMPLE_NVG);
        ResourceLocation thermalTagId = new ResourceLocation("warborn", "has_" + TAG_THERMAL);
        ResourceLocation digitalTagId = new ResourceLocation("warborn", "has_" + TAG_DIGITAL);
        
        return stack.is(TagKey.create(Registries.ITEM, goggleTagId)) ||
               stack.is(TagKey.create(Registries.ITEM, nvgTagId)) ||
               stack.is(TagKey.create(Registries.ITEM, simpleNvgTagId)) ||
               stack.is(TagKey.create(Registries.ITEM, thermalTagId)) ||
               stack.is(TagKey.create(Registries.ITEM, digitalTagId));
    }
    
    /**
     * Checks if the helmet has a specific vision mode
     */
    public static boolean hasVisionMode(ItemStack stack, String visionTag) {
        if (!hasVisionCapability(stack)) return false;
        
        // First check NBT tags (for backwards compatibility)
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(visionTag)) {
            return true;
        }
        
        // Then check data-defined item tags
        if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem)) return false;
        
        ResourceLocation tagId = new ResourceLocation("warborn", "has_" + visionTag);
        return stack.is(TagKey.create(Registries.ITEM, tagId));
    }
    
    /**
     * Add a specific vision capability to a helmet
     */
    public static void addVisionCapability(ItemStack stack, String visionTag) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem)) return;
        if (((ArmorItem)stack.getItem()).getType() != Type.HELMET) return;
        
        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean(TAG_GOGGLE, true); // Base tag
        tag.putBoolean(visionTag, true);  // Specific vision type
    }
}