package com.raiiiden.warborn.client.event;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.screen.RemovePlateScreen;
import com.raiiiden.warborn.client.shader.ClientVisionState;
import com.raiiiden.warborn.client.shader.ShaderRegistry;
import com.raiiiden.warborn.common.init.ModSoundEvents;
import com.raiiiden.warborn.common.item.WBArmorItem;
import com.raiiiden.warborn.common.item.BackpackItem;
import com.raiiiden.warborn.common.network.ModNetworking;
import com.raiiiden.warborn.common.network.ServerboundNVGArmAnimationPacket;
import com.raiiiden.warborn.common.util.HelmetVisionHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = "warborn", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientKeyEvents {

    private static Item lastHelmetItem = null;
    private static String lastVisionType = "";

    public static final TagKey<Item> HAS_TOGGLE_TAG =
            TagKey.create(Registries.ITEM, new ResourceLocation(WARBORN.MODID, "has_toggle"));

    private static final TagKey<Item> BETA_7 =
            TagKey.create(Registries.ITEM, new ResourceLocation(WARBORN.MODID, "is_beta7"));

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Player player = mc.player;

        if (ModKeybindings.OPEN_BACKPACK.consumeClick()) {
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (BackpackItem.isBackpackItem(chest)) {
                ModNetworking.openBackpack(chest);
                return;
            }

            CuriosApi.getCuriosInventory(player).ifPresent(inv -> {
                inv.getCurios().forEach((slotId, handler) -> {
                    for (int i = 0; i < handler.getStacks().getSlots(); i++) {
                        ItemStack stack = handler.getStacks().getStackInSlot(i);
                        if (BackpackItem.isBackpackItem(stack)) {
                            ModNetworking.openBackpack(stack);
                            return;
                        }
                    }
                });
            });
        }

        if (ModKeybindings.TOGGLE_HELMET_TOP.consumeClick()) {
            ModNetworking.sendToServer(new ServerboundNVGArmAnimationPacket(true));
        }

        if (ModKeybindings.TOGGLE_SPECIAL_VISION.consumeClick()) {
            if (HelmetVisionHandler.toggleVision(player)) {
                ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                String activeVision = HelmetVisionHandler.getActiveVisionType(helmet);
                player.playSound(ModSoundEvents.WARBORN_NVG_TOGGLE.get(), 1.0F, 1.0F);

                if (!activeVision.isEmpty()) {
                    String message;
                    ChatFormatting color = switch (activeVision) {
                        case WBArmorItem.TAG_NVG -> {
                            message = "Night Vision Mode";
                            yield ChatFormatting.GREEN;
                        }
                        case WBArmorItem.TAG_SIMPLE_NVG -> {
                            message = "Simple Night Vision Mode";
                            yield ChatFormatting.GREEN;
                        }
                        case WBArmorItem.TAG_DIGITAL -> {
                            message = "Digital Vision Mode";
                            yield ChatFormatting.WHITE;
                        }
                        case WBArmorItem.TAG_THERMAL -> {
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
                player.displayClientMessage(Component.literal("No appropriate helmet equipped")
                                .withStyle(ChatFormatting.RED),
                        true);
            }
        }

        if (ModKeybindings.REMOVE_PLATE_MENU.consumeClick()) {
            Minecraft.getInstance().setScreen(new RemovePlateScreen());
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Player player = mc.player;
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        boolean isHelmetValid = HelmetVisionHandler.isAllowedHelmet(helmet);

        if (!isHelmetValid) {
            disableAllShaders(player);
            lastHelmetItem = null;
            lastVisionType = "";
            return;
        }

        Item currentItem = helmet.getItem();
        String currentVision = HelmetVisionHandler.getActiveVisionType(helmet);

        if (lastHelmetItem != null && currentItem != lastHelmetItem) {
            if (!lastVisionType.isEmpty()) {
                ShaderRegistry.getInstance().setShaderEnabled(
                        HelmetVisionHandler.getShaderIdFromVisionType(lastVisionType), false
                );
                ClientVisionState.clear(player.getUUID());
            }
        }

        lastHelmetItem = currentItem;
        lastVisionType = currentVision;
    }

    private static void disableAllShaders(Player player) {
        String[] shaders = {"nvg", "snvg", "dvg", "dnvg", "tvg"};
        boolean wasActive = false;

        for (String id : shaders) {
            if (ShaderRegistry.getInstance().isShaderActive(id) ||
                    ShaderRegistry.getInstance().isShaderForceEnabled(id)) {
                ShaderRegistry.getInstance().setShaderEnabled(id, false);
                wasActive = true;
            }
        }

        ClientVisionState.clear(player.getUUID());

        if (wasActive) {
            player.displayClientMessage(Component.literal("Vision mode disabled - helmet removed")
                    .withStyle(ChatFormatting.YELLOW), true);
        }
    }
}