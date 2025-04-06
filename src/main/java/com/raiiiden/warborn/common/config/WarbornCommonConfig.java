package com.raiiiden.warborn.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class WarbornCommonConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> HELMETS_WITH_NVG;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        HELMETS_WITH_NVG = builder
                .comment("Accepted item IDs for NVG helmets (format: modid:item_name)")
                .defineListAllowEmpty(
                        "nvg_helmets",
                        List.of(
                                "warborn:nato_sqad_leader_helmet",
                                "warborn:nato_ukr_helmet",
                                "warborn:nato_sqad_leader_helmet_woodland",
                                "warborn:nato_ukr_helmet_woodland",
                                "warborn:beta7_nvg_helmet",
                                "warborn:beta7_nvg_helmet_slate",
                                "warborn:beta7_nvg_helmet_ash"
                        ),
                        obj -> obj instanceof String
                );

        SPEC = builder.build();
    }
}
