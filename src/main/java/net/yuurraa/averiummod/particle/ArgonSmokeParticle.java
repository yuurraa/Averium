// src/main/java/net/yuurraa/averiummod/particle/ArgonSmokeParticle.java
package net.yuurraa.averiummod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class ArgonSmokeParticle extends TextureSheetParticle {
    protected ArgonSmokeParticle(ClientLevel level, double x, double y, double z,
                                 double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.friction = 1.0f;
        this.gravity = 0.0f;
        this.lifetime = 60 + this.random.nextInt(10);
        this.quadSize = 0.1f + this.random.nextFloat() * 0.05f;
        this.setAlpha(0.9f);
        this.pickSprite(sprites);

        // Override lateral velocity to keep it mostly vertical
        this.xd = 0;
        this.zd = 0;
        this.yd = 0.01 + this.random.nextDouble() * 0.06; // gentle float upward

        // Set a random initial rotation (roll)
        this.roll = this.random.nextFloat() * ((float) Math.PI * 2);
        this.oRoll = this.roll;
    }

    @Override
    public void tick() {
        super.tick();
        this.setAlpha(Math.max(0f, this.alpha - 0.015f)); // Fade slowly
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            ArgonSmokeParticle particle = new ArgonSmokeParticle(level, x, y, z, vx, vy, vz, this.sprites);
            particle.setColor(0.0f, 0.0f, 0.5f); // Dark blue color
            return particle;
        }
    }
}
