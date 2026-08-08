package com.raiiiden.warborn.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// A goggle device insertable into a helmet tagged can_have_goggles; its visionType picks the shader.
public class GogglesItem extends Item {

    private final String visionType;

    public GogglesItem(String visionType) {
        super(new Item.Properties().stacksTo(1));
        this.visionType = visionType;
    }

    public String getVisionType() {
        return visionType;
    }

    // Goggles cannot go into chestplate bundles or vanilla bundles.
    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        String typeName = switch (visionType) {
            case WBArmorItem.TAG_NVG         -> "Night Vision";
            case WBArmorItem.TAG_SIMPLE_NVG  -> "Simple Night Vision";
            case WBArmorItem.TAG_THERMAL     -> "Thermal Vision";
            case WBArmorItem.TAG_THERMAL_WHITE -> "Thermal Vision (White Hot)";
            case WBArmorItem.TAG_THERMAL_BLACK -> "Thermal Vision (Black Hot)";
            case WBArmorItem.TAG_DIGITAL     -> "Digital Vision";
            default                          -> visionType;
        };
        tooltip.add(Component.literal("Mode: " + typeName).withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.literal("Requires a compatible helmet + NVG Battery.").withStyle(ChatFormatting.GRAY));
    }
}
