package com.raiiiden.warborn.client.event;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.util.ArmorLayerCoverage;
import com.raiiiden.warborn.client.util.ArmorLayerCoverage.Region;
import com.raiiiden.warborn.common.item.WBArmorItem;
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
import top.theillusivec4.curios.api.SlotResult;

import java.util.function.Predicate;

// Hides the player's second skin layer under Warborn gear, derived from the model rather than a curated list; the hide_layer tag still works for items that aren't ours.
@Mod.EventBusSubscriber(modid = WARBORN.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class HideLayerRenderHandler {

    private static final TagKey<Item> HIDE_LAYER =
            ItemTags.create(new ResourceLocation(WARBORN.MODID, "hide_layer"));

    // Saves pre-render visibility so Post can restore it exactly,
    // preserving whatever the player's own skin customisation settings set.
    private static final ThreadLocal<boolean[]> SAVED = ThreadLocal.withInitial(() -> new boolean[6]);

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;
        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();

        boolean[] saved = SAVED.get();
        saved[0] = model.hat.visible;
        saved[1] = model.jacket.visible;
        saved[2] = model.leftSleeve.visible;
        saved[3] = model.rightSleeve.visible;
        saved[4] = model.leftPants.visible;
        saved[5] = model.rightPants.visible;

        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);

        // Head: a balaclava worn either as headgear or in the curios mask slot.
        ItemStack mask = isMask(head) ? head : findCurio(player, HideLayerRenderHandler::isMask);
        if (coversRegion(mask, Region.HEAD)) {
            model.hat.visible = false;
        }

        // Torso: the uniform, worn either in the chest slot or the curios uniform slot.
        ItemStack garment = isGarment(chest) ? chest : findCurio(player, HideLayerRenderHandler::isGarment);
        if (!garment.isEmpty()) {
            model.jacket.visible = false;
            model.leftSleeve.visible = false;
            model.rightSleeve.visible = false;
        }

        // Legs: any leg armour, or a garment modelled all the way down.
        if (isLegArmour(legs) || coversRegion(garment, Region.LEGS)) {
            model.leftPants.visible = false;
            model.rightPants.visible = false;
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        PlayerModel<AbstractClientPlayer> model = event.getRenderer().getModel();
        boolean[] saved = SAVED.get();

        model.hat.visible = saved[0];
        model.jacket.visible = saved[1];
        model.leftSleeve.visible = saved[2];
        model.rightSleeve.visible = saved[3];
        model.leftPants.visible = saved[4];
        model.rightPants.visible = saved[5];
    }

    private static boolean isMask(ItemStack stack) {
        return !stack.isEmpty()
                && ((stack.getItem() instanceof WBArmorItem armor && armor.isBalaclava()) || stack.is(HIDE_LAYER));
    }

    private static boolean isGarment(ItemStack stack) {
        return !stack.isEmpty()
                && ((stack.getItem() instanceof WBArmorItem armor && armor.isUniform()) || stack.is(HIDE_LAYER));
    }

    private static boolean isLegArmour(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() instanceof WBArmorItem || stack.is(HIDE_LAYER));
    }

    private static boolean coversRegion(ItemStack stack, Region region) {
        return !stack.isEmpty()
                && stack.getItem() instanceof WBArmorItem armor
                && ArmorLayerCoverage.covers(armor, region);
    }

    @SuppressWarnings({"deprecation", "removal"})
    private static ItemStack findCurio(AbstractClientPlayer player, Predicate<ItemStack> predicate) {
        return CuriosApi.getCuriosHelper().findCurios(player, predicate)
                .stream()
                .findFirst()
                .map(SlotResult::stack)
                .orElse(ItemStack.EMPTY);
    }
}
