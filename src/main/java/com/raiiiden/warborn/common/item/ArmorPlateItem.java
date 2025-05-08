package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.renderer.item.WarbornPlateRenderer;
import com.raiiiden.warborn.client.sound.WarbornClientSounds;
import com.raiiiden.warborn.common.init.ModRegistry;
import com.raiiiden.warborn.common.init.ModSoundEvents;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import com.raiiiden.warborn.common.util.PlateTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.Map;


public class ArmorPlateItem extends Item implements GeoItem {

    public static final RawAnimation INSERT_ANIMATION =
            RawAnimation.begin().thenPlay("animation.use");
    public static final RawAnimation IDLE_ANIMATION =
            RawAnimation.begin().thenLoop("animation.idle");
    public static final RawAnimation REMOVE_ANIMATION =
            RawAnimation.begin().thenPlay("animation.remove");
    public static final RawAnimation SWAP_ANIMATION =
            RawAnimation.begin().thenPlay("animation.swap");

    public static final TagKey<Item> PLATE_COMPATIBLE =
            ItemTags.create(new ResourceLocation(WARBORN.MODID, "plate_compatible"));
    private static final Logger LOGGER =
            LogManager.getLogger(WARBORN.MODID + "/ArmorPlateItem");
    public static final String CONTROLLER = "controller";
    public static final String PENDING_INSERT_TAG = "warborn_pending_insert";

    private static final String MSG_PREFIX = "message." + WARBORN.MODID + ".plate.";
    private static final String KEY_MSG_MISSING_CHEST = MSG_PREFIX + "missing_chestplate";
    private static final String KEY_MSG_INCOMPATIBLE_CHEST = MSG_PREFIX + "incompatible_chestplate";
    private static final String KEY_MSG_PLATE_BROKEN = MSG_PREFIX + "is_broken";
    private static final String KEY_MSG_FRONT_INSTALLED = MSG_PREFIX + "front_installed";
    private static final String KEY_MSG_BACK_INSTALLED = MSG_PREFIX + "back_installed";
    private static final String KEY_MSG_SLOTS_FULL = MSG_PREFIX + "slots_full";

    private static final Map<UUID, ArmorPlateSound> activeSounds = new HashMap<>();


