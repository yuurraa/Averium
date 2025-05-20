// src/main/java/net/yuurraa/averiummod/ClientModEvents.java
package net.yuurraa.averiummod;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yuurraa.averiummod.particle.CryoParticle;
import net.yuurraa.averiummod.particle.ModParticles;
import net.yuurraa.averiummod.particle.ArgonSmokeParticle;

@Mod.EventBusSubscriber(modid = AveriumMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
                ModParticles.ARGON_SMOKE.get(),
                ArgonSmokeParticle.Factory::new);
        event.registerSpriteSet(
                ModParticles.CRYO_PARTICLE.get(),
                CryoParticle.Factory::new);
    }
}
