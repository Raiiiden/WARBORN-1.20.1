package com.raiiiden.warborn.client.event;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.gui.BackpackTooltipComponent;
import com.raiiiden.warborn.client.gui.HelmetSlotTooltipComponent;
import com.raiiiden.warborn.client.gui.NVGScreenFadeOverlay;
import com.raiiiden.warborn.client.renderer.item.ChestplateBundleDecorator;
import com.raiiiden.warborn.client.renderer.item.HelmetBatteryDecorator;
import com.raiiiden.warborn.common.item.BackpackTooltipData;
import com.raiiiden.warborn.common.item.HelmetSlotTooltip;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterItemDecorationsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import com.raiiiden.warborn.client.renderer.layer.WarbornBackpackLayer;
import com.raiiiden.warborn.client.renderer.layer.WarbornGoggleMountLayer;
import com.raiiiden.warborn.client.renderer.layer.WarbornMaskLayer;
import com.raiiiden.warborn.client.renderer.layer.WarbornShoulderpadsLayer;
import com.raiiiden.warborn.client.renderer.layer.WarbornUniformLayer;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("nvg_fade", NVGScreenFadeOverlay.OVERLAY);
    }

    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        HelmetBatteryDecorator batteryDecorator = new HelmetBatteryDecorator();
        ChestplateBundleDecorator bundleDecorator = new ChestplateBundleDecorator();

        ForgeRegistries.ITEMS.forEach(item -> {
            if (item instanceof WBArmorItem wbItem) {
                if (wbItem.getType() == ArmorItem.Type.HELMET) {
                    event.register(item, batteryDecorator);
                } else if (wbItem.getType() == ArmorItem.Type.CHESTPLATE) {
                    event.register(item, bundleDecorator);
                }
            }
        });
    }

    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(HelmetSlotTooltip.class, HelmetSlotTooltipComponent::new);
        event.register(BackpackTooltipData.class, BackpackTooltipComponent::new);
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public static void registerEntityLayers(EntityRenderersEvent.AddLayers event) {
        if (event == null) {
            return;
        }
        for (String skin : event.getSkins()) {
            LivingEntityRenderer<?, ?> renderer = event.getSkin(skin);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new WarbornBackpackLayer<>(playerRenderer));
                playerRenderer.addLayer(new WarbornShoulderpadsLayer<>(playerRenderer));
                playerRenderer.addLayer(new WarbornUniformLayer<>(playerRenderer));
                playerRenderer.addLayer((new WarbornMaskLayer<>(playerRenderer)));
                addGoggleMountLayer(playerRenderer);
            }
        }

        // The goggle mount hangs off a helmet in the vanilla head slot, so anything humanoid that can
        // wear one has to get the layer - armor stands above all, but zombies and skeletons too. The
        // curios layers above stay player-only because the slots they read only exist on players.
        // AddLayers exposes no entity-type listing on this Forge version, so walk the registry and
        // ask for each renderer; getRenderer returns null for anything that isn't a LivingEntity.
        for (EntityType<?> type : ForgeRegistries.ENTITY_TYPES) {
            LivingEntityRenderer<?, ?> renderer = event.getRenderer((EntityType<? extends LivingEntity>) type);
            if (renderer != null && !(renderer instanceof PlayerRenderer)) {
                addGoggleMountLayer(renderer);
            }
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void addGoggleMountLayer(LivingEntityRenderer<?, ?> renderer) {
        if (!(renderer.getModel() instanceof HumanoidModel)) return;

        LivingEntityRenderer raw = renderer;
        raw.addLayer(new WarbornGoggleMountLayer<>(raw));
    }
}