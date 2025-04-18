package com.raiiiden.warborn.common.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class WarbornArmorConfig {
    public static final ForgeConfigSpec SPEC;
    public static final Config CONFIG;

    static {
        var pair = new ForgeConfigSpec.Builder().configure(Config::new);
        SPEC = pair.getRight();
        CONFIG = pair.getLeft();
    }

    public static class Config {
        public final ForgeConfigSpec.IntValue helmetArmor;
        public final ForgeConfigSpec.IntValue chestplateArmor;
        public final ForgeConfigSpec.IntValue leggingsArmor;
        public final ForgeConfigSpec.IntValue shoulderpadArmor;
        public final ForgeConfigSpec.IntValue backpackArmor;

        public Config(ForgeConfigSpec.Builder builder) {
            builder.push("armor_values");

            helmetArmor = builder
                    .comment("Base armor value for helmets")
                    .defineInRange("helmet", 3, 0, 100);

            chestplateArmor = builder
                    .comment("Base armor value for chestplates")
                    .defineInRange("chestplate", 8, 0, 100);

            leggingsArmor = builder
                    .comment("Base armor value for leggings")
                    .defineInRange("leggings", 6, 0, 100);

            shoulderpadArmor = builder
                    .comment("Armor value for shoulderpads")
                    .defineInRange("shoulderpads", 2, 0, 100);

            backpackArmor = builder
                    .comment("Armor value for backpacks")
                    .defineInRange("backpacks", 1, 0, 100);

            builder.pop();
        }
    }
}
// This class does literally nothing right now