// src/main/java/net/yuurraa/averiummod/block/XenonVentBlock.java
package net.yuurraa.averiummod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.yuurraa.averiummod.block.entity.ArgonVentBlockEntity;
import net.yuurraa.averiummod.block.entity.ModBlockEntities;
import net.yuurraa.averiummod.block.entity.XenonVentBlockEntity;
import net.yuurraa.averiummod.item.ModItems;
import net.yuurraa.averiummod.particle.ModParticles;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class XenonVentBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public XenonVentBlock(BlockBehaviour.Properties props) {
        super(props);
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new XenonVentBlockEntity(pos, state);
    }

    // Provide the ticker for the BlockEntity
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide()) {
            return null;
        }
        BlockEntityType<XenonVentBlockEntity> expectedType = ModBlockEntities.XENON_VENT.get();
        BlockEntityTicker<XenonVentBlockEntity> xenonVentTicker = XenonVentBlockEntity::tick;

        if (blockEntityType == expectedType) {
            return (BlockEntityTicker<T>) xenonVentTicker;
        } else {
            return null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof XenonVentBlockEntity vent) || !vent.hasXenon()) {
            return;
        }
        // Spawn Xenon particles (bluish)
        if (world.isEmptyBlock(pos.above())) { // Only if space above is clear
            for (int i = 0; i < 4; i++) { // Number of particles
                double x = pos.getX() + rand.nextDouble();
                double y = pos.getY() + 1.01D; // Slightly above the block
                double z = pos.getZ() + rand.nextDouble();
                world.addParticle(
                        ModParticles.XENON_GAS.get(), // Use Xenon particle
                        x, y, z,
                        (rand.nextDouble() - 0.5D) * 0.02D, // Slight horizontal drift
                        0.01 + rand.nextDouble() * 0.015,    // Gentle upward float
                        (rand.nextDouble() - 0.5D) * 0.02D  // Slight horizontal drift
                );
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) {
            return player.getItemInHand(hand).getItem() == Items.GLASS_BOTTLE ? InteractionResult.sidedSuccess(true) : InteractionResult.PASS;
        }

        ItemStack heldItemStack = player.getItemInHand(hand);
        if (heldItemStack.getItem() == Items.GLASS_BOTTLE) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof XenonVentBlockEntity vent && vent.hasXenon()) {
                vent.collectOneXenon();

                if (!player.getAbilities().instabuild) {
                    heldItemStack.shrink(1);
                }

                ItemStack bottledXenonStack = new ItemStack(ModItems.BOTTLED_XENON.get());
                if (!player.addItem(bottledXenonStack)) {
                    player.drop(bottledXenonStack, false);
                }
                return InteractionResult.CONSUME;
            } else {
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS;
    }
}