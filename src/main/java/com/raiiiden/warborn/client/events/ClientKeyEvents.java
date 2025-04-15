package com.raiiiden.warborn.client.events;

import com.raiiiden.warborn.client.ModKeybindings;
import com.raiiiden.warborn.client.screen.RemovePlateScreen;
import com.raiiiden.warborn.client.shader.ShaderRegistry;
import com.raiiiden.warborn.common.network.ModNetworking;
import com.raiiiden.warborn.common.util.HelmetVisionHandler;
import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
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

        if (ModKeybindings.TOGGLE_SPECIAL_VISION.consumeClick()) {
            if (HelmetVisionHandler.toggleVision(player)) {
                ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                String activeVision = HelmetVisionHandler.getActiveVisionType(helmet);

                if (!activeVision.isEmpty()) {
                    String message;
                    ChatFormatting color = switch (activeVision) {
                        case WarbornArmorItem.TAG_NVG -> {
                            message = "Night Vision Mode";
                            yield ChatFormatting.GREEN;
                        }
                        case WarbornArmorItem.TAG_SIMPLE_NVG -> {
                            message = "Simple Night Vision Mode";
                            yield ChatFormatting.GREEN;
                        }
                        case WarbornArmorItem.TAG_DIGITAL -> {
                            message = "Digital Vision Mode";
                            yield ChatFormatting.WHITE;
                        }
                        case WarbornArmorItem.TAG_THERMAL -> {
                            message = "Thermal Vision Mode";
                            yield ChatFormatting.RED;
                        }
                        default -> {
                            message = "Vision Mode";
                            yield ChatFormatting.YELLOW;
                        }
                    };

                    player.displayClientMessage(Component.literal(message + " Activated").withStyle(color), true);
                } else {
                    player.displayClientMessage(Component.literal("Vision Mode Disabled").withStyle(ChatFormatting.YELLOW), true);
                }
            } else {
                player.displayClientMessage(Component.literal("No appropriate helmet equipped").withStyle(ChatFormatting.RED), true);
            }
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

        if (!HelmetVisionHandler.isAllowedHelmet(helmet)) {
            ShaderRegistry registry = ShaderRegistry.getInstance();

            final String[] VISION_SHADERS = {
                    "nvg", "snvg", "dvg", "dnvg", "tvg"
            };

            boolean wasActive = false;
            for (String shaderId : VISION_SHADERS) {
                if (registry.isShaderActive(shaderId) || registry.isShaderForceEnabled(shaderId)) {
                    registry.setShaderEnabled(shaderId, false);
                    wasActive = true;
                }
            }

            if (wasActive) {
                player.displayClientMessage(Component.literal("Vision mode disabled - helmet removed").withStyle(ChatFormatting.YELLOW), true);
            }
        }
    }
}
