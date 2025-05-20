// src/main/java/net/yuurraa/averiummod/block/InferniumOreBlock.java
package net.yuurraa.averiummod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
// No particle imports needed unless you add particles to this ore block later

public class InferniumOreBlock extends Block {
    public InferniumOreBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    /*
    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos posIn, RandomSource randIn) {
        // Example: spawn some flame or smoke particles
        if (randIn.nextInt(10) == 0) {
            double x = (double)posIn.getX() + randIn.nextDouble();
            double y = (double)posIn.getY() + randIn.nextDouble();
            double z = (double)posIn.getZ() + randIn.nextDouble();
            // levelIn.addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.0D, 0.0D);
        }
    }
     */
}