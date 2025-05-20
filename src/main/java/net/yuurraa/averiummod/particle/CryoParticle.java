// src/main/java/net/yuurraa/averiummod/particle/CryoParticle.java
package net.yuurraa.averiummod.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import org.jetbrains.annotations.NotNull;

public class CryoParticle extends TextureSheetParticle {

    protected CryoParticle(ClientLevel level, double x, double y, double z,
                           double vx, double vy, double vz, SpriteSet spriteSet) {
        super(level, x, y, z, vx, vy, vz);

        this.friction = 0.98F; // Slows down gradually
        this.gravity = 0.008F;   // Very slight downward pull, adjust for desired floatiness (0 for no pull, negative for upward)
        this.lifetime = 50 + this.random.nextInt(40); // Lifetime of 1.5 to 3 seconds

        // Much smaller initial random velocity for less "explosive" movement
        this.xd = (this.random.nextDouble() * 2.0D - 1.0D) * 0.015D; // Gentle horizontal drift
        this.yd = (this.random.nextDouble() * 2.0D - 1.0D) * 0.01D;  // Gentle vertical drift
        this.zd = (this.random.nextDouble() * 2.0D - 1.0D) * 0.015D; // Gentle horizontal drift

        this.quadSize *= 0.6F + this.random.nextFloat() * 0.3F; // Smaller and less variable size

        this.pickSprite(spriteSet);

        // Light blue / cyan color
        this.rCol = 0.6f + random.nextFloat() * 0.1f; // Reduced randomness for more consistent color
        this.gCol = 0.8f + random.nextFloat() * 0.1f;
        this.bCol = 0.95f + random.nextFloat() * 0.05f; // Closer to pure light blue/cyan

        this.setAlpha(0.7f + this.random.nextFloat() * 0.3f); // Initial alpha between 0.6 and 0.9

        // Set a random initial rotation (roll)
        this.roll = this.random.nextFloat() * ((float) Math.PI * 2);
        this.oRoll = this.roll;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.yd -= this.gravity; // Apply gravity
        this.move(this.xd, this.yd, this.zd); // Move the particle

        // Apply friction
        this.xd *= this.friction;
        this.yd *= this.friction; // Friction on Y can make it float a bit more if gravity is low
        this.zd *= this.friction;

        // Stop vertical movement if on ground and gravity was pulling down
        if (this.onGround && this.gravity > 0.0f) {
            this.yd = 0.0D;
        }

        // Fade out: reduce alpha gradually over its lifetime
        // This calculation ensures it fades from its initial alpha to 0 over its lifetime.
        if (this.lifetime > 0) { // Avoid division by zero if lifetime somehow is 0
            float initialAlpha = 1.0f; // Assume we started with alpha 1 conceptually before this.alpha was set
            // Or, better, use the actual alpha at age 0.
            // For simplicity, let's make it fade a fixed amount.
            this.alpha = Math.max(0.0f, this.alpha - (0.7f / (float)this.lifetime) ); // Fades from ~0.7-1.0 towards 0
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Factory(SpriteSet spriteSet) {
            this.sprites = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel levelIn,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            // Note: The xd, yd, zd passed here are often 0 unless specified by addParticle
            // Our constructor adds its own random initial velocities.
            return new CryoParticle(levelIn, x, y, z, xd, yd, zd, this.sprites);
        }
    }
}