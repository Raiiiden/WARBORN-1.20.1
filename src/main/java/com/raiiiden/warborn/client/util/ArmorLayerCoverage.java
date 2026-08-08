package com.raiiiden.warborn.client.util;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.client.model.WarbornGenericArmorModel;
import com.raiiiden.warborn.common.item.WBArmorItem;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

// Walks a baked geo model once to find which body regions it covers, so second-skin hiding follows the model instead of a hand-kept list.
public final class ArmorLayerCoverage {
    public enum Region { HEAD, CHEST, LEGS }

    // Which body region a bone belongs to; both armor* bones and their biped* parents are listed, since cubes parented straight to a root render in every slot.
    private static final Map<String, Region> BONE_REGIONS = Map.ofEntries(
            Map.entry("bipedHead", Region.HEAD),
            Map.entry("armorHead", Region.HEAD),
            Map.entry("bipedBody", Region.CHEST),
            Map.entry("armorBody", Region.CHEST),
            Map.entry("bipedRightArm", Region.CHEST),
            Map.entry("armorRightArm", Region.CHEST),
            Map.entry("bipedLeftArm", Region.CHEST),
            Map.entry("armorLeftArm", Region.CHEST),
            Map.entry("bipedRightLeg", Region.LEGS),
            Map.entry("armorRightLeg", Region.LEGS),
            Map.entry("armorRightBoot", Region.LEGS),
            Map.entry("bipedLeftLeg", Region.LEGS),
            Map.entry("armorLeftLeg", Region.LEGS),
            Map.entry("armorLeftBoot", Region.LEGS)
    );

    private static final Map<Item, Set<Region>> CACHE = new IdentityHashMap<>();

    private ArmorLayerCoverage() {}

    public static boolean covers(WBArmorItem item, Region region) {
        return regions(item).contains(region);
    }

    // Models and their bones are rebuilt on reload, so the cached answers go with them.
    public static void clear() {
        CACHE.clear();
    }

    private static Set<Region> regions(WBArmorItem item) {
        Set<Region> cached = CACHE.get(item);
        if (cached != null) return cached;

        Set<Region> regions = EnumSet.noneOf(Region.class);
        try {
            WarbornGenericArmorModel model = new WarbornGenericArmorModel(item);
            BakedGeoModel baked = model.getBakedModel(model.getModelResource(item));
            if (baked != null) {
                for (GeoBone bone : baked.topLevelBones()) {
                    collect(bone, null, regions);
                }
            }
        } catch (Exception exception) {
            WARBORN.LOGGER.error("Failed to read body coverage for {}", item, exception);
        }

        CACHE.put(item, regions);
        return regions;
    }

    private static void collect(GeoBone bone, Region inherited, Set<Region> regions) {
        Region region = BONE_REGIONS.getOrDefault(bone.getName(), inherited);

        if (region != null && !bone.getCubes().isEmpty()) {
            regions.add(region);
        }
        for (GeoBone child : bone.getChildBones()) {
            collect(child, region, regions);
        }
    }
}
