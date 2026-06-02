package com.raiiiden.warborn.client.event;

import com.raiiiden.warborn.WARBORN;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HideLayerRenderHandler {

    private static final TagKey<Item> HIDE_LAYER =
            ItemTags.create(new ResourceLocation(WARBORN.MODID, "hide_layer"));

    // Saves pre-render visibility so Post can restore it exactly,
    // preserving whatever the player's own skin customisation settings set.
    private static final ThreadLocal<boolean[]> SAVED = ThreadLocal.withInitial(() -> new boolean[10]);

    @SuppressWarnings({"deprecation", "removal"})
    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();

        boolean[] saved = SAVED.get();
        saved[0] = model.body.visible;
        saved[1] = model.leftArm.visible;
        saved[2] = model.rightArm.visible;
        saved[3] = model.jacket.visible;
        saved[4] = model.leftSleeve.visible;
        saved[5] = model.rightSleeve.visible;
        saved[6] = model.leftLeg.visible;
        saved[7] = model.rightLeg.visible;
        saved[8] = model.leftPants.visible;
        saved[9] = model.rightPants.visible;

        // Leggings slot → hide outer skin layer only (leftPants/rightPants)
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        if (!leggings.isEmpty() && leggings.is(HIDE_LAYER)) {
            model.leftPants.visible  = false;
            model.rightPants.visible = false;
        }

        // Chestplate slot or curios uniform slot → hide outer skin layer only (jacket/sleeves)
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean hasUniform = (!chestplate.isEmpty() && chestplate.is(HIDE_LAYER))
                || !CuriosApi.getCuriosHelper()
                        .findCurios(player, stack -> stack.is(HIDE_LAYER))
                        .isEmpty();
        if (hasUniform) {
            model.jacket.visible      = false;
            model.leftSleeve.visible  = false;
            model.rightSleeve.visible = false;
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
        boolean[] saved = SAVED.get();

        model.body.visible        = saved[0];
        model.leftArm.visible     = saved[1];
        model.rightArm.visible    = saved[2];
        model.jacket.visible      = saved[3];
        model.leftSleeve.visible  = saved[4];
        model.rightSleeve.visible = saved[5];
        model.leftLeg.visible     = saved[6];
        model.rightLeg.visible    = saved[7];
        model.leftPants.visible   = saved[8];
        model.rightPants.visible  = saved[9];
    }
}
