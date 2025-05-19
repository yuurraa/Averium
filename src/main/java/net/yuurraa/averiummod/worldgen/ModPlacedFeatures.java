package net.yuurraa.averiummod.worldgen;

import net.minecraft.core.Direction; // Import this
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate; // Import this
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.yuurraa.averiummod.AveriumMod;

import java.util.List;

public class ModPlacedFeatures {
    public static final ResourceKey<PlacedFeature> ARGON_VENT_PLACED =
            registerKey("argon_vent_placed");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        Holder.Reference<ConfiguredFeature<?, ?>> oreFeatureHolder = context.lookup(Registries.CONFIGURED_FEATURE)
                .getOrThrow(ModConfiguredFeatures.ARGON_VENT);

        // Placement modifiers:
        var modifiers = List.of(
                CountPlacement.of(8), // Attempts per chunk. Adjust as needed.
                InSquarePlacement.spread(), // Spreads the placement horizontally within the chunk.
                HeightRangePlacement.uniform( // Defines the Y-level range. Deepslate is roughly Y=0 down to Y=-64.
                        VerticalAnchor.absolute(-64),
                        VerticalAnchor.absolute(0)  // You had -16, 0 allows it a bit higher in deepslate layers. Adjust if preferred.
                ),
                // This tries to ensure that the block directly above the placement position is air.
                // This increases the chance of the vent being exposed on its top side.
                // For a vein of size 5, this checks the origin of the vein.
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.ONLY_IN_AIR_PREDICATE, 1),
                BiomeFilter.biome() // Necessary filter for biome modifiers to work correctly.
        );

        register(context, ARGON_VENT_PLACED, oreFeatureHolder, modifiers);
    }

    private static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE,
                new ResourceLocation(AveriumMod.MOD_ID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context,
                                 ResourceKey<PlacedFeature> key,
                                 Holder<ConfiguredFeature<?, ?>> conf,
                                 List<PlacementModifier> modifiers) {
        context.register(key, new PlacedFeature(conf, List.copyOf(modifiers)));
    }
}