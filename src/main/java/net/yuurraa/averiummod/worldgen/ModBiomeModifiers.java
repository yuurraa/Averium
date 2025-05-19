// src/main/java/net/yuurraa/averiummod/worldgen/biome/ModBiomeModifiers.java
package net.yuurraa.averiummod.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.Tags; // Import Forge Tags
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.yuurraa.averiummod.AveriumMod;

public class ModBiomeModifiers {
    // ARGON_VENT (existing)
    public static final ResourceKey<BiomeModifier> ADD_ARGON_VENT = registerKey("add_argon_vent");

    // CRYTHON_ORE
    public static final ResourceKey<BiomeModifier> ADD_CRYTHON_ORE = registerKey("add_crython_ore");


    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        // ARGON_VENT (existing)
        context.register(ADD_ARGON_VENT, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ARGON_VENT_PLACED)), // Use updated key
                GenerationStep.Decoration.UNDERGROUND_ORES));

        // CRYTHON_ORE
        // Target cold biomes. Using Forge's IS_SNOWY tag is a good general choice.
        // You could also use Tags.Biomes.IS_COLD_OVERWORLD or be more specific by listing individual biome tags.
        context.register(ADD_CRYTHON_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(Tags.Biomes.IS_SNOWY), // Using Forge's IS_SNOWY tag
                // Alternatively, for a broader cold definition: biomes.getOrThrow(Tags.Biomes.IS_COLD_OVERWORLD),
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.CRYTHON_ORE_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(AveriumMod.MOD_ID, name));
    }
}