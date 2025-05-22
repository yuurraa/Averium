// src/main/java/net/yuurraa/averiummod/block/InertInfuserBlock.java
package net.yuurraa.averiummod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Containers; // Import for dropping items
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkHooks;
import net.yuurraa.averiummod.block.entity.InertInfuserBlockEntity;
import net.yuurraa.averiummod.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class InertInfuserBlock extends BaseEntityBlock { // Changed from Block to BaseEntityBlock
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public InertInfuserBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    /* Block Entity Stuff */
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL; // Use MODEL for normal blocks, ENTITYBLOCK_ANIMATED for TESRs
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new InertInfuserBlockEntity(pPos, pState); // Now directly instantiates
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos,
                                 Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof MenuProvider) { // Check for MenuProvider (which InertInfuserBlockEntity implements)
                NetworkHooks.openScreen((ServerPlayer)pPlayer, (MenuProvider)entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        // We only want server-side ticking for logic
        if(pLevel.isClientSide()) {
            return null;
        }
        // Provide the ticker for our block entity
        return createTickerHelper(pBlockEntityType, ModBlockEntities.INERT_INFUSER.get(),
                InertInfuserBlockEntity::tick);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof InertInfuserBlockEntity be) {
                // Call the drops method we will fully implement in the BE
                // For now, a basic implementation to drop inventory contents:
                if (be.getCapability(ForgeCapabilities.ITEM_HANDLER).isPresent()) {
                    IItemHandler itemHandler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElseThrow(RuntimeException::new);
                    SimpleContainer container = new SimpleContainer(itemHandler.getSlots());
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        container.setItem(i, itemHandler.getStackInSlot(i));
                    }
                    Containers.dropContents(pLevel, pPos, container);
                    pLevel.updateNeighbourForOutputSignal(pPos, this); // Update comparators
                }
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }
}