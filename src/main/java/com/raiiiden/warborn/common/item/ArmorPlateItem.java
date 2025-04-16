package com.raiiiden.warborn.common.item;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.init.ModRegistry;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArmorPlateItem extends Item {
    private final ProtectionTier tier;
    private final MaterialType material;

    public static final TagKey<Item> PLATE_COMPATIBLE = ItemTags.create(
            new ResourceLocation(WARBORN.MODID, "plate_compatible"));

    public ArmorPlateItem(ProtectionTier tier, MaterialType material, Properties props) {
        super(props.durability(material.getBaseDurability()));
        this.tier = tier;
        this.material = material;
    }

    public static boolean isPlateCompatible(ItemStack chestplate) {
        return !chestplate.isEmpty() && chestplate.is(PLATE_COMPATIBLE);
    }

    public static ItemStack createPlateWithHitsRemaining(ProtectionTier tier, MaterialType material, int currentDurability) {
        ItemStack stack = new ItemStack(ModRegistry.getPlateItem(tier, material));
        int damage = Math.max(0, material.getBaseDurability() - currentDurability);
        stack.setDamageValue(damage);
        return stack;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        
        if (chest.isEmpty()) {
            player.displayClientMessage(Component.literal("You must be wearing a chestplate.").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }
        
        if (!isPlateCompatible(chest)) {
            player.displayClientMessage(
                Component.literal("This armor doesn't support armor plates.").withStyle(ChatFormatting.RED), 
                true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack held = player.getItemInHand(hand);
        int currentDurability = material.getBaseDurability() - held.getDamageValue();

        if (currentDurability <= 0) {
            player.displayClientMessage(Component.literal("This plate is broken.").withStyle(ChatFormatting.RED), true);
            return InteractionResultHolder.fail(held);
        }

        chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            Plate plate = new Plate(tier, material);
            float delta = plate.getMaxDurability() - currentDurability;
            if (delta > 0.01f) {
                plate.damage(delta);
            }

            if (!cap.hasFrontPlate()) {
                cap.insertFrontPlate(plate);
                player.displayClientMessage(Component.literal("Front plate installed!").withStyle(ChatFormatting.GREEN), true);
                if (!player.getAbilities().instabuild) held.shrink(1);
            } else if (!cap.hasBackPlate()) {
                cap.insertBackPlate(plate);
                player.displayClientMessage(Component.literal("Back plate installed!").withStyle(ChatFormatting.GREEN), true);
                if (!player.getAbilities().instabuild) held.shrink(1);
            } else {
                player.displayClientMessage(Component.literal("All plate slots are full!").withStyle(ChatFormatting.RED), true);
            }

            chest.setTag(chest.getOrCreateTag());
            player.setItemSlot(EquipmentSlot.CHEST, chest);
        });

        return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
    }

    //TODO think about style guide for this stuff in general
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int currentDurability = material.getBaseDurability() - stack.getDamageValue();

        Component tierComponent = Component.empty()
            .append(Component.literal("Rating: ").withStyle(ChatFormatting.GRAY))
            .append(tier.getDisplayName());
        tooltip.add(tierComponent);

        Component materialComponent = Component.empty()
            .append(Component.literal("Material: ").withStyle(ChatFormatting.GRAY))
            .append(material.getDisplayName());
        tooltip.add(materialComponent);

        Component durabilityComponent = Component.empty()
            .append(Component.literal("Durability: ").withStyle(ChatFormatting.GRAY))
            .append(Component.literal(currentDurability + " / " + material.getBaseDurability()).withStyle(ChatFormatting.RED));
        tooltip.add(durabilityComponent);

        float speedMod = material.getSpeedModifier();
        String speedText = String.format("%+.1f%%", speedMod * 100); // e.g. +5.0% or -10.0%
        ChatFormatting speedColor = speedMod >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
        Component movementComponent = Component.empty()
            .append(Component.literal("Movement: ").withStyle(ChatFormatting.GRAY))
            .append(Component.literal(speedText).withStyle(speedColor))
            .append(Component.literal(" (averaged with other plates)").withStyle(ChatFormatting.GRAY));
        tooltip.add(movementComponent);
        
        tooltip.add(Component.literal("Can only be inserted into vests.").withStyle(ChatFormatting.RED));
    }

    public ProtectionTier getTier() {
        return tier;
    }

    public MaterialType getMaterial() {
        return material;
    }
}