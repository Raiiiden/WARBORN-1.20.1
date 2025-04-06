package com.raiiiden.warborn.client;

import com.raiiiden.warborn.common.network.ModNetworking;
import com.raiiiden.warborn.common.util.HelmetVisionHandler;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = "warborn", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientKeyEvents {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Player player = mc.player;

        // OPEN BACKPACK
        if (ModKeybindings.OPEN_BACKPACK.consumeClick()) {
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (WarbornArmorItem.isBackpackItem(chest)) {
                ModNetworking.openBackpack(chest);
                return;
            }

            CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
                inv.getCurios().forEach((slotId, handler) -> {
                    for (int i = 0; i < handler.getStacks().getSlots(); i++) {
                        ItemStack stack = handler.getStacks().getStackInSlot(i);
                        if (WarbornArmorItem.isBackpackItem(stack)) {
                            ModNetworking.openBackpack(stack);
                            return;
                        }
                    }
                });
            });
        }

        // TOGGLE NIGHT VISION (shader-based)
        if (ModKeybindings.TOGGLE_NIGHT_VISION.consumeClick()) {
            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            if (!HelmetVisionHandler.isAllowedHelmet(helmet)) {
                player.displayClientMessage(Component.literal("You must be wearing a valid helmet."), true);
                return;
            }

            var tag = helmet.getOrCreateTag();
            boolean enabled = tag.getBoolean("NvgCheck");
            tag.putBoolean("NvgCheck", !enabled);
            String msg = enabled ? "Night Vision OFF" : "Night Vision ON";
            player.displayClientMessage(Component.literal(msg), true);
        }
        if (ModKeybindings.REMOVE_PLATE_MENU.consumeClick()) {
            Minecraft.getInstance().setScreen(new RemovePlateScreen());
        }
    }


    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase != ClientTickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Player player = mc.player;
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        // Remove night vision if the helmet is removed or swapped
        if (player.hasEffect(MobEffects.NIGHT_VISION) && !HelmetVisionHandler.isAllowedHelmet(helmet)) {
            player.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
}
