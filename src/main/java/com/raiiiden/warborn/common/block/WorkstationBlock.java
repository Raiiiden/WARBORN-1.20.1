package com.raiiiden.warborn.common.block;

import com.raiiiden.warborn.common.blockentity.WorkstationBlockEntity;
import com.raiiiden.warborn.common.init.ModBlockEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class WorkstationBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    // Set while the station is actively working, so the model can show a running machine.
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private final WorkstationKind kind;

    public WorkstationBlock(WorkstationKind kind, Properties properties) {
        super(properties);
        this.kind = kind;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, false));
    }

    public WorkstationKind getKind() {
        return kind;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof WorkstationBlockEntity blockEntity) {
            NetworkHooks.openScreen(serverPlayer, blockEntity, blockEntity::writeScreenOpeningData);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // Spits the station's contents out rather than voiding them when it is broken.
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof WorkstationBlockEntity blockEntity) {
                Containers.dropContents(level, pos, blockEntity);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WorkstationBlockEntity(kind, pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null;
        return createTickerHelper(blockEntityType, ModBlockEntityRegistry.typeFor(kind), WorkstationBlockEntity::serverTick);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof WorkstationBlockEntity blockEntity) {
            return net.minecraft.world.inventory.AbstractContainerMenu.getRedstoneSignalFromContainer(blockEntity);
        }
        return 0;
    }

    @SuppressWarnings("unused")
    public static boolean isLit(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).getValue(LIT);
    }

    public enum WorkstationKind {
        // Turns vanilla resources into raw materials. Burns fuel, so it takes real time.
        INDUSTRIAL_PRESS("industrial_press", 3, true, 200),
        // Assembles materials into finished gear. No fuel, but still not instant.
        BALLISTICS_BENCH("ballistics_bench", 9, false, 100);

        private final String name;
        private final int inputSlots;
        private final boolean hasFuel;
        private final int defaultProcessTime;

        WorkstationKind(String name, int inputSlots, boolean hasFuel, int defaultProcessTime) {
            this.name = name;
            this.inputSlots = inputSlots;
            this.hasFuel = hasFuel;
            this.defaultProcessTime = defaultProcessTime;
        }

        public String getName() {
            return name;
        }

        public Component title() {
            return Component.translatable("container.fracturepoint." + name);
        }

        public int inputSlots() {
            return inputSlots;
        }

        public boolean hasFuel() {
            return hasFuel;
        }

        // Only meaningful when {@link #hasFuel()}; sits directly after the inputs.
        public int fuelSlot() {
            return inputSlots;
        }

        public int outputSlot() {
            return inputSlots + (hasFuel ? 1 : 0);
        }

        public int totalSlots() {
            return outputSlot() + 1;
        }

        public int defaultProcessTime() {
            return defaultProcessTime;
        }

        public static WorkstationKind byOrdinal(int ordinal) {
            WorkstationKind[] values = values();
            return ordinal >= 0 && ordinal < values.length ? values[ordinal] : INDUSTRIAL_PRESS;
        }
    }
}
