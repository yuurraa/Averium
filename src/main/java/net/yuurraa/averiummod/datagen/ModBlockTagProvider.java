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
        // Tag Crython Ores as mineable with pickaxe (existing)
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CRYTHON_ORE.get())
                .add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get())
                .add(ModBlocks.ARGON_VENT.get()); // Argon Vent is also pickaxe mineable

        // Tag Crython Ores as needing Diamond tool (existing)
        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.CRYTHON_ORE.get())
                .add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get());

        // NEW: Tag Argon Vent as needing Stone tool
        // This will add "averiummod:argon_vent" to the list of values for the
        // "minecraft:needs_stone_tool" tag.
        // The generated JSON will be in your mod's data pack, but will apply to the vanilla tag.
        this.tag(BlockTags.NEEDS_STONE_TOOL)
                .add(ModBlocks.ARGON_VENT.get());

        // Optional: Add to general ore tags (existing)
        this.tag(Tags.Blocks.ORES)
                .add(ModBlocks.CRYTHON_ORE.get())
                .add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get());

        // Optional: Add to a specific ore tag for your mod (existing)
        this.tag(BlockTags.create(new ResourceLocation(AveriumMod.MOD_ID, "ores/crython")))
                .add(ModBlocks.CRYTHON_ORE.get())
                .add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_STONE)
                .add(ModBlocks.CRYTHON_ORE.get());
        this.tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
                .add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get());
    }
}