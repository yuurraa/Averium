// src/main/java/net/yuurraa/averiummod/block/ArgonVentBlock.java
package net.yuurraa.averiummod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.RandomSource;

import net.yuurraa.averiummod.block.entity.ArgonVentBlockEntity;
import net.yuurraa.averiummod.block.entity.ModBlockEntities;
import net.yuurraa.averiummod.particle.ModParticles;
import net.yuurraa.averiummod.item.ModItems;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public class ArgonVentBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ArgonVentBlock(BlockBehaviour.Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArgonVentBlockEntity(pos, state);
    }

    // Provide the ticker for the BlockEntity
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        // We only want server-side ticking for regeneration
        if (level.isClientSide()) {
            return null;
        }
        BlockEntityType<ArgonVentBlockEntity> expectedType = ModBlockEntities.ARGON_VENT.get();
        BlockEntityTicker<ArgonVentBlockEntity> argonVentTicker = ArgonVentBlockEntity::tick;

        if (blockEntityType == expectedType) {
            return (BlockEntityTicker<T>) argonVentTicker;
        } else {
            return null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ArgonVentBlockEntity vent) || !vent.hasArgon()) {
            return;
        }
        if (world.isEmptyBlock(pos.above())) {
            for (int i = 0; i < 4; i++) {
                double x = pos.getX() + rand.nextDouble();
                double y = pos.getY() + 1.01D;
                double z = pos.getZ() + rand.nextDouble();
                world.addParticle(
                        ModParticles.ARGON_SMOKE.get(),
                        x, y, z,
                        0, 0.01 + rand.nextDouble() * 0.015, 0
                );
            }
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItemStack = player.getItemInHand(hand);

        if (heldItemStack.is(ModItems.GAS_CELL.get())) { // Check if holding an Empty Gas Cell
            if (world.isClientSide) {
                return InteractionResult.SUCCESS; // Client optimistic
            }

            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ArgonVentBlockEntity vent && vent.hasArgon()) {
                vent.collectOne(); // Consume one unit of gas from the vent BE

                // Consume one Empty Gas Cell from player's hand
                if (!player.getAbilities().instabuild) {
                    heldItemStack.shrink(1);
                }

                // Give player one Argon Gas Cell
                ItemStack filledCellStack = new ItemStack(ModItems.ARGON_GAS_CELL.get());
                if (!player.addItem(filledCellStack)) { // If inventory is full
                    player.drop(filledCellStack, false); // Drop it in the world
                }
                world.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 0.7F, 1.2F + world.random.nextFloat() * 0.2F);
                return InteractionResult.CONSUME;
            } else {
                // Vent is empty or not the right type
                return InteractionResult.FAIL;
            }
        }
        return InteractionResult.PASS; // If not holding an Empty Gas Cell
    }
}