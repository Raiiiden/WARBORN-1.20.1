package com.raiiiden.warborn.client;

import com.raiiiden.warborn.item.WarbornArmorItem;
import com.raiiiden.warborn.common.network.ModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = "warborn", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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

    @Mod.EventBusSubscriber(modid = "warborn", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (OPEN_BACKPACK.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player == null) return;

                // Check chestplate first
                ItemStack chest = mc.player.getItemBySlot(EquipmentSlot.CHEST);
                if (WarbornArmorItem.isBackpackItem(chest)) {
                    ModNetworking.openBackpack(chest);
                    return;
                }

                // Then check Curios
                CuriosApi.getCuriosInventory(mc.player).ifPresent(inv -> {
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
        }
    }
}
