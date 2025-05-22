// src/main/java/net/yuurraa/averiummod/block/ModBlocks.java
package net.yuurraa.averiummod.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, AveriumMod.MOD_ID);

    // ARGON VENT
    public static final RegistryObject<Block> ARGON_VENT = registerBlock("argon_vent",
            () -> new ArgonVentBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).requiresCorrectToolForDrops()));
    // XENON VENT
    public static final RegistryObject<Block> XENON_VENT = registerBlock("xenon_vent",
            () -> new XenonVentBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE).requiresCorrectToolForDrops()));

    // CRYTHON ORE
    public static final RegistryObject<Block> CRYTHON_ORE = registerBlock("crython_ore",
            () -> new CrythonOreBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(30.0F, 1200.0F)
                    .sound(SoundType.STONE)
            ));
    public static final RegistryObject<Block> DEEPSLATE_CRYTHON_ORE = registerBlock("deepslate_crython_ore",
            () -> new CrythonOreBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.DEEPSLATE)
                    .instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(45.0F, 1200.0F)
                    .sound(SoundType.DEEPSLATE)
            ));

    public static final RegistryObject<Block> INFERNIUM_ORE = registerBlock("infernium_ore",
            () -> new InferniumOreBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .instrument(net.minecraft.world.level.block.state.properties.NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops()
                    .strength(20.0F, 1200.0F)
                    .sound(SoundType.NETHER_ORE)
            ));

    // INERT INFUSER
    public static final RegistryObject<Block> INERT_INFUSER = registerBlock("inert_infuser",
            () -> new InertInfuserBlock(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK) // Or Blocks.IRON_BLOCK etc.
                    .strength(5.0f, 6.0f) // Example strength
                    .requiresCorrectToolForDrops()
                    .noOcclusion())); // Add .noOcclusion() if you plan to have transparent parts and don't want it to cull adjacent block faces unnecessarily


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}