// src/main/java/net/yuurraa/averiummod/block/CrythonOreBlock.java
package net.yuurraa.averiummod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.yuurraa.averiummod.item.ModItems; // Import ModItems
import net.yuurraa.averiummod.particle.ModParticles;
import top.theillusivec4.curios.api.CuriosApi; // Import CuriosApi

public class CrythonOreBlock extends Block {

    private static final int MINING_FATIGUE_DURATION = 20 * 4; // 4 seconds
    private static final int MINING_FATIGUE_AMPLIFIER = 0; // Mining Fatigue I

    public CrythonOreBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        if (!pLevel.isClientSide) {
            // Check if the player has the Frost Ebber equipped
            boolean hasFrostEbber = CuriosApi.getCuriosHelper()
                    .findFirstCurio(pPlayer, ModItems.FROST_EBBER.get())
                    .isPresent();

            if (!hasFrostEbber) { // Apply Mining Fatigue ONLY IF the charm is NOT equipped
                pPlayer.addEffect(new MobEffectInstance(
                        MobEffects.DIG_SLOWDOWN,
                        MINING_FATIGUE_DURATION,
                        MINING_FATIGUE_AMPLIFIER,
                        false, true, true
                ));
            }
        }
        super.attack(pState, pLevel, pPos, pPlayer);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, Level levelIn, BlockPos posIn, RandomSource randIn) {
        // ... (your existing particle logic) ...
        super.animateTick(stateIn, levelIn, posIn, randIn);
        if (randIn.nextInt(3) == 0) {
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