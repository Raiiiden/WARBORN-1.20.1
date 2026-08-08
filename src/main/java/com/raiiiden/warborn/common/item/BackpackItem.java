package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.client.renderer.armor.WarbornBackpackRenderer;
import com.raiiiden.warborn.client.renderer.item.WarbornArmorItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
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
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
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
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class BackpackItem extends ArmorItem implements GeoItem, ICurioItem {

    public static final String TAG_UPGRADE = "backpack_upgrade";

    // ── Upgrade NBT helpers ──────────────────────────────────────────────────

    public static ItemStack getInsertedUpgrade(ItemStack backpack) {
        CompoundTag tag = backpack.getTag();
        if (tag == null || !tag.contains(TAG_UPGRADE)) return ItemStack.EMPTY;
        return ItemStack.of(tag.getCompound(TAG_UPGRADE));
    }

    public static void setInsertedUpgrade(ItemStack backpack, ItemStack upgrade) {
        if (upgrade.isEmpty()) {
            if (backpack.hasTag()) backpack.getTag().remove(TAG_UPGRADE);
        } else {
            backpack.getOrCreateTag().put(TAG_UPGRADE, upgrade.save(new CompoundTag()));
        }
    }

    public static int getTier(ItemStack backpack) {
        ItemStack upgrade = getInsertedUpgrade(backpack);
        if (upgrade.getItem() instanceof BackpackUpgradeItem u) return u.getTier();
        return 1;
    }

    // Number of storage slots accessible at this tier (9 / 27 / 54).
    public static int getSlotsForTier(int tier) {
        return switch (tier) { case 2 -> 27; case 3 -> 54; default -> 9; };
    }

    // Number of visible rows shown in the GUI at this tier (1 / 3 / 3+scroll).
    public static int getVisibleRowsForTier(int tier) {
        return tier == 1 ? 1 : 3;
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final String armorType;
    private final String armorTexture;

    public BackpackItem(ArmorMaterial material, Type type, Properties properties, String armorType) {
        this(material, type, properties, armorType, armorType);
    }

    public BackpackItem(ArmorMaterial material, Type type, Properties properties, String armorType,
                        String armorTexture) {
        super(material, type, properties);
        this.armorType = armorType;
        this.armorTexture = armorTexture;
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
                if (!id.equals("backpack")) return;

                for (int i = 0; i < handler.getSlots(); i++) {
                    if (handler.getStacks().getStackInSlot(i).isEmpty()) {
                        handler.getStacks().setStackInSlot(i, stack.copy());
                        stack.shrink(1);
                        player.displayClientMessage(
                                Component.literal("Equipped backpack to Curios Backpack slot")
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

    // ── Bundle right-click mechanic ──────────────────────────────────────────
    // Cursor = upgrade → click on backpack slot: insert upgrade
    @Override
    public boolean overrideOtherStackedOnMe(ItemStack backpack, ItemStack cursor, Slot slot,
                                            ClickAction action, Player player,
                                            SlotAccess access) {
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) return false;
        if (!(cursor.getItem() instanceof BackpackUpgradeItem incoming)) return false;

        int currentTier = getTier(backpack);
        if (incoming.getTier() <= currentTier) return false; // no downgrade / same tier

        ItemStack oldUpgrade = getInsertedUpgrade(backpack);
        setInsertedUpgrade(backpack, cursor.copyWithCount(1));
        access.set(ItemStack.EMPTY); // consume cursor
        if (!oldUpgrade.isEmpty()) {
            // Return old upgrade to cursor; if cursor now has something, drop to inventory
            if (access.get().isEmpty()) {
                access.set(oldUpgrade);
            } else {
                player.getInventory().placeItemBackInInventory(oldUpgrade);
            }
        }
        playInsertSound(player);
        return true;
    }

    // Cursor = backpack → right-click on any slot: eject upgrade
    @Override
    public boolean overrideStackedOnOther(ItemStack backpack, Slot slot, ClickAction action, Player player) {
        if (action != ClickAction.SECONDARY) return false;

        ItemStack upgrade = getInsertedUpgrade(backpack);
        if (upgrade.isEmpty()) return false;

        // Block downgrade if the hidden slots have items
        int newTier = 1;
        if (upgrade.getItem() instanceof BackpackUpgradeItem u) newTier = u.getTier() == 3 ? 2 : 1;
        int maxAllowed = getSlotsForTier(newTier);
        if (hasItemsInRange(backpack, maxAllowed)) {
            player.displayClientMessage(
                    Component.literal("Clear the extra slots before removing this upgrade.")
                            .withStyle(ChatFormatting.RED), true);
            return true;
        }

        ItemStack ejected = upgrade.copy();
        setInsertedUpgrade(backpack, ItemStack.EMPTY);
        if (slot.mayPlace(ejected) && slot.getItem().isEmpty()) {
            slot.set(ejected);
        } else {
            player.getInventory().placeItemBackInInventory(ejected);
        }
        playRemoveOneSound(player);
        return true;
    }

    // Returns true if any slot at index >= {@code fromSlot} in this backpack's handler has an item.
    private static boolean hasItemsInRange(ItemStack backpack, int fromSlot) {
        var capOpt = backpack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (capOpt.isEmpty()) return false;
        var handler = capOpt.get();
        for (int i = fromSlot; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new BackpackCapabilityProvider(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, level, components, flag);
        int tier = getTier(stack);
        int slots = getSlotsForTier(tier);
        components.add(Component.literal("Tier " + tier + " — " + slots + " slots").withStyle(ChatFormatting.GRAY));
        ItemStack upgrade = getInsertedUpgrade(stack);
        if (!upgrade.isEmpty()) {
            components.add(Component.literal("Upgrade: ").withStyle(ChatFormatting.GRAY)
                    .append(upgrade.getHoverName().copy().withStyle(ChatFormatting.YELLOW)));
        } else {
            components.add(Component.literal("No upgrade installed").withStyle(ChatFormatting.DARK_GRAY));
        }
        components.add(Component.literal("Right Click: Open Backpack").withStyle(ChatFormatting.GRAY));
        components.add(Component.literal("Shift+Right Click: Equip to Curios").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public Optional<net.minecraft.world.inventory.tooltip.TooltipComponent> getTooltipImage(ItemStack stack) {
        var capOpt = stack.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (capOpt.isEmpty()) return Optional.empty();
        var handler = capOpt.get();

        List<ItemStack> items = new ArrayList<>(handler.getSlots());
        for (int i = 0; i < handler.getSlots(); i++) {
            items.add(handler.getStackInSlot(i));
        }
        return Optional.of(new BackpackTooltipData(items, getTier(stack)));
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
            private WarbornArmorItemRenderer itemRenderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entity, ItemStack stack,
                                                                   EquipmentSlot slot, HumanoidModel<?> original) {
                if (this.renderer == null) {
                    this.renderer = new WarbornBackpackRenderer(BackpackItem.this);
                }
                this.renderer.prepForRender(entity, stack, slot, original);
                return this.renderer;
            }

            // Only reached by items whose model json is builtin/entity; the rest keep their flat sprite.
            @Override
            public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.itemRenderer == null) {
                    this.itemRenderer = new WarbornArmorItemRenderer();
                }
                return this.itemRenderer;
            }
        });
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(SlotContext slotContext, UUID uuid,
                                                                        ItemStack stack) {
        return CurioArmorAttributes.forSlot(this, uuid, slotContext.identifier());
    }

    // Curios gear pays out through its curios slot only, so it doesn't stack with the vanilla armor slot.
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (CurioArmorAttributes.isCuriosGear(stack)) return ImmutableMultimap.of();
        return super.getAttributeModifiers(slot, stack);
    }

    @Override
    public ICurio.SoundInfo getEquipSound(SlotContext slotContext, ItemStack stack) {
        return new ICurio.SoundInfo(getMaterial().getEquipSound(), 1.0F, 1.0F);
    }

    public String getArmorType() {
        return this.armorType;
    }

    public String getArmorTexture() {
        return this.armorTexture;
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
