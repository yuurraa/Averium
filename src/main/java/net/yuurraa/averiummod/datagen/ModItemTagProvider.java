// src/main/java/net/yuurraa/averiummod/datagen/ModItemTagProvider.java
package net.yuurraa.averiummod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags; // For vanilla tags if needed
import net.minecraft.world.level.block.Block; // Required for one of the constructors
import net.minecraftforge.common.data.ExistingFileHelper;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider,
                              CompletableFuture<TagLookup<Block>> blockTagLookup, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, blockTagLookup, AveriumMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Add 'inert_charm' to the 'curios:charm' item tag
        this.tag(ItemTags.create(new ResourceLocation("curios", "charm")))
                .add(ModItems.INERT_CHARM.get());
    }
}