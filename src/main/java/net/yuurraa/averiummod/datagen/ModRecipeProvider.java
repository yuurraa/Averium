// src/main/java/net/yuurraa/averiummod/datagen/ModRecipeProvider.java
package net.yuurraa.averiummod.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.item.ModItems; // Make sure this is your ModItems class

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // Crython from Smelting/Blasting Raw Crython
        oreBlasting(consumer, List.of(ModItems.RAW_CRYTHON.get()), RecipeCategory.MISC, ModItems.UNSTABLE_CRYTHON.get(), 0.7f, 100, "crython_from_blasting_raw_crython");
        oreSmelting(consumer, List.of(ModItems.RAW_CRYTHON.get()), RecipeCategory.MISC, ModItems.UNSTABLE_CRYTHON.get(), 0.7f, 200, "crython_from_smelting_raw_crython");

        // Infernium from Smelting/Blasting Raw Infernium
        oreBlasting(consumer, List.of(ModItems.RAW_INFERNIUM.get()), RecipeCategory.MISC, ModItems.UNSTABLE_INFERNIUM.get(), 0.7f, 100, "infernium_from_blasting_raw_infernium");
        oreSmelting(consumer, List.of(ModItems.RAW_INFERNIUM.get()), RecipeCategory.MISC, ModItems.UNSTABLE_INFERNIUM.get(), 0.7f, 200, "infernium_from_smelting_raw_infernium");

        // Gold Reinforced Stick Crafting
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLD_REINFORCED_STICK.get(), 1)
                .pattern(" GS")
                .pattern("GLG")
                .pattern("SG ")
                .define('G', Items.GOLD_INGOT)
                .define('S', Items.STICK)
                .define('L', Items.LEATHER)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);

        // Frost Ebber Crafting
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FROST_EBBER.get(), 1)
                .pattern("MAM")
                .pattern("IPI")
                .define('A', ModItems.CHARGED_XENON_MATRIX.get())
                .define('P', Items.PRISMARINE_SHARD)
                .define('I', Items.BLUE_ICE)
                .define('M', Items.AMETHYST_SHARD)
                .unlockedBy("has_charged_xenon_matrix_cell", has(ModItems.CHARGED_XENON_MATRIX.get()))
                .unlockedBy("has_prismarine_shard", has(Items.PRISMARINE_SHARD))
                .unlockedBy("has_blue_ice", has(Items.BLUE_ICE))
                .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "frost_ebber_crafting"));

        // Obsidian Stake Crafting
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.OBSIDIAN_STAKE.get(), 1)
                .pattern(" N ")
                .pattern("OAO")
                .pattern(" M ")
                .define('N', Items.NETHERITE_SCRAP)
                .define('A', ModItems.CHARGED_XENON_MATRIX.get())
                .define('O', Items.CRYING_OBSIDIAN)
                .define('M', Items.AMETHYST_SHARD)
                .unlockedBy("has_charged_xenon_matrix_cell", has(ModItems.CHARGED_XENON_MATRIX.get()))
                .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                .unlockedBy("has_crying_obsidian", has(Items.CRYING_OBSIDIAN))
                .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "obsidian_stake_crafting"));

        // --- Gas Cell Recipe ---
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.GAS_CELL.get())
                .pattern("IHI")
                .pattern("IGI")
                .pattern("IRI")
                .define('I', Items.IRON_INGOT)
                .define('H', Items.HOPPER)
                .define('G', Items.GLASS_PANE)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .unlockedBy("has_glass_pane", has(Items.GLASS_PANE))
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "gas_cell_crafting"));

        // --- Empty Xenon Matrix Recipe ---
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.XENON_MATRIX.get())
                .pattern(" Q ")
                .pattern("QEQ")
                .pattern(" Q ")
                .define('Q', Items.QUARTZ)
                .define('E', Items.ENDER_PEARL)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "xenon_matrix_crafting"));

        // --- Charged Xenon Matrix Recipe (using Xenon Gas Cell) ---
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CHARGED_XENON_MATRIX.get(), 1)
                .requires(ModItems.XENON_MATRIX.get())
                .requires(ModItems.XENON_GAS_CELL.get())
                .unlockedBy("has_xenon_matrix", has(ModItems.XENON_MATRIX.get()))
                .unlockedBy("has_xenon_gas_cell", has(ModItems.XENON_GAS_CELL.get()))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "charged_xenon_matrix_crafting"));
    }

    // Helper methods for ore smelting/blasting (from Vanilla's RecipeProvider, adapted)
    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_smelting");
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer, RecipeSerializer<? extends AbstractCookingRecipe> pSerializer, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeNameSuffix) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pSerializer)
                    .group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pFinishedRecipeConsumer, AveriumMod.MOD_ID + ":" + getItemName(pResult) + pRecipeNameSuffix + "_" + getItemName(itemlike));
        }
    }
}