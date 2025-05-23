// src/main/java/net/yuurraa/averiummod/datagen/ModRecipeProvider.java
package net.yuurraa.averiummod.datagen;

// Correct Gson imports
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.advancements.Advancement;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.item.ModItems;
import net.yuurraa.averiummod.recipe.InertInfuserRecipe; // Import your recipe
import net.yuurraa.averiummod.recipe.ModRecipeTypes;   // Import your recipe types
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        // --- Existing Recipes ---
        oreBlasting(consumer, List.of(ModItems.RAW_CRYTHON.get()), RecipeCategory.MISC, ModItems.UNSTABLE_CRYTHON.get(), 0.7f, 100, "crython_from_blasting_raw_crython");
        oreSmelting(consumer, List.of(ModItems.RAW_CRYTHON.get()), RecipeCategory.MISC, ModItems.UNSTABLE_CRYTHON.get(), 0.7f, 200, "crython_from_smelting_raw_crython");

        oreBlasting(consumer, List.of(ModItems.RAW_INFERNIUM.get()), RecipeCategory.MISC, ModItems.UNSTABLE_INFERNIUM.get(), 0.7f, 100, "infernium_from_blasting_raw_infernium");
        oreSmelting(consumer, List.of(ModItems.RAW_INFERNIUM.get()), RecipeCategory.MISC, ModItems.UNSTABLE_INFERNIUM.get(), 0.7f, 200, "infernium_from_smelting_raw_infernium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GOLD_REINFORCED_STICK.get(), 1)
                .pattern(" GS")
                .pattern("GLG")
                .pattern("SG ")
                .define('G', Items.GOLD_INGOT)
                .define('S', Items.STICK)
                .define('L', Items.LEATHER)
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                // .unlockedBy("has_stick", has(Items.STICK)) // Not necessary to unlock by every ingredient usually
                // .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.FROST_EBBER.get(), 1)
                .pattern("MAM")
                .pattern("ICI")
                .define('A', ModItems.CHARGED_XENON_MATRIX.get())
                .define('C', ModItems.STABLE_CRYTHON.get())
                .define('I', Items.BLUE_ICE)
                .define('M', Items.AMETHYST_SHARD)
                .unlockedBy("has_charged_xenon_matrix", has(ModItems.CHARGED_XENON_MATRIX.get()))
                // .unlockedBy("has_stable_crython", has(ModItems.STABLE_CRYTHON.get()))
                // .unlockedBy("has_blue_ice", has(Items.BLUE_ICE))
                // .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "frost_ebber_crafting"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.OBSIDIAN_STAKE.get(), 1)
                .pattern(" N ")
                .pattern("OAI")
                .pattern(" M ")
                .define('N', Items.NETHERITE_SCRAP)
                .define('A', ModItems.CHARGED_XENON_MATRIX.get())
                .define('O', Items.CRYING_OBSIDIAN)
                .define('I', ModItems.STABLE_INFERNIUM.get())
                .define('M', Items.AMETHYST_SHARD)
                .unlockedBy("has_charged_xenon_matrix", has(ModItems.CHARGED_XENON_MATRIX.get()))
                // .unlockedBy("has_netherite_scrap", has(Items.NETHERITE_SCRAP))
                // .unlockedBy("has_crying_obsidian", has(Items.CRYING_OBSIDIAN))
                // .unlockedBy("has_stable_infernium", has(ModItems.STABLE_INFERNIUM.get()))
                // .unlockedBy("has_amethyst_shard", has(Items.AMETHYST_SHARD))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "obsidian_stake_crafting"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.GAS_CELL.get(), 4)
                .pattern("IHI")
                .pattern("IGI")
                .pattern("IRI")
                .define('I', Items.IRON_INGOT)
                .define('H', Items.HOPPER)
                .define('G', Items.GLASS_PANE)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_hopper", has(Items.HOPPER))
                // .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                // .unlockedBy("has_glass_pane", has(Items.GLASS_PANE))
                // .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "gas_cell_crafting"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.XENON_MATRIX.get())
                .pattern(" Q ")
                .pattern("QEQ")
                .pattern(" Q ")
                .define('Q', Items.QUARTZ)
                .define('E', Items.ENDER_PEARL)
                .unlockedBy("has_ender_pearl", has(Items.ENDER_PEARL))
                // .unlockedBy("has_quartz", has(Items.QUARTZ))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "xenon_matrix_crafting"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CHARGED_XENON_MATRIX.get(), 1)
                .requires(ModItems.XENON_MATRIX.get())
                .requires(ModItems.XENON_GAS_CELL.get())
                .unlockedBy("has_xenon_matrix", has(ModItems.XENON_MATRIX.get()))
                // .unlockedBy("has_xenon_gas_cell", has(ModItems.XENON_GAS_CELL.get()))
                .save(consumer, new ResourceLocation(AveriumMod.MOD_ID, "charged_xenon_matrix_crafting"));


        // --- Inert Infuser Recipes ---
        infuserRecipe(consumer,
                Ingredient.of(ModItems.UNSTABLE_CRYTHON.get()),
                Ingredient.of(Items.REDSTONE),
                new ItemStack(ModItems.STABLE_CRYTHON.get()),
                200, // processingTime
                "argon", // gasType
                0.7f, // experience
                "stable_crython_from_unstable_via_infuser"
        );

        infuserRecipe(consumer,
                Ingredient.of(ModItems.UNSTABLE_INFERNIUM.get()),
                Ingredient.of(Items.GLOWSTONE_DUST),
                new ItemStack(ModItems.STABLE_INFERNIUM.get()),
                250, // processingTime
                "xenon", // gasType
                0.7f, // experience
                "stable_infernium_from_unstable_via_infuser"
        );
    }

    protected static void infuserRecipe(Consumer<FinishedRecipe> consumer, Ingredient metalInput, Ingredient catalystInput, ItemStack output, int processingTime, String gasType, float experience, String recipeName) {
        consumer.accept(new InertInfuserFinishedRecipe(
                new ResourceLocation(AveriumMod.MOD_ID, "inert_infusing/" + recipeName),
                output,
                metalInput,
                catalystInput,
                processingTime,
                gasType,
                experience // Pass experience
        ));
    }

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
                    .save(pFinishedRecipeConsumer, new ResourceLocation(AveriumMod.MOD_ID, getItemName(pResult) + pRecipeNameSuffix + "_" + getItemName(itemlike)));
        }
    }

    public static class InertInfuserFinishedRecipe implements FinishedRecipe {
        private final ResourceLocation id;
        private final ItemStack output;
        private final Ingredient metalInput;
        private final Ingredient catalystInput;
        private final int processingTime;
        private final String gasType;
        private final float experience; // New field
        private final Advancement.Builder advancement = Advancement.Builder.advancement();

        public InertInfuserFinishedRecipe(ResourceLocation id, ItemStack output, Ingredient metalInput, Ingredient catalystInput, int processingTime, String gasType, float experience) {
            this.id = id;
            this.output = output;
            this.metalInput = metalInput;
            this.catalystInput = catalystInput;
            this.processingTime = processingTime;
            this.gasType = gasType;
            this.experience = experience; // Store experience
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            JsonArray ingredientsArray = new JsonArray();
            ingredientsArray.add(metalInput.toJson());
            ingredientsArray.add(catalystInput.toJson());
            json.add("ingredients", ingredientsArray);

            JsonObject outputObject = new JsonObject();
            outputObject.addProperty("item", ForgeRegistries.ITEMS.getKey(this.output.getItem()).toString());
            if (this.output.getCount() > 1) {
                outputObject.addProperty("count", this.output.getCount());
            }
            json.add("output", outputObject);

            json.addProperty("processing_time", this.processingTime);
            json.addProperty("gas_type", this.gasType);
            json.addProperty("experience", this.experience); // Serialize experience
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ModRecipeTypes.INERT_INFUSING_SERIALIZER.get(); //
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
