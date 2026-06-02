package com.raiiiden.warborn.common.init;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.blockentity.WorkstationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WARBORN.MODID);

    public static final RegistryObject<BlockEntityType<WorkstationBlockEntity>> INDUSTRIAL_PRESS =
            BLOCK_ENTITIES.register("industrial_press", () -> BlockEntityType.Builder.of(
                    (pos, state) -> WorkstationBlockEntity.industrialPress(pos, state),
                    ModBlockRegistry.INDUSTRIAL_PRESS.get()).build(null));

    public static final RegistryObject<BlockEntityType<WorkstationBlockEntity>> BALLISTICS_BENCH =
            BLOCK_ENTITIES.register("ballistics_bench", () -> BlockEntityType.Builder.of(
                    (pos, state) -> WorkstationBlockEntity.ballisticsBench(pos, state),
                    ModBlockRegistry.BALLISTICS_BENCH.get()).build(null));

    public static final RegistryObject<BlockEntityType<WorkstationBlockEntity>> COMPOSITE_FABRICATOR =
            BLOCK_ENTITIES.register("composite_fabricator", () -> BlockEntityType.Builder.of(
                    (pos, state) -> WorkstationBlockEntity.compositeFabricator(pos, state),
                    ModBlockRegistry.COMPOSITE_FABRICATOR.get()).build(null));
}
