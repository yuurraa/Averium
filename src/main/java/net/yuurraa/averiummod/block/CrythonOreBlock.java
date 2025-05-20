// src/main/java/net/yuurraa/averiummod/block/CrythonOreBlock.java
package net.yuurraa.averiummod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.yuurraa.averiummod.particle.ModParticles;
import net.minecraft.core.Direction;


public class CrythonOreBlock extends Block {

    private static final int MINING_FATIGUE_DURATION = 20 * 4; // 4 seconds
    private static final int MINING_FATIGUE_AMPLIFIER = 0; // Mining Fatigue I

    public CrythonOreBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        // This method is called on both client and server when a player starts "attacking" (left-clicking) a block.
        // We only want to apply the effect on the server side.
        if (!pLevel.isClientSide) {
            // Apply Mining Fatigue I to the player
            pPlayer.addEffect(new MobEffectInstance(
                    MobEffects.DIG_SLOWDOWN,
                    MINING_FATIGUE_DURATION,
                    MINING_FATIGUE_AMPLIFIER,
                    false, // ambient
                    true,  // visible particles
                    true   // show icon
            ));
        }
        super.attack(pState, pLevel, pPos, pPlayer); // Call super if it has any relevant logic (Block.attack is empty by default)
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos posIn, RandomSource randIn) {
        // Your existing particle logic
        super.animateTick(stateIn, levelIn, posIn, randIn);
        if (randIn.nextInt(5) == 0) {
            for (Direction direction : Direction.values()) {
                BlockPos adjacentPos = posIn.relative(direction);
                if (!levelIn.getBlockState(adjacentPos).isSolidRender(levelIn, adjacentPos)) {
                    double xOffset = direction.getStepX() == 0 ? randIn.nextDouble() : 0.5D + direction.getStepX() * 0.53D;
                    double yOffset = direction.getStepY() == 0 ? randIn.nextDouble() : 0.5D + direction.getStepY() * 0.53D;
                    double zOffset = direction.getStepZ() == 0 ? randIn.nextDouble() : 0.5D + direction.getStepZ() * 0.53D;
                    double particleX = (double)posIn.getX() + xOffset;
                    double particleY = (double)posIn.getY() + yOffset;
                    double particleZ = (double)posIn.getZ() + zOffset;
                    levelIn.addParticle(ModParticles.CRYO_PARTICLE.get(), particleX, particleY, particleZ, 0.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}