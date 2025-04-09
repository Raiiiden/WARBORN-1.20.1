package com.raiiiden.warborn.item;

import com.raiiiden.warborn.common.init.ModRegistry;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

    public ArmorPlateItem(ProtectionTier tier, MaterialType material, Properties props) {
        super(props.durability(material.getBaseDurability()));
        this.tier = tier;
        this.material = material;
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
            player.displayClientMessage(Component.literal("You must wear a chestplate."), true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack held = player.getItemInHand(hand);
        int currentDurability = material.getBaseDurability() - held.getDamageValue();

        if (currentDurability <= 0) {
            player.displayClientMessage(Component.literal("This plate is broken."), true);
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
                player.displayClientMessage(Component.literal("Front plate installed!"), true);
                if (!player.getAbilities().instabuild) held.shrink(1);
            } else if (!cap.hasBackPlate()) {
                cap.insertBackPlate(plate);
                player.displayClientMessage(Component.literal("Back plate installed!"), true);
                if (!player.getAbilities().instabuild) held.shrink(1);
            } else {
                player.displayClientMessage(Component.literal("All plate slots are full!"), true);
            }

            // Force sync to item
            chest.setTag(chest.getOrCreateTag());
            player.setItemSlot(EquipmentSlot.CHEST, chest);
        });

        return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int currentDurability = material.getBaseDurability() - stack.getDamageValue();

        // Add tier info
        tooltip.add(Component.literal("Protection: " + tier.name().replace("LEVEL_", "NIJ "))
                .withStyle(ChatFormatting.BLUE));

        // Add material info
        ChatFormatting materialColor = ChatFormatting.GRAY;
        if (material == MaterialType.STEEL) materialColor = ChatFormatting.DARK_GRAY;
        else if (material == MaterialType.CERAMIC) materialColor = ChatFormatting.WHITE;
        else if (material == MaterialType.POLYETHYLENE) materialColor = ChatFormatting.GREEN;
        else if (material == MaterialType.COMPOSITE) materialColor = ChatFormatting.GOLD;

        tooltip.add(Component.literal("Material: " + material.name())
                .withStyle(materialColor));

        // Add durability info
        tooltip.add(Component.literal("Durability: " + currentDurability + " / " + material.getBaseDurability())
                .withStyle(ChatFormatting.RED));

        // Add speed impact
        float speedMod = material.getSpeedModifier();
        String speedText = speedMod > 0 ? "+" + (speedMod * 100) + "%" : (speedMod * 100) + "%";
        ChatFormatting speedColor = speedMod >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED;
        tooltip.add(Component.literal("Movement: " + speedText)
                .withStyle(speedColor));
    }

    public ProtectionTier getTier() {
        return tier;
    }

    public MaterialType getMaterial() {
        return material;
    }
}