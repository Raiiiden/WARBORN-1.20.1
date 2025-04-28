package com.raiiiden.warborn.common.util;

import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import com.raiiiden.warborn.common.object.plate.MaterialType;
import com.raiiiden.warborn.common.object.plate.Plate;
import com.raiiiden.warborn.common.object.plate.ProtectionTier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class PlateTooltip {
    private PlateTooltip() {
    }                 // no-instantiate utility

    private static final String KEY_PREFIX_TOOLTIP = "tooltip.warborn.";
    private static final String KEY_PREFIX_ITEM = "item.warborn."; // Or wherever material/tier names are

    private static final String KEY_PLATE_RATING = KEY_PREFIX_TOOLTIP + "plate.rating";       // "Rating: %s"
    private static final String KEY_PLATE_MATERIAL = KEY_PREFIX_TOOLTIP + "plate.material";     // "Material: %s"
    private static final String KEY_PLATE_DURABILITY = KEY_PREFIX_TOOLTIP + "plate.durability";   // "Durability: %s / %s" or "%1$d / %2$d"
    private static final String KEY_PLATE_MOVEMENT = KEY_PREFIX_TOOLTIP + "plate.movement";     // "Movement: %s"
    private static final String KEY_PLATE_MOVEMENT_SUFFIX = KEY_PREFIX_TOOLTIP + "plate.movement.suffix"; // " (averaged with other plates)"
    private static final String KEY_PLATE_VEST_ONLY = KEY_PREFIX_TOOLTIP + "plate.vest_only";    // "Can only be inserted into vests."

    private static final String KEY_VEST_COMPATIBLE = KEY_PREFIX_TOOLTIP + "vest.compatible";  // "Plate Compatible"
    private static final String KEY_VEST_FRONT = KEY_PREFIX_TOOLTIP + "vest.front";        // "Front: %s %s" (Material, Tier)
    private static final String KEY_VEST_BACK = KEY_PREFIX_TOOLTIP + "vest.back";         // "Back: %s %s" (Material, Tier)
    private static final String KEY_VEST_SPEED = KEY_PREFIX_TOOLTIP + "vest.speed";        // "Speed Effect: %s"

    /**
     * Adds tooltip information for a Plate item stack.
     */
    public static void addPlate(List<Component> tip,
                                ProtectionTier tier,
                                MaterialType material,
                                ItemStack stack) {
        int maxDur = material.getBaseDurability();
        int curDur = maxDur - stack.getDamageValue();

        tip.add(Component.translatable(KEY_PLATE_RATING, tier.getDisplayName())
                .withStyle(ChatFormatting.GRAY));

        tip.add(Component.translatable(KEY_PLATE_MATERIAL, material.getDisplayName())
                .withStyle(ChatFormatting.GRAY));

         Style durabilityStyle = getDurabilityColor(curDur, maxDur);
         tip.add(Component.translatable(KEY_PLATE_DURABILITY,
                Component.literal(String.valueOf(curDur)).withStyle(durabilityStyle),
                Component.literal(String.valueOf(maxDur)).withStyle(ChatFormatting.GRAY)
         ).withStyle(ChatFormatting.GRAY));


        float speed = material.getSpeedModifier();
        String pct = String.format("%+.1f%%", speed * 100);
        ChatFormatting speedColour = speed >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED;

        tip.add(Component.translatable(KEY_PLATE_MOVEMENT,
                        Component.literal(pct).withStyle(speedColour)
                ).withStyle(ChatFormatting.GRAY)
                .append(Component.translatable(KEY_PLATE_MOVEMENT_SUFFIX)
                        .withStyle(ChatFormatting.DARK_GRAY)));

        tip.add(Component.translatable(KEY_PLATE_VEST_ONLY)
                .withStyle(ChatFormatting.RED));
    }

    /**
     * Adds tooltip information to a Plate Carrier item stack.
     */
    public static void addChestplate(List<Component> list, ItemStack stack) {
        list.add(Component.translatable(KEY_VEST_COMPATIBLE)
                .withStyle(style -> style.withColor(0xFFAA00))); // STAY WOKE o.O (no not htat woke dummy)

        stack.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            float totalSpeed = 0f;
            int count = 0;

            Plate front = cap.hasFrontPlate() ? cap.getFrontPlate() : null;
            Plate back = cap.hasBackPlate() ? cap.getBackPlate() : null;

            if (front != null && !front.isBroken()) {
                list.add(Component.translatable(KEY_VEST_FRONT,
                                front.getMaterial().getDisplayName(),
                                front.getTier().getDisplayName())
                        .withStyle(ChatFormatting.GRAY));
                totalSpeed += front.getSpeedModifier();
                ++count;
            }

            if (back != null && !back.isBroken()) {
                list.add(Component.translatable(KEY_VEST_BACK,
                                back.getMaterial().getDisplayName(),
                                back.getTier().getDisplayName())
                        .withStyle(ChatFormatting.GRAY));
                totalSpeed += back.getSpeedModifier();
                ++count;
            }

            if (count > 0) {
                float avg = totalSpeed / count;
                String pct = String.format("%+.1f%%", avg * 100);
                ChatFormatting colour = avg >= 0 ? ChatFormatting.GREEN : ChatFormatting.RED;

                list.add(Component.translatable(KEY_VEST_SPEED,
                        Component.literal(pct).withStyle(colour)
                ).withStyle(ChatFormatting.GRAY));
            }
        });
    }

    private static Style getDurabilityColor(int current, int max) {
        double percentage = (double) current / max;
        if (percentage > 0.6) return Style.EMPTY.applyFormat(ChatFormatting.GREEN);
        if (percentage > 0.25) return Style.EMPTY.applyFormat(ChatFormatting.YELLOW);
        return Style.EMPTY.applyFormat(ChatFormatting.RED);
    }
}
