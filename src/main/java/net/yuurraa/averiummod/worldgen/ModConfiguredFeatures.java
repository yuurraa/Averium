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

public class ModConfiguredFeatures {
    public static final ResourceKey<ConfiguredFeature<?, ?>> ARGON_VENT =
            registerKey("argon_vent_ore");

    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        // 1. Define where we can replace blocks (deepslate ore replaceables)
        RuleTest deepslateReplaceables = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        // 2. Create an OreConfiguration: target=deepslate, state=our block, size=5
        OreConfiguration config = new OreConfiguration(
                List.of(OreConfiguration.target(deepslateReplaceables, ModBlocks.ARGON_VENT.get().defaultBlockState())),
                6  // vein size
        );

        // 3. Register the configured feature under our key
        register(context, ARGON_VENT, Feature.ORE, config);
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
