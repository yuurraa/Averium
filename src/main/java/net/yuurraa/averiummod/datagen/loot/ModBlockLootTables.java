// src/main/java/net/yuurraa/averiummod/datagen/loot/ModBlockLootTables.java
package net.yuurraa.averiummod.datagen.loot;

import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
// No need to import Enchantments if you're not using ApplyBonusCount for Fortune
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;
import net.yuurraa.averiummod.block.ModBlocks;
import net.yuurraa.averiummod.item.ModItems;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        // Argon Vent - Drops nothing
        this.add(ModBlocks.ARGON_VENT.get(), noDrop());

        // Crython Ores - Use the custom single drop method
        this.add(ModBlocks.CRYTHON_ORE.get(),
                createSingleRawOreDropNoFortune(ModBlocks.CRYTHON_ORE.get(), ModItems.RAW_CRYTHON.get()));
        this.add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get(),
                createSingleRawOreDropNoFortune(ModBlocks.DEEPSLATE_CRYTHON_ORE.get(), ModItems.RAW_CRYTHON.get()));

        // Infernium Ore - Also use the custom single drop method
        this.add(ModBlocks.INFERNIUM_ORE.get(),
                createSingleRawOreDropNoFortune(ModBlocks.INFERNIUM_ORE.get(), ModItems.RAW_INFERNIUM.get()));
    }

    /**
     * Creates a loot table for an ore that:
     * 1. Drops itself (pBlock) when mined with Silk Touch.
     * 2. Drops exactly one specified item (pItem) when mined without Silk Touch.
     * 3. The drop of pItem is NOT affected by Fortune.
     * 4. Includes explosion decay for the pItem drop.
     */
    protected LootTable.Builder createSingleRawOreDropNoFortune(Block pBlock, Item pItem) {
        return createSilkTouchDispatchTable(pBlock, // If Silk Touch, drops pBlock
                this.applyExplosionDecay(pBlock,    // Apply explosion decay to the non-silk touch drop
                        LootItem.lootTableItem(pItem) // Drop pItem
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))) // Exactly one
                        // No ApplyBonusCount for Fortune means Fortune has no effect on quantity
                )
        );
    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}