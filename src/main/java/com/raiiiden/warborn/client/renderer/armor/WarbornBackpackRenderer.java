package com.raiiiden.warborn.client.renderer.armor;

import com.raiiiden.warborn.client.icon.IconPreparable;
import com.raiiiden.warborn.client.model.WarbornBackpackModel;
import com.raiiiden.warborn.common.item.BackpackItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class WarbornBackpackRenderer extends GeoArmorRenderer<BackpackItem> implements IconPreparable {
    private final BackpackItem item;

    public WarbornBackpackRenderer(BackpackItem item) {
        super(new WarbornBackpackModel(item));
        this.item = item;
    }

    @Override
    public void prepForIcon(ItemStack stack, EquipmentSlot slot) {
        this.baseModel = this;
        this.currentEntity = null;
        this.currentStack = stack;
        this.animatable = this.item;
        this.currentSlot = slot;
    }

    // Icon mode has no entity for GeckoLib to key animation state on; see IconPreparable.
    @Override
    public long getInstanceId(BackpackItem animatable) {
        if (this.currentEntity == null) {
            return IconPreparable.iconInstanceId(this.item);
        }
        return super.getInstanceId(animatable);
    }
}
