// src/main/java/net/yuurraa/averiummod/worldgen/ModConfiguredFeatures.java
package net.yuurraa.averiummod.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.block.ModBlocks;

import java.util.List;
import java.util.logging.Logger;

public class ModConfiguredFeatures {
    // ARGON_VENT
    public static final ResourceKey<ConfiguredFeature<?, ?>> ARGON_VENT =
            registerKey("argon_vent_ore");
    // XENON_VENT
    public static final ResourceKey<ConfiguredFeature<?, ?>> XENON_VENT =
            registerKey("xenon_vent_ore");

    // CRYTHON_ORE
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRYTHON_ORE =
            registerKey("crython_ore");
    // INFERNIUM_ORE
    public static final ResourceKey<ConfiguredFeature<?, ?>> INFERNIUM_ORE =
            registerKey("infernium_ore");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        // ARGON_VENT
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);
        OreConfiguration argonVentConfig = new OreConfiguration(
                List.of(OreConfiguration.target(deepslateReplaceables, ModBlocks.ARGON_VENT.get().defaultBlockState())),
                6);
        register(context, ARGON_VENT, Feature.ORE, argonVentConfig);
        // XENON_VENT
        OreConfiguration xenonVentConfig = new OreConfiguration(
                List.of(OreConfiguration.target(deepslateReplaceables, ModBlocks.XENON_VENT.get().defaultBlockState())),
                4);
        register(context, XENON_VENT, Feature.ORE, xenonVentConfig);


        // CRYTHON_ORE
        RuleTest stoneReplaceables = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        List<OreConfiguration.TargetBlockState> crythonOres = List.of(
                OreConfiguration.target(stoneReplaceables, ModBlocks.CRYTHON_ORE.get().defaultBlockState()),
                OreConfiguration.target(deepslateReplaceables, ModBlocks.DEEPSLATE_CRYTHON_ORE.get().defaultBlockState())
        );
        OreConfiguration crythonOreConfig = new OreConfiguration(crythonOres, 4);
        register(context, CRYTHON_ORE, Feature.ORE, crythonOreConfig);


        // INFERNIUM_ORE
        RuleTest netherReplaceables = new TagMatchTest(BlockTags.BASE_STONE_NETHER);
        List<OreConfiguration.TargetBlockState> inferniumOres = List.of(
                OreConfiguration.target(netherReplaceables, ModBlocks.INFERNIUM_ORE.get().defaultBlockState())
        );
        OreConfiguration inferniumOreConfig = new OreConfiguration(inferniumOres, 3, 0.0f);
        register(context, INFERNIUM_ORE, Feature.ORE, inferniumOreConfig);
    }

    private static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
                new ResourceLocation(AveriumMod.MOD_ID, name));
    }

    private static <FC extends net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration, F extends Feature<FC>>
    void register(BootstapContext<ConfiguredFeature<?, ?>> context,
                  ResourceKey<ConfiguredFeature<?, ?>> key,
                  F feature,
                  FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}