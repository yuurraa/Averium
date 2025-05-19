package net.yuurraa.averiummod.block;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.RandomSource;

import net.yuurraa.averiummod.block.entity.ArgonVentBlockEntity;
import net.yuurraa.averiummod.particle.ModParticles;
import net.yuurraa.averiummod.item.ModItems;
import net.minecraft.core.Direction;

/**
 * ArgonVentBlock now implements EntityBlock so it can create
 * and interact with its corresponding BlockEntity.
 */
public class ArgonVentBlock extends Block implements EntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public ArgonVentBlock(BlockBehaviour.Properties props) {
        super(props);
        // set default facing
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block,BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    /** Create the BlockEntity when this block is placed. */
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ArgonVentBlockEntity(pos, state);
    }

    /** Only emit particles if the BlockEntity still has Argon remaining. */
    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ArgonVentBlockEntity vent) || !vent.hasArgon()) {
            return;
        }
        if (world.isEmptyBlock(pos.above())) {
            for (int i = 0; i < 6; i++) {
                double x = pos.getX() + rand.nextDouble();
                double y = pos.getY() + 1.01;
                double z = pos.getZ() + rand.nextDouble();
                world.addParticle(
                        ModParticles.ARGON_SMOKE.get(),
                        x, y, z,
                        0, 0.01 + rand.nextDouble() * 0.01, 0
                );
            }
        }
    }

    /**
     * Right‑click (use) the vent with a glass bottle to collect one unit of Argon:
     * - Decrement the BlockEntity’s reservoir
     * - Consume the bottle and give Bottled Argon
     */
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos,
                                 Player player, InteractionHand hand, BlockHitResult hit) {
        if (world.isClientSide) {
            // only short‑circuit if they’re holding a bottle
            ItemStack held = player.getItemInHand(hand);
            if (held.getItem() == Items.GLASS_BOTTLE) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() == Items.GLASS_BOTTLE) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof ArgonVentBlockEntity vent && vent.hasArgon()) {
                // collect one unit
                vent.collectOne();
                BlockState newState = world.getBlockState(pos);
                world.sendBlockUpdated(pos, newState, newState, 3);
                // consume empty bottle
                held.shrink(1);
                // give bottled argon
                player.addItem(new ItemStack(ModItems.BOTTLED_ARGON.get()));
                return InteractionResult.CONSUME;
            }
        }
        return super.use(state, world, pos, player, hand, hit);
    }
}
