// src/main/java/net/yuurraa/averiummod/block/InferniumOreBlock.java
package net.yuurraa.averiummod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction; // Keep if using face-specific logic
import net.minecraft.core.particles.ParticleTypes; // Import this!
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
// import net.minecraft.world.level.block.SoundType; // Not directly used here, but fine in class
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
// import net.minecraft.world.level.material.MapColor; // Not directly used here
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class InferniumOreBlock extends Block {
    public InferniumOreBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos posIn, RandomSource randIn) {
        super.animateTick(stateIn, levelIn, posIn, randIn); // Good practice to call super

        // Chance to spawn a particle: 1 in 'X' ticks. Adjust X for frequency.
        // Lower X = more particles.
        if (randIn.nextInt(3) == 0) {

            // Option 1: Spawn randomly on any exposed face (more complex, more realistic)
            for (Direction direction : Direction.values()) {
                BlockPos adjacentPos = posIn.relative(direction);
                // Check if the adjacent block doesn't fully block the face
                if (!levelIn.getBlockState(adjacentPos).isSolidRender(levelIn, adjacentPos)) {
                    // Calculate offset to be on the surface of the face
                    double xOffset = direction.getStepX() == 0 ? randIn.nextDouble() : 0.5D + direction.getStepX() * 0.51D; // 0.51 to be just outside
                    double yOffset = direction.getStepY() == 0 ? randIn.nextDouble() : 0.5D + direction.getStepY() * 0.51D;
                    double zOffset = direction.getStepZ() == 0 ? randIn.nextDouble() : 0.5D + direction.getStepZ() * 0.51D;

                    double particleX = (double)posIn.getX() + xOffset;
                    double particleY = (double)posIn.getY() + yOffset;
                    double particleZ = (double)posIn.getZ() + zOffset;

                    // Small initial velocity pointing away from the block face
                    double vx = direction.getStepX() * 0.01D;
                    double vy = direction.getStepY() * 0.01D;
                    double vz = direction.getStepZ() * 0.01D;

                    levelIn.addParticle(ParticleTypes.SMALL_FLAME, particleX, particleY, particleZ, vx, vy, vz);

                    // Optionally, add some smoke too for a smoldering effect
                    if (randIn.nextInt(3) == 0) { // Less frequent than flames
                        levelIn.addParticle(ParticleTypes.SMOKE, particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
                    }
                }
            }
        }
    }
}