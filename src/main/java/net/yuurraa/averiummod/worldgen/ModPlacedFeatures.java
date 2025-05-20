// src/main/java/net/yuurraa/averiummod/worldgen/ModPlacedFeatures.java
package net.yuurraa.averiummod.worldgen;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.yuurraa.averiummod.AveriumMod;

import java.util.List;

public class ModPlacedFeatures {
    // ARGON_VENT
    public static final ResourceKey<PlacedFeature> ARGON_VENT_PLACED =
            registerKey("argon_vent_placed");
    // XENON_VENT
    public static final ResourceKey<PlacedFeature> XENON_VENT_PLACED =
            registerKey("xenon_vent_placed");

    // CRYTHON_ORE
    public static final ResourceKey<PlacedFeature> CRYTHON_ORE_PLACED =
            registerKey("crython_ore_placed");
    // INFERNIUM_ORE
    public static final ResourceKey<PlacedFeature> INFERNIUM_ORE_PLACED =
            registerKey("infernium_ore_placed");

    public static void bootstrap(BootstapContext<PlacedFeature> context) {
        Holder.Reference<ConfiguredFeature<?, ?>> argonVentConfigured = context.lookup(Registries.CONFIGURED_FEATURE)
                .getOrThrow(ModConfiguredFeatures.ARGON_VENT);
        Holder.Reference<ConfiguredFeature<?, ?>> xenonVentConfigured = context.lookup(Registries.CONFIGURED_FEATURE)
                .getOrThrow(ModConfiguredFeatures.XENON_VENT);
        Holder.Reference<ConfiguredFeature<?, ?>> crythonOreConfigured = context.lookup(Registries.CONFIGURED_FEATURE)
                .getOrThrow(ModConfiguredFeatures.CRYTHON_ORE);
        Holder.Reference<ConfiguredFeature<?, ?>> inferniumOreConfigured = context.lookup(Registries.CONFIGURED_FEATURE)
                .getOrThrow(ModConfiguredFeatures.INFERNIUM_ORE);

        // ARGON_VENT PLACEMENT
        var argonVentModifiers = List.of(
                CountPlacement.of(7),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.absolute(-64),
                        VerticalAnchor.absolute(0)
                ),
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.ONLY_IN_AIR_PREDICATE, 1),
                BiomeFilter.biome()
        );
        register(context, ARGON_VENT_PLACED, argonVentConfigured, argonVentModifiers);

        // XENON_VENT PLACEMENT
        // Sparser (lower count) and slightly deeper than Argon Vents
        var xenonVentModifiers = List.of(
                CountPlacement.of(6), // Sparser than Argon's 7
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.absolute(-64), // Deepest
                        VerticalAnchor.absolute(0)  // Max height, lower than Argon's 0
                ),
                // Same EnvironmentScan as Argon Vent if it should also face open air
                EnvironmentScanPlacement.scanningFor(Direction.UP, BlockPredicate.ONLY_IN_AIR_PREDICATE, 1),
                BiomeFilter.biome()
        );
        register(context, XENON_VENT_PLACED, xenonVentConfigured, xenonVentModifiers);


        // CRYTHON_ORE PLACEMENT
        var crythonOreModifiers = List.of(
                CountPlacement.of(6), // Set higher due to overlaps in biomes
                InSquarePlacement.spread(),
                // Diamond Y-level distribution: triangle shape, peak at bottom of world.
                HeightRangePlacement.triangle(
                        VerticalAnchor.absolute(-64), // Bottom anchor
                        VerticalAnchor.absolute(16)   // Top anchor, similar to diamonds
                ),
                BiomeFilter.biome() // Essential for biome-specific placement via BiomeModifiers
        );
        register(context, CRYTHON_ORE_PLACED, crythonOreConfigured, crythonOreModifiers);


        // INFERNIUM_ORE PLACEMENT
        var inferniumOreModifiers = List.of(
                CountPlacement.of(4),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.absolute(8),
                        VerticalAnchor.absolute(119)
                ),
                BiomeFilter.biome()
        );
        register(context, INFERNIUM_ORE_PLACED, inferniumOreConfigured, inferniumOreModifiers);
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