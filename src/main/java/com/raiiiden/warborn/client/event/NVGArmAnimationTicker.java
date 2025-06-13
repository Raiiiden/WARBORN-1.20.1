package com.raiiiden.warborn.client.event;

import com.raiiiden.warborn.common.item.WBArmorItem;
import com.raiiiden.warborn.common.network.ModNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NVGArmAnimationTicker {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        Player player = mc.player;

        int animTick = player.getPersistentData().getInt("NVG_ANIM_TICK");
        if (animTick > 0) {
            player.getPersistentData().putInt("NVG_ANIM_TICK", animTick + 1);

            int maxTicks = 10;
            int triggerTick = 8;

            if (animTick >= triggerTick && !player.getPersistentData().getBoolean("NVG_ANIM_HELMET_READY")) {
                player.getPersistentData().putBoolean("NVG_ANIM_HELMET_READY", true);

                ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                if (helmet.getItem() instanceof WBArmorItem helmetItem && helmet.is(ClientKeyEvents.HAS_TOGGLE_TAG)) {
                    boolean newState = !helmetItem.isTopOpen(helmet);
                    ModNetworking.sendToggleHelmetTop(newState);
                    player.playSound(newState ?
                            SoundEvents.IRON_TRAPDOOR_OPEN : SoundEvents.IRON_TRAPDOOR_CLOSE, 1.0F, 1.0F);

                    player.displayClientMessage(
                            Component.literal("Helmet Top " + (newState ? "Opened" : "Closed"))
                                    .withStyle(ChatFormatting.GRAY),
                            true
                    );
                }
            }

            if (animTick >= maxTicks) {
                player.getPersistentData().putInt("NVG_ANIM_TICK", 0);
                player.getPersistentData().putBoolean("NVG_ANIM_HELMET_READY", false);
            }
        }
    }
}