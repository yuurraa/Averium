// src/main/java/net/yuurraa/averiummod/item/ModCreativeModTabs.java
package net.yuurraa.averiummod.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.block.ModBlocks;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AveriumMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> AVERIUM_TAB = CREATIVE_MODE_TABS.register("averium_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.UNSTABLE_INFERNIUM.get()))
                    .title(Component.translatable("creativetab.averium_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.ARGON_VENT.get());
                        pOutput.accept(ModBlocks.XENON_VENT.get());
                        pOutput.accept(ModBlocks.CRYTHON_ORE.get());
                        pOutput.accept(ModBlocks.DEEPSLATE_CRYTHON_ORE.get());
                        pOutput.accept(ModBlocks.INFERNIUM_ORE.get());

                        pOutput.accept(ModItems.FROST_EBBER.get());
                        pOutput.accept(ModItems.OBSIDIAN_STAKE.get());

                        pOutput.accept(ModItems.RAW_CRYTHON.get());
                        pOutput.accept(ModItems.UNSTABLE_CRYTHON.get());
                        pOutput.accept(ModItems.STABLE_CRYTHON.get());

                        pOutput.accept(ModItems.RAW_INFERNIUM.get());
                        pOutput.accept(ModItems.UNSTABLE_INFERNIUM.get());
                        pOutput.accept(ModItems.STABLE_INFERNIUM.get());

                        pOutput.accept(ModItems.GOLD_REINFORCED_STICK.get());

                        pOutput.accept(ModItems.GAS_CELL.get());
                        pOutput.accept(ModItems.ARGON_GAS_CELL.get());
                        pOutput.accept(ModItems.XENON_GAS_CELL.get());
                        pOutput.accept(ModItems.XENON_MATRIX.get());
                        pOutput.accept(ModItems.CHARGED_XENON_MATRIX.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