    private final ProtectionTier tier;
    private final MaterialType material;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ArmorPlateItem(ProtectionTier tier, MaterialType material, Properties props) {
        super(props.durability(material.getBaseDurability()));
        this.tier = tier;
        this.material = material;
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public static boolean isPlateCompatible(ItemStack chest) {
        return !chest.isEmpty() && chest.is(PLATE_COMPATIBLE);
    }

    public static ItemStack createPlateWithHitsRemaining(ProtectionTier t, MaterialType m, int currentDur) {
        ItemStack stack = new ItemStack(ModRegistry.getPlateItem(t, m));
        int damage = Math.max(0, m.getBaseDurability() - currentDur);
        stack.setDamageValue(damage);
        return stack;
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack stack) {
        return 2;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack held = player.getItemInHand(hand);
        CompoundTag tag = held.getOrCreateTag();

        if (chest.isEmpty()) {
            player.displayClientMessage(Component.translatable(KEY_MSG_MISSING_CHEST).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(held);
        }

        if (!isPlateCompatible(chest)) {
            player.displayClientMessage(Component.translatable(KEY_MSG_INCOMPATIBLE_CHEST).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(held);
        }

        if (tag.getBoolean(PENDING_INSERT_TAG)) {
            return InteractionResultHolder.pass(held);
        }

        AtomicBoolean canInsert = new AtomicBoolean(false);
        chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            if (!cap.hasFrontPlate() || !cap.hasBackPlate()) {
                canInsert.set(true);
            }
        });

        if (!canInsert.get()) {
            player.displayClientMessage(Component.translatable(KEY_MSG_SLOTS_FULL).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(held);
        }

        int maxDur = this.material.getBaseDurability();
        int currentDur = maxDur > 0 ? maxDur - held.getDamageValue() : 0;

        if (currentDur <= 0) {
            player.displayClientMessage(Component.translatable(KEY_MSG_PLATE_BROKEN).withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(held);
        }

        if (level instanceof ServerLevel serverLevel) {
            tag.putBoolean(PENDING_INSERT_TAG, true);
            tag.putInt("warborn_insert_delay", 62);
            tag.putFloat("InsertDurability", currentDur);
            tag.putString("InsertTier", tier.name());
            tag.putString("InsertMaterial", material.getInternalName());

            this.triggerAnim(player, GeoItem.getOrAssignId(held, serverLevel), CONTROLLER, "use");
        }

        if (level.isClientSide) {
            WarbornClientSounds.playArmorInsertSound(player, this);
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

        // Clean up leftover tags if not inserting
        if (!tag.getBoolean(PENDING_INSERT_TAG)) {
            if (tag.contains("inserting")) tag.remove("inserting");
            if (tag.contains("GeckoLibID")) tag.remove("GeckoLibID");
            cancelPendingInsert(tag);
            return;
        }

        if (!selected) {
            cancelPendingInsert(tag);
            return;
        }

        int delay = tag.getInt("warborn_insert_delay") - 1;
        tag.putInt("warborn_insert_delay", delay);
        if (delay > 0) return;

        tag.remove(PENDING_INSERT_TAG);
        tag.remove("warborn_insert_delay");

        ProtectionTier tier;
        MaterialType material;
        float durability;
        try {
            tier = ProtectionTier.valueOf(tag.getString("InsertTier"));
            material = MaterialType.valueOf(tag.getString("InsertMaterial"));
            durability = tag.getFloat("InsertDurability");
        } catch (Exception e) {
            LOGGER.warn("Failed to parse plate insert data: {}", tag);
            cancelPendingInsert(tag);
            return;
        }

        tag.remove("InsertTier");
        tag.remove("InsertMaterial");
        tag.remove("InsertDurability");

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty() || !isPlateCompatible(chest)) return;

        Plate plate = new Plate(tier, material);
        plate.setCurrentDurability(durability);

        chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            boolean inserted = false;

            if (!cap.hasFrontPlate()) {
                cap.insertFrontPlate(plate);
                player.displayClientMessage(Component.translatable(KEY_MSG_FRONT_INSTALLED).withStyle(ChatFormatting.GREEN), true);
                inserted = true;
            } else if (!cap.hasBackPlate()) {
                cap.insertBackPlate(plate);
                player.displayClientMessage(Component.translatable(KEY_MSG_BACK_INSTALLED).withStyle(ChatFormatting.GREEN), true);
                inserted = true;
            } else {
                player.displayClientMessage(Component.translatable(KEY_MSG_SLOTS_FULL).withStyle(ChatFormatting.RED), true);
            }

            if (inserted && !player.isCreative()) {
                stack.shrink(1);
            }
        });
    }
    private void cancelPendingInsert(CompoundTag tag) {
        tag.remove("warborn_pending_insert");
        tag.remove("warborn_insert_delay");
        tag.remove("InsertDurability");
        tag.remove("InsertTier");
        tag.remove("InsertMaterial");
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

            if (state.isCurrentAnimation(INSERT_ANIMATION))
                return PlayState.CONTINUE;

            state.setAnimation(IDLE_ANIMATION);
            return PlayState.CONTINUE;
        })
                .triggerableAnim("use", INSERT_ANIMATION)
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

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        CompoundTag tag = oldStack.getTag();
        if (tag != null && tag.getBoolean("warborn_pending_insert")) {
            cancelPendingInsert(tag);
        }
        return slotChanged || oldStack.getItem() != newStack.getItem();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                List<Component> tip, TooltipFlag flag) {
        PlateTooltip.addPlate(tip, tier, material, stack);
        super.appendHoverText(stack, level, tip, flag);
    }

    public ProtectionTier getTier() {
        return tier;
    }

    public MaterialType getMaterial() {
        return material;
    }

    @OnlyIn(Dist.CLIENT)
    private static class ArmorPlateSound extends AbstractTickableSoundInstance {
        private final Player player;
        private final Item trackedItem;

        public ArmorPlateSound(Player player, Item trackedItem) {
            super(ModSoundEvents.WARBORN_PLATE_INSERT.get(), SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
            this.player = player;
            this.trackedItem = trackedItem;
            this.looping = false;
            this.volume = 1.0f;
            this.pitch = 1.0f;
            this.x = player.getX();
            this.y = player.getY();
            this.z = player.getZ();
        }

        @Override
        public void tick() {
            if (!player.isAlive() || !isHoldingPlate(player)) {
                this.stop();
                activeSounds.remove(player.getUUID());
            } else {
                this.x = player.getX();
                this.y = player.getY();
                this.z = player.getZ();
            }
        }

        private boolean isHoldingPlate(Player player) {
            return player.getMainHandItem().is(trackedItem) || player.getOffhandItem().is(trackedItem);
        }

        @Override
        public boolean canStartSilent() {
            return false;
        }
    }
}
