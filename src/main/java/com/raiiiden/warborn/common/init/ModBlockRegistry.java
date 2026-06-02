package com.raiiiden.warborn.common.init;

import com.raiiiden.warborn.WARBORN;
import com.raiiiden.warborn.common.block.WorkstationBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, WARBORN.MODID);

    public static final RegistryObject<Block> INDUSTRIAL_PRESS = registerWorkstation("industrial_press", WorkstationBlock.WorkstationKind.INDUSTRIAL_PRESS);
    public static final RegistryObject<Block> BALLISTICS_BENCH = registerWorkstation("ballistics_bench", WorkstationBlock.WorkstationKind.BALLISTICS_BENCH);
    public static final RegistryObject<Block> COMPOSITE_FABRICATOR = registerWorkstation("composite_fabricator", WorkstationBlock.WorkstationKind.COMPOSITE_FABRICATOR);

    private static RegistryObject<Block> registerWorkstation(String name, WorkstationBlock.WorkstationKind kind) {
        RegistryObject<Block> block = BLOCKS.register(name, () -> new WorkstationBlock(kind, BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL)));
        ModItemRegistry.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }
}
