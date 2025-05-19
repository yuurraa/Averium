package net.yuurraa.averiummod.worldgen;

import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.worldgen.ModPlacedFeatures;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_ARGON_VENT = registerKey("add_argon_vent");

    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        // This modifier adds the ARGON_VENT_PLACED feature to all biomes tagged as IS_OVERWORLD.
        // The feature will be added during the UNDERGROUND_ORES decoration step.
        context.register(ADD_ARGON_VENT, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
                biomes.getOrThrow(BiomeTags.IS_OVERWORLD), // Target Overworld biomes
                HolderSet.direct(placedFeatures.getOrThrow(ModPlacedFeatures.ARGON_VENT_PLACED)),
                GenerationStep.Decoration.UNDERGROUND_ORES));
    }

    private static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(AveriumMod.MOD_ID, name));
    }
}