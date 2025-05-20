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

    // Argon
    public static final RegistryObject<SimpleParticleType> ARGON_SMOKE =
            PARTICLES.register("argon_smoke", () -> new SimpleParticleType(false));
    // Xenon
    public static final RegistryObject<SimpleParticleType> XENON_GAS =
            PARTICLES.register("xenon_gas", () -> new SimpleParticleType(false));

    // Crython
    public static final RegistryObject<SimpleParticleType> CRYO_PARTICLE =
            PARTICLES.register("cryo_particle", () -> new SimpleParticleType(true));

    public static void register(IEventBus eventBus) {
        PARTICLES.register(eventBus);
    }
}