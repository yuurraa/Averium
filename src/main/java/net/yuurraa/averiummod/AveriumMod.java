// src/main/java/net/yuurraa/averiummod/AveriumMod.java
package net.yuurraa.averiummod;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
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
import net.yuurraa.averiummod.item.InertCharmItem;
import net.yuurraa.averiummod.item.ModCreativeModTabs;
import net.yuurraa.averiummod.item.ModItems;
import net.yuurraa.averiummod.particle.ModParticles;
// No specific worldgen imports needed here for registration itself
import org.slf4j.Logger;
import top.theillusivec4.curios.api.*;
import top.theillusivec4.curios.api.type.capability.ICurio;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            // Example: event.accept(ModItems.CRYTHON);
        }
        // Add your argon_vent block item to a creative tab
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS || event.getTabKey() == ModCreativeModTabs.AVERIUM_TAB.getKey()) {
            ModBlocks.ARGON_VENT.get();
            if (ModBlocks.ARGON_VENT.get().asItem() != net.minecraft.world.item.Items.AIR) {
                event.accept(ModBlocks.ARGON_VENT.get());
            }
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Server starting logic
    }
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Client-specific setup
        }
    }
}