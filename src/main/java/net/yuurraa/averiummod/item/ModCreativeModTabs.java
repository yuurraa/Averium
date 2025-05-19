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
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.CRYTHON.get()))
                    .title(Component.translatable("creativetab.averium_tab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.ARGON_VENT.get());

                        pOutput.accept(ModItems.RAW_CRYTHON.get());
                        pOutput.accept(ModItems.CRYTHON.get());

                        pOutput.accept(ModItems.GOLD_REINFORCED_STICK.get());
                        pOutput.accept(ModItems.BOTTLED_ARGON.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
