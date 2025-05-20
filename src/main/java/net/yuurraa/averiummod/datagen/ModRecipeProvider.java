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
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT)) // Criterion to unlock the recipe
                .unlockedBy("has_stick", has(Items.STICK))
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);

        // Frost Ebber Crafting
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FROST_EBBER.get(), 1)
                .pattern(" S ")
                .pattern("GAG")
                .pattern("OMO")
                .define('G', Items.GOLD_INGOT)
                .define('A', ModItems.BOTTLED_ARGON.get())
                .define('S', Items.STRING)
                .define('O', Items.OBSIDIAN)
                .define('M', Items.AMETHYST_SHARD)
                .unlockedBy("has_bottled_argon", has(ModItems.BOTTLED_ARGON.get())) // Unlock condition
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .unlockedBy("has_string", has(Items.STRING))
                .unlockedBy("has_obsidian", has(Items.OBSIDIAN))
                .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "frost_ebber_crafting"));

        // Obsidian Stake Crafting
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.OBSIDIAN_STAKE.get(), 1)
                .pattern(" S ")
                .pattern("IAI")
                .pattern("OMO")
                .define('I', Items.IRON_INGOT)
                .define('A', ModItems.BOTTLED_ARGON.get())
                .define('S', Items.STRING)
                .define('O', Items.OBSIDIAN)
                .define('M', Items.AMETHYST_SHARD)
                .unlockedBy("has_bottled_argon", has(ModItems.BOTTLED_ARGON.get())) // Unlock condition
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .unlockedBy("has_string", has(Items.STRING))
                .unlockedBy("has_obsidian", has(Items.OBSIDIAN))
                .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "obsidian_stake_crafting"));
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