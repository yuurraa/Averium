package net.yuurraa.averiummod.block;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.yuurraa.averiummod.particle.ModParticles;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ArgonVentBlock extends Block {
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level world, @NotNull BlockPos pos, @NotNull RandomSource rand) {
        // Only emit if space above is empty
        if (world.isEmptyBlock(pos.above())) {

            // Emit 6 particles every tick
            for (int i = 0; i < 6; i++) {
                double x = pos.getX() + rand.nextDouble();
                double y = pos.getY() + 1.01; // just above surface
                double z = pos.getZ() + rand.nextDouble();

                double vx = 0.0;
                double vy = 0.01 + rand.nextDouble() * 0.01;
                double vz = 0.0;

                world.addParticle(
                        ModParticles.ARGON_SMOKE.get(),
                        x, y, z,
                        vx, vy, vz
                );
            }
        }
    }
}
