package com.raiiiden.warborn.common.init;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.block.WorkstationBlock.WorkstationKind;
import com.raiiiden.warborn.common.blockentity.WorkstationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, WARBORN.MODID);

    public static final RegistryObject<BlockEntityType<WorkstationBlockEntity>> INDUSTRIAL_PRESS =
            BLOCK_ENTITIES.register("industrial_press", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new WorkstationBlockEntity(WorkstationKind.INDUSTRIAL_PRESS, pos, state),
                    ModBlockRegistry.INDUSTRIAL_PRESS.get()).build(null));

    public static final RegistryObject<BlockEntityType<WorkstationBlockEntity>> BALLISTICS_BENCH =
            BLOCK_ENTITIES.register("ballistics_bench", () -> BlockEntityType.Builder.of(
                    (pos, state) -> new WorkstationBlockEntity(WorkstationKind.BALLISTICS_BENCH, pos, state),
                    ModBlockRegistry.BALLISTICS_BENCH.get()).build(null));

    public static BlockEntityType<WorkstationBlockEntity> typeFor(WorkstationKind kind) {
        return switch (kind) {
            case INDUSTRIAL_PRESS -> INDUSTRIAL_PRESS.get();
            case BALLISTICS_BENCH -> BALLISTICS_BENCH.get();
        };
    }
}
