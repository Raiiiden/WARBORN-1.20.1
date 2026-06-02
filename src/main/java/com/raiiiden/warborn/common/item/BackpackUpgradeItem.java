package com.raiiiden.warborn.common.item;

import net.minecraft.world.item.Item;

public class BackpackUpgradeItem extends Item {

    private final int tier; // 2 or 3

    public BackpackUpgradeItem(int tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public int getTier() {
        return tier;
    }
}
