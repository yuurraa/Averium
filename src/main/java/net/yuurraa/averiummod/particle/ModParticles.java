// src/main/java/net/yuurraa/averiummod/particle/ModParticles.java
package net.yuurraa.averiummod.particle;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yuurraa.averiummod.AveriumMod;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, AveriumMod.MOD_ID);

    public static final RegistryObject<SimpleParticleType> ARGON_SMOKE =
            PARTICLES.register("argon_smoke", () -> new SimpleParticleType(false));

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}
