// src/main/java/net/yuurraa/averiummod/ClientModEvents.java
package net.yuurraa.averiummod;

import net.minecraft.client.gui.screens.MenuScreens; // Add this import
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent; // Add this import
import net.yuurraa.averiummod.particle.CryoParticle;
import net.yuurraa.averiummod.particle.ModParticles;
import net.yuurraa.averiummod.particle.ArgonSmokeParticle;
import net.yuurraa.averiummod.particle.XenonGasParticle;
import net.yuurraa.averiummod.screen.InertInfuserScreen; // Add this import
import net.yuurraa.averiummod.screen.ModMenuTypes; // Add this import

@Mod.EventBusSubscriber(modid = AveriumMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    // This is from your main mod class, ensure it's here or in a similar client setup event
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        System.out.println("AveriumMod: FMLClientSetupEvent fired!");
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.INERT_INFUSER_MENU.get(), InertInfuserScreen::new);
        });
    }

    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(
                ModParticles.ARGON_SMOKE.get(),
                ArgonSmokeParticle.Factory::new);
        event.registerSpriteSet(
                ModParticles.XENON_GAS.get(),
                XenonGasParticle.Factory::new);
        event.registerSpriteSet(
                ModParticles.CRYO_PARTICLE.get(),
                CryoParticle.Factory::new);
    }
}