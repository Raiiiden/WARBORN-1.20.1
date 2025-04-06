package com.raiiiden.warborn.item;

import com.raiiiden.warborn.ModRegistry;
import com.raiiiden.warborn.common.object.capability.PlateHolderImpl;
import com.raiiiden.warborn.common.object.capability.PlateHolderProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ArmorPlateItem extends Item {
    public static final int MAX_HITS = 10;

    public ArmorPlateItem(Properties props) {
        super(props.durability(MAX_HITS));
    }

    public static ItemStack createPlateWithHitsRemaining(int hitsLeft) {
        ItemStack stack = new ItemStack(ModRegistry.ARMOR_PLATE.get());
        int damage = Math.max(0, MAX_HITS - hitsLeft);
        stack.setDamageValue(damage);
        return stack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.isEmpty()) {
            player.displayClientMessage(Component.literal("You must wear a chestplate."), true);
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack held = player.getItemInHand(hand);
        int hitsLeft = MAX_HITS - held.getDamageValue();

        if (hitsLeft <= 0) {
            player.displayClientMessage(Component.literal("This plate is broken."), true);
            return InteractionResultHolder.fail(held);
        }

        chest.getCapability(PlateHolderProvider.CAP).ifPresent(cap -> {
            if (cap instanceof PlateHolderImpl impl) {
                if (!impl.hasFrontPlate()) {
                    impl.insertFrontPlateWithDurability(hitsLeft);
                    player.displayClientMessage(Component.literal("Front plate installed!"), true);
                    if (!player.getAbilities().instabuild) held.shrink(1);
                } else if (!impl.hasBackPlate()) {
                    impl.insertBackPlateWithDurability(hitsLeft);
                    player.displayClientMessage(Component.literal("Back plate installed!"), true);
                    if (!player.getAbilities().instabuild) held.shrink(1);
                } else {
                    player.displayClientMessage(Component.literal("All plate slots are full!"), true);
                }

                // Force sync to item
                chest.setTag(chest.getOrCreateTag());
                player.setItemSlot(EquipmentSlot.CHEST, chest);
            }
        });

        return InteractionResultHolder.sidedSuccess(held, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        int hitsLeft = MAX_HITS - stack.getDamageValue();
        tooltip.add(Component.literal("Hits left: " + hitsLeft + " / " + MAX_HITS));
    }
}
