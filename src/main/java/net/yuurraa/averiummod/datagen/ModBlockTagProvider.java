// src/main/java/net/yuurraa/averiummod/datagen/ModBlockTagProvider.java
package net.yuurraa.averiummod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.block.ModBlocks;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, AveriumMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Argon Vent
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.ARGON_VENT.get());
        // Xenon Vent
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.XENON_VENT.get());

        // Crython Ores
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CRYTHON_ORE.get())
                .add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get())
                .add(ModBlocks.ARGON_VENT.get()); // Argon Vent is also pickaxe mineable
        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.CRYTHON_ORE.get())
                .add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get());
        this.tag(Tags.Blocks.ORES)
                .add(ModBlocks.CRYTHON_ORE.get())
                .add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get());
        this.tag(BlockTags.create(new ResourceLocation(AveriumMod.MOD_ID, "ores/crython")))
                .add(ModBlocks.CRYTHON_ORE.get())
                .add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE)
                .add(ModBlocks.CRYTHON_ORE.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
                .add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get());

        // Infernium Ore
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.INFERNIUM_ORE.get());
        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.INFERNIUM_ORE.get());
        this.tag(Tags.Blocks.ORES)
                .add(ModBlocks.INFERNIUM_ORE.get());
        this.tag(BlockTags.create(new ResourceLocation(AveriumMod.MOD_ID, "ores/infernium"))) // Your specific ore tag
                .add(ModBlocks.INFERNIUM_ORE.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_NETHERRACK)
                .add(ModBlocks.INFERNIUM_ORE.get());
    }
}