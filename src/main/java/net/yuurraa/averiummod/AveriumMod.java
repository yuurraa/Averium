// src/main/java/net/yuurraa/averiummod/AveriumMod.java
package net.yuurraa.averiummod;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.yuurraa.averiummod.block.ModBlocks;
import net.yuurraa.averiummod.block.entity.ModBlockEntities;
import net.yuurraa.averiummod.item.ModCreativeModTabs;
import net.yuurraa.averiummod.item.ModItems;
import net.yuurraa.averiummod.particle.ModParticles;
// No specific worldgen imports needed here for registration itself
import net.yuurraa.averiummod.recipe.ModRecipeTypes;
import net.yuurraa.averiummod.screen.ModMenuTypes;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.*;

@Mod(AveriumMod.MOD_ID)
public class AveriumMod {
    public static final String MOD_ID = "averiummod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public AveriumMod() { // Forge will typically inject FMLJavaModLoadingContext if this constructor is used.
        // Or you can use FMLJavaModLoadingContext.get().getModEventBus() directly.
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus(); // Common practice

        ModCreativeModTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModParticles.register(modEventBus);
        ModMenuTypes.register(modEventBus);;
        ModRecipeTypes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this); // For server events, etc.
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::enqueueCurioSlot);

        // The DataGenerators class will be picked up automatically by Forge
        // due to @Mod.EventBusSubscriber, so no explicit registration call needed here for it.
    }

    private void enqueueCurioSlot(final InterModEnqueueEvent event) {
        // REGISTER the 'charm' slot type using the built-in preset
        InterModComms.sendTo(
                CuriosApi.MODID,
                SlotTypeMessage.REGISTER_TYPE,
                () -> SlotTypePreset.CHARM.getMessageBuilder().build()
        );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Any common setup logic
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Server starting logic
    }
}