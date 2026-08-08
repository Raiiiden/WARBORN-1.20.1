package com.raiiiden.warborn.client.renderer;

import com.raiiiden.warborn.common.init.ModItemRegistry;
import com.raiiiden.warborn.common.object.plate.Plate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.UUID;

// Manages phantom plate rendering for removal animations when the player isn't holding a plate item.
@OnlyIn(Dist.CLIENT)
public class PhantomPlateRenderManager extends AbstractPhantomRenderManager {
    private static PhantomPlateRenderManager INSTANCE;

    private PhantomPlateRenderManager() {}

    public static PhantomPlateRenderManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PhantomPlateRenderManager();
        }
        return INSTANCE;
    }

    // Starts rendering a phantom plate for the removal animation.
    public void startPhantomRender(Plate plate, int durationTicks, UUID playerUUID) {
        if (plate == null) return;

        ItemStack stack = ModItemRegistry.getPlateItem(plate.getTier(), plate.getMaterial())
                .getDefaultInstance();

        CompoundTag tag = stack.getOrCreateTag();
        tag.putBoolean("warborn_phantom_render", true);
        tag.putBoolean(com.raiiiden.warborn.common.item.ArmorPlateItem.PENDING_REMOVE_TAG, true);
        tag.putInt("warborn_remove_delay", 55); // Match the animation length

        activate(stack, durationTicks, playerUUID);
    }
}
