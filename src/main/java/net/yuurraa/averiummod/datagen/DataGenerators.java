// src/main/java/net/yuurraa/averiummod/datagen/DataGenerators.java
package net.yuurraa.averiummod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider; // For ModLootTableProvider wrapper
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets; // For ModLootTableProvider wrapper
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.datagen.loot.ModBlockLootTables; // Assuming you have this

import java.util.List; // For ModLootTableProvider wrapper
import java.util.Set;  // For ModLootTableProvider wrapper
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = AveriumMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // --- SERVER PROVIDERS ---
        // Block Tags Provider (existing)
        ModBlockTagProvider blockTagProvider = new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagProvider);

        // !!! THIS IS THE KEY ADDITION FOR CURIOS TAGS !!!
        // Item Tags Provider - Pass the blockTagLookup from the ModBlockTagProvider
        generator.addProvider(event.includeServer(), new ModItemTagProvider(packOutput, lookupProvider, blockTagProvider.contentsGetter(), existingFileHelper));

        // Loot Table Provider (existing example structure)
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(ModBlockLootTables::new, LootContextParamSets.BLOCK)
                // Add other sub-providers for entities, chests etc. if you have them
        )));

        // Recipe Provider (existing)
        generator.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));

        // Worldgen provider (existing)
        // Make sure ModWorldGenProvider is correctly defined if you uncomment this.
        generator.addProvider(event.includeServer(), new ModWorldGenProvider(packOutput, lookupProvider));


        // --- CLIENT PROVIDERS --- (Uncomment and implement these as needed)
        // generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));
        // generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
    }
}