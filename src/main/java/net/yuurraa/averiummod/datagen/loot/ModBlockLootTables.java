// src/main/java/net/yuurraa/averiummod/datagen/loot/ModBlockLootTables.java
package net.yuurraa.averiummod.datagen.loot;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
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
        // Argon Vent - Drops nothing (as per your previous setup)
        this.add(ModBlocks.ARGON_VENT.get(), noDrop());

        // Crython Ores - Drop Raw Crython, require Netherite Pickaxe
        // Condition for Netherite tier tool (Tier 4)
        // Vanilla Tiers: Wood(0), Stone(1), Iron(2), Diamond(3), Netherite(4), Gold(0)
        // We need to check if the tool used has a high enough mining level.
        // The `forge:needs_netherite_tool` tag on the block itself enforces that it *can* be broken and *won't* drop with lesser tools.
        // The loot table here defines *what* it drops when broken correctly.
        // Vanilla ores use `createOreDrop`.
        this.add(ModBlocks.CRYTHON_ORE.get(),
                createCrythonOreDrops(ModBlocks.CRYTHON_ORE.get(), ModItems.RAW_CRYTHON.get()));
        this.add(ModBlocks.DEEPSLATE_CRYTHON_ORE.get(),
                createCrythonOreDrops(ModBlocks.DEEPSLATE_CRYTHON_ORE.get(), ModItems.RAW_CRYTHON.get()));
    }

    // Custom drop method for Crython Ore to include the Netherite tool check if needed,
    // although `requiresCorrectToolForDrops()` and the `forge:needs_netherite_tool` tag
    // should handle the "no drop unless correct tool" part.
    // The standard `createOreDrop` already respects `requiresCorrectToolForDrops`.
    // So, we just need to ensure the block is tagged correctly.
    // The loot table here will just specify what drops WHEN broken correctly.
    protected LootTable.Builder createCrythonOreDrops(Block pBlock, Item pItem) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(pItem)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1.0F))) // Always drops 1 raw item
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
                )
        );
    }


    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}