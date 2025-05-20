// src/main/java/net/yuurraa/averiummod/particle/XenonGasParticle.java
package net.yuurraa.averiummod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class XenonGasParticle extends TextureSheetParticle {
    protected XenonGasParticle(ClientLevel level, double x, double y, double z,
                               double vx, double vy, double vz, SpriteSet sprites) {
        super(level, x, y, z, vx, vy, vz);
        this.friction = 1.0f;
        this.gravity = 0.0f; // Gas, so no gravity
        this.lifetime = 50 + this.random.nextInt(20); // Slightly different lifetime from Argon
        this.quadSize = 0.12f + this.random.nextFloat() * 0.06f; // Slightly different size
        this.setAlpha(0.85f);
        this.pickSprite(sprites);

        // Similar upward drift
        this.xd = (this.random.nextDouble() - 0.5D) * 0.02D; // Slight horizontal drift
        this.zd = (this.random.nextDouble() - 0.5D) * 0.02D; // Slight horizontal drift
        this.yd = 0.02 + this.random.nextDouble() * 0.05; // Gentle float upward

        this.roll = this.random.nextFloat() * ((float) Math.PI * 2F);
        this.oRoll = this.roll;
    }

    @Override
    public void tick() {
        super.tick();
        this.setAlpha(Math.max(0f, this.alpha - 0.018f)); // Slightly different fade
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
            XenonGasParticle particle = new XenonGasParticle(level, x, y, z, vx, vy, vz, this.sprites);
            // Bluish color for Xenon
            particle.setColor(0.1f, 0.6f, 0.9f); // Example: A nice light/sky blue
            return particle;
        }
    }
}