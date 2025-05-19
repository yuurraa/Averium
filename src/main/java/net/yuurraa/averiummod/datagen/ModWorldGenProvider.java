// src/main/java/net/yuurraa/averiummod/datagen/ModWorldGenProvider.java

package net.yuurraa.averiummod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.worldgen.ModConfiguredFeatures;
import net.yuurraa.averiummod.worldgen.ModPlacedFeatures;
import net.yuurraa.averiummod.worldgen.ModBiomeModifiers; // Import this

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider {
    // Define the registries that our mod is adding entries to
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap); // Add this line for biome modifiers

    public ModWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(AveriumMod.MOD_ID));
    }
}