package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.client.renderer.armor.WarbornGenericArmorRenderer;
import com.raiiiden.warborn.common.object.capability.ChestplateBundleCapabilityProvider;
import com.raiiiden.warborn.common.object.capability.ChestplateBundleHandler;
import com.raiiiden.warborn.common.object.capability.NVGBatteryStorage;
import com.raiiiden.warborn.common.util.BatteryDisplayCache;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.raiiiden.warborn.client.event.ClientKeyEvents.HAS_TOGGLE_TAG;

public class WBArmorItem extends ArmorItem implements GeoItem, ICurioItem {
    public static final String TAG_GOGGLE = "goggle";
    public static final String TAG_NVG = "nvg";
    public static final String TAG_SIMPLE_NVG = "simple_nvg";
    public static final String TAG_THERMAL = "thermal";
    public static final String TAG_DIGITAL = "digital";
    public static final TagKey<Item> PLATE_COMPATIBLE = ItemTags.create(new ResourceLocation("warborn", "plate_compatible"));
    // Helmets with this tag have goggle mounting points and can accept goggle + battery items
    public static final TagKey<Item> CAN_HAVE_GOGGLES = TagKey.create(Registries.ITEM, new ResourceLocation("fracturepoint", "can_have_goggles"));
    private static final int MAX_STACK_SIZE = 100;
    private static final int MAX_SLOTS = 4;
    public static final String TAG_INSERTED_BATTERY = "InsertedNVGBattery";
    public static final String TAG_INSERTED_GOGGLES  = "InsertedGoggles";
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String armorType;

    public WBArmorItem(ArmorMaterial armorMaterial, Type type, Item.Properties properties, String armorType) {
        super(armorMaterial, type, properties.defaultDurability(armorMaterial.getDurabilityForType(type)));
        this.armorType = armorType;
    }

    public static boolean isChestplateItem(ItemStack stack) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id.getPath().toLowerCase().contains("chestplate");
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
        }).filter(stack -> !stack.isEmpty());
    }

    private static Stream<ItemStack> getContents(ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.ITEM_HANDLER)
                .map(handler -> IntStream.range(0, handler.getSlots())
                        .mapToObj(handler::getStackInSlot)
                        .filter(s -> !s.isEmpty()))
                .orElse(Stream.empty());
    }

    public static boolean isArmor(ItemStack stack) {
        return stack.getItem() instanceof net.minecraft.world.item.ArmorItem;
    }

    /** Returns true if this helmet currently has goggles installed in the goggle slot. */
    public static boolean hasVisionCapability(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof net.minecraft.world.item.ArmorItem)) return false;
        if (((net.minecraft.world.item.ArmorItem) stack.getItem()).getType() != Type.HELMET) return false;
        return hasInsertedGoggles(stack);
    }

    /** Returns true if goggles are installed and their visionType matches the given tag. */
    public static boolean hasVisionMode(ItemStack stack, String visionTag) {
        ItemStack goggles = getInsertedGoggles(stack);
        if (goggles.isEmpty()) return false;
        return goggles.getItem() instanceof GogglesItem g && g.getVisionType().equals(visionTag);
    }

    public static boolean isPlateCompatible(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof net.minecraft.world.item.ArmorItem)) return false;
        if (((net.minecraft.world.item.ArmorItem) stack.getItem()).getType() != Type.CHESTPLATE) return false;

        return stack.is(PLATE_COMPATIBLE);
    }

    // ── Goggle Slot Helpers ──────────────────────────────────────────────────────

    /** Returns true if this helmet has goggle mount points (static tag). */
    public static boolean canHaveGoggles(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.is(CAN_HAVE_GOGGLES);
    }

    public static ItemStack getInsertedGoggles(ItemStack helmet) {
        CompoundTag tag = helmet.getTag();
        if (tag == null || !tag.contains(TAG_INSERTED_GOGGLES)) return ItemStack.EMPTY;
        return ItemStack.of(tag.getCompound(TAG_INSERTED_GOGGLES));
    }

    public static void setInsertedGoggles(ItemStack helmet, ItemStack goggles) {
        if (goggles.isEmpty()) {
            CompoundTag tag = helmet.getTag();
            if (tag != null) tag.remove(TAG_INSERTED_GOGGLES);
        } else {
            CompoundTag gogglesTag = new CompoundTag();
            goggles.save(gogglesTag);
            helmet.getOrCreateTag().put(TAG_INSERTED_GOGGLES, gogglesTag);
        }
    }

    public static boolean hasInsertedGoggles(ItemStack helmet) {
        CompoundTag tag = helmet.getTag();
        return tag != null && tag.contains(TAG_INSERTED_GOGGLES);
    }

    // ── NVG Battery Slot Helpers ────────────────────────────────────────────────

    public static ItemStack getInsertedBattery(ItemStack helmet) {
        CompoundTag tag = helmet.getTag();
        if (tag == null || !tag.contains(TAG_INSERTED_BATTERY)) return ItemStack.EMPTY;
        return ItemStack.of(tag.getCompound(TAG_INSERTED_BATTERY));
    }

    public static void setInsertedBattery(ItemStack helmet, ItemStack battery) {
        if (battery.isEmpty()) {
            CompoundTag tag = helmet.getTag();
            if (tag != null) tag.remove(TAG_INSERTED_BATTERY);
        } else {
            CompoundTag batteryTag = new CompoundTag();
            battery.save(batteryTag);
            helmet.getOrCreateTag().put(TAG_INSERTED_BATTERY, batteryTag);
        }
    }

    public static boolean hasInsertedBattery(ItemStack helmet) {
        CompoundTag tag = helmet.getTag();
        return tag != null && tag.contains(TAG_INSERTED_BATTERY);
    }

    // ────────────────────────────────────────────────────────────────────────────

    private static boolean isAnimatedHelmet(WBArmorItem item) {
        Set<String> animatedHelmets = Set.of("insurgency_commander", "beta7_nvg", "beta7_nvg_ash", "beta7_nvg_slate", "nato_sqad_leader", "nato_sqad_leader_woodland", "nato_ukr", "nato_ukr_woodland", "killa", "tagilla");
        return animatedHelmets.contains(item.getArmorType());
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return true;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack heldStack, Slot slot, ClickAction action, Player player) {
        if (!(heldStack.getItem() instanceof WBArmorItem)) return false;
        if (action != ClickAction.SECONDARY) return false;

        // ── NVG helmet: goggle + battery slot interaction ────────────────────
        if (canHaveGoggles(heldStack)) {
            ItemStack slotItem = slot.getItem();
            if (slotItem.isEmpty()) {
                if (player.isShiftKeyDown()) {
                    if (hasInsertedBattery(heldStack)) {
                        slot.set(getInsertedBattery(heldStack));
                        setInsertedBattery(heldStack, ItemStack.EMPTY);
                        playRemoveOneSound(player);
                        return true;
                    }
                    if (hasInsertedGoggles(heldStack)) {
                        slot.set(getInsertedGoggles(heldStack));
                        setInsertedGoggles(heldStack, ItemStack.EMPTY);
                        playRemoveOneSound(player);
                        return true;
                    }
                }
            } else if (slotItem.getItem() instanceof GogglesItem && !hasInsertedGoggles(heldStack)) {
                setInsertedGoggles(heldStack, slotItem.split(1));
                playInsertSound(player);
                return true;
            } else if (slotItem.getItem() instanceof NVGBatteryItem && !hasInsertedBattery(heldStack)) {
                setInsertedBattery(heldStack, slotItem.split(1));
                playInsertSound(player);
                return true;
            }
        }

        if (!isChestplateItem(heldStack)) return false;
        // ─ chestplate bundle interaction below ───────────────────────────────
        ItemStack chestplate = heldStack;

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
    public boolean overrideOtherStackedOnMe(ItemStack slotStack, ItemStack carried, Slot slot, ClickAction action, Player player, SlotAccess access) {
        if (!(slotStack.getItem() instanceof WBArmorItem)) return false;
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;

        // ── NVG helmet: goggle + battery slot interaction ────────────────────
        if (canHaveGoggles(slotStack)) {
            if (carried.isEmpty()) {
                if (hasInsertedBattery(slotStack)) {
                    access.set(getInsertedBattery(slotStack));
                    setInsertedBattery(slotStack, ItemStack.EMPTY);
                    playRemoveOneSound(player);
                    return true;
                }
                if (hasInsertedGoggles(slotStack)) {
                    access.set(getInsertedGoggles(slotStack));
                    setInsertedGoggles(slotStack, ItemStack.EMPTY);
                    playRemoveOneSound(player);
                    return true;
                }
            } else if (carried.getItem() instanceof GogglesItem && !hasInsertedGoggles(slotStack)) {
                setInsertedGoggles(slotStack, carried.split(1));
                playInsertSound(player);
                return true;
            } else if (carried.getItem() instanceof NVGBatteryItem && !hasInsertedBattery(slotStack)) {
                setInsertedBattery(slotStack, carried.split(1));
                playInsertSound(player);
                return true;
            }
        }

        if (!isChestplateItem(slotStack)) return false;
        // ─ chestplate bundle interaction below ───────────────────────────────
        ItemStack chestplate = slotStack;
        ItemStack itemBeingMoved = carried;

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
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (canHaveGoggles(stack)) {
            ItemStack goggles = getInsertedGoggles(stack);
            if (goggles.isEmpty()) {
                tooltip.add(Component.literal("Goggles: Not installed").withStyle(ChatFormatting.GRAY));
            } else if (goggles.getItem() instanceof GogglesItem g) {
                String typeName = switch (g.getVisionType()) {
                    case TAG_NVG -> "Night Vision";
                    case TAG_SIMPLE_NVG -> "Simple Night Vision";
                    case TAG_THERMAL -> "Thermal Vision";
                    case TAG_DIGITAL -> "Digital Vision";
                    default -> g.getVisionType();
                };
                tooltip.add(Component.literal("Goggles: " + typeName).withStyle(ChatFormatting.GREEN));
            }
            ItemStack battery = getInsertedBattery(stack);
            if (battery.isEmpty()) {
                tooltip.add(Component.literal("Battery: Not installed").withStyle(ChatFormatting.GRAY));
            } else {
                int energy = BatteryDisplayCache.isActive()
                        ? BatteryDisplayCache.get()
                        : NVGBatteryStorage.readEnergy(battery);
                int max = NVGBatteryStorage.MAX_CAPACITY;
                ChatFormatting color = energy > max / 4 ? ChatFormatting.GREEN : ChatFormatting.RED;
                tooltip.add(Component.literal("Battery: " + energy + " / " + max + " RF").withStyle(color));
            }
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (canHaveGoggles(stack)) {
            // Always show exactly 2 slots (goggles + battery), even when empty
            return Optional.of(new HelmetSlotTooltip(getInsertedGoggles(stack), getInsertedBattery(stack)));
        }

        if (!isChestplateItem(stack)) return Optional.empty();

        List<ItemStack> contents = getContents(stack).limit(MAX_SLOTS).toList();
        NonNullList<ItemStack> result = NonNullList.create();
        result.addAll(contents);

        int totalWeight = contents.stream().mapToInt(ItemStack::getCount).sum();
        return Optional.of(new BundleTooltip(result, totalWeight));
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

        EquipmentSlot slot = this.getEquipmentSlot();
        ItemStack equipped = player.getItemBySlot(slot);

        if (equipped.isEmpty()) {
            player.setItemSlot(slot, stack.split(1));
            return InteractionResultHolder.success(player.getItemInHand(hand));
        }

        return super.use(level, player, hand);
    }

    private void playDropContentsSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private WarbornGenericArmorRenderer renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
                                                                   EquipmentSlot slot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new WarbornGenericArmorRenderer(WBArmorItem.this);
                }
                this.renderer.prepForRender(entity, stack, slot, original);
                this.renderer.applySlimArmScaleIfNeeded(entity); // <-- slim arm fix
                return this.renderer;
            }
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "helmet_toggle", 0, state -> {
            ItemStack stack = state.getData(DataTickets.ITEMSTACK);
            Entity rawEntity = state.getData(DataTickets.ENTITY);

            if (!(rawEntity instanceof LivingEntity entity) || entity instanceof ArmorStand) {
                return PlayState.STOP;
            }

            if (stack == null || !stack.hasTag() || !stack.is(HAS_TOGGLE_TAG)) {
                return PlayState.STOP;
            }

            boolean open = stack.getOrCreateTag().getBoolean("helmet_top_open");
            String animationName = open ? "helmet_open" : "helmet_closed";

            state.setAnimation(RawAnimation.begin().then(animationName, Animation.LoopType.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }));
    }

    public void setTopOpen(ItemStack stack, boolean open) {
        stack.getOrCreateTag().putBoolean("helmet_top_open", open);
    }

    public boolean isTopOpen(ItemStack stack) {
        return stack.getOrCreateTag().getBoolean("helmet_top_open");
    }

    @Override
    public void curioTick(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!this.getArmorType().contains("shoulderpads")) return;

        var armorAttr = livingEntity.getAttributes().getInstance(Attributes.ARMOR);
        if (armorAttr == null) return;

        UUID modifierId = UUID.nameUUIDFromBytes(("fracturepoint:shoulderpads_" + livingEntity.getUUID() + index).getBytes());

        // Make sure no duplicate modifier exists
        if (armorAttr.getModifier(modifierId) == null) {
            int defense = this.getMaterial().getDefenseForType(this.getType());
            armorAttr.addTransientModifier(new AttributeModifier(
                    modifierId, "fracturepoint:shoulderpads", defense, AttributeModifier.Operation.ADDITION));
        }
    }
    @Override
    public void onUnequip(String identifier, int index, LivingEntity livingEntity, ItemStack stack) {
        if (!this.getArmorType().contains("shoulderpads")) return;

        var armorAttr = livingEntity.getAttributes().getInstance(Attributes.ARMOR);
        if (armorAttr == null) return;

        UUID modifierId = UUID.nameUUIDFromBytes(("fracturepoint:shoulderpads_" + livingEntity.getUUID() + index).getBytes());

        if (armorAttr.getModifier(modifierId) != null) {
            armorAttr.removeModifier(modifierId);
        }
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (isChestplateItem(stack)) {
            return new ChestplateBundleCapabilityProvider(stack);
        }
        return super.initCapabilities(stack, nbt);
    }
}