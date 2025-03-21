package com.raiiiden.warborn.client;

import com.raiiiden.warborn.item.WarbornArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = "warborn", bus = Mod.EventBusSubscriber.Bus.MOD)
public class BackpackKeyBindings {

    public static final KeyMapping OPEN_BACKPACK = new KeyMapping(
            "key.warborn.open_backpack",
            GLFW.GLFW_KEY_B,
            "key.categories.inventory"
    );

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_BACKPACK);
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
    }

    public static class ClientEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (OPEN_BACKPACK.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null) return;

                // Try chestplate first
                ItemStack chest = mc.player.getItemBySlot(EquipmentSlot.CHEST);
                if (chest.getItem() instanceof WarbornArmorItem armor && armor.getArmorType().contains("backpack")) {
                    armor.use(mc.level, mc.player, InteractionHand.MAIN_HAND);
                    return;
                }

                // Then check curios
                CuriosApi.getCuriosInventory(mc.player).ifPresent(inv -> {
                    inv.getCurios().forEach((id, handler) -> {
                        for (int i = 0; i < handler.getStacks().getSlots(); i++) {
                            ItemStack stack = handler.getStacks().getStackInSlot(i);
                            if (stack.getItem() instanceof WarbornArmorItem armor && armor.getArmorType().contains("backpack")) {
                                armor.use(mc.level, mc.player, InteractionHand.MAIN_HAND);
                                return;
                            }
                        }
                    });
                });
            }
        }
    }
}
