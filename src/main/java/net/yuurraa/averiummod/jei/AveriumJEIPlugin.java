// src/main/java/net/yuurraa/averiummod/jei/AveriumJEIPlugin.java
package net.yuurraa.averiummod.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes; // For ItemStack ingredients
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.block.ModBlocks;
import net.yuurraa.averiummod.item.ModItems;
import net.yuurraa.averiummod.recipe.InertInfuserRecipe;

import java.util.List; // Add this

@JeiPlugin
public class AveriumJEIPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_UID = new ResourceLocation(AveriumMod.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new InertInfuserRecipeCategory(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Frost Ebber
        registration.addIngredientInfo(
                new ItemStack(ModItems.FROST_EBBER.get()), // The item this info is about
                VanillaTypes.ITEM_STACK, // The type of the ingredient
                Component.translatable("jei.averiummod.info.frost_ebber")
        );

        // Obsidian Stake
        registration.addIngredientInfo(
                new ItemStack(ModItems.OBSIDIAN_STAKE.get()), // The item this info is about
                VanillaTypes.ITEM_STACK, // The type of the ingredient
                Component.translatable("jei.averiummod.info.obsidian_stake")
        );

        // --- How to obtain Argon Gas Cell ---
        registration.addIngredientInfo(
                new ItemStack(ModItems.ARGON_GAS_CELL.get()), // The item this info is about
                VanillaTypes.ITEM_STACK, // The type of the ingredient
                Component.translatable("jei.averiummod.info.argon_gas_cell.line1"),
                Component.translatable("jei.averiummod.info.argon_gas_cell.line2")
        );

        // --- How to obtain Xenon Gas Cell ---
        registration.addIngredientInfo(
                new ItemStack(ModItems.XENON_GAS_CELL.get()), // The item this info is about
                VanillaTypes.ITEM_STACK, // The type of the ingredient
                Component.translatable("jei.averiummod.info.xenon_gas_cell.line1"),
                Component.translatable("jei.averiummod.info.xenon_gas_cell.line2")
        );

        // Add info to Xenon Matrix
        registration.addIngredientInfo(
                new ItemStack(ModItems.XENON_MATRIX.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.xenon_matrix.info")
        );

        // Add info to Charged Xenon Matrix
        registration.addIngredientInfo(
                new ItemStack(ModItems.CHARGED_XENON_MATRIX.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.charged_xenon_matrix.info")
        );

        // Add info to Gas Cell about its use with vents
        registration.addIngredientInfo(
                new ItemStack(ModItems.GAS_CELL.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.gas_cell.info")
        );

        // Add info to Argon Vent block about its interaction
        registration.addIngredientInfo(
                new ItemStack(ModBlocks.ARGON_VENT.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.argon_vent.interaction")
        );

        // Add info to Xenon Vent block about its interaction
        registration.addIngredientInfo(
                new ItemStack(ModBlocks.XENON_VENT.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.xenon_vent.interaction")
        );

        // Add info to Inert Infuser block about its interaction
        registration.addIngredientInfo(
                new ItemStack(ModBlocks.INERT_INFUSER.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.inert_infuser")
        );

        // Gold-Reinforced Stick
        registration.addIngredientInfo(
                new ItemStack(ModItems.GOLD_REINFORCED_STICK.get()), // The item this info is about
                VanillaTypes.ITEM_STACK, // The type of the ingredient
                Component.translatable("jei.averiummod.info.gold_reinforced_stick")
        );

        // --- How to obtain Raw Crython ---
        registration.addIngredientInfo(
                new ItemStack(ModItems.RAW_CRYTHON.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.raw_crython.line1"),
                Component.translatable("jei.averiummod.info.raw_crython.line2"),
                Component.translatable("jei.averiummod.info.raw_crython.line3"),
                Component.translatable("jei.averiummod.info.crython_slow")
        );

        // Unstable and Stable Crython
        registration.addIngredientInfo(
                new ItemStack(ModItems.UNSTABLE_CRYTHON.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.crython_slow")
        );
        registration.addIngredientInfo(
                new ItemStack(ModItems.STABLE_CRYTHON.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.stable_crython"),
                Component.translatable("jei.averiummod.info.stable_crython.info")
        );

        // Add info to the Crython Ore blocks
        registration.addIngredientInfo(
                new ItemStack(ModBlocks.CRYTHON_ORE.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.crython_ore.drops"),
                Component.translatable("jei.averiummod.info.crython_ore.info")
        );
        registration.addIngredientInfo(
                new ItemStack(ModBlocks.DEEPSLATE_CRYTHON_ORE.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.deepslate_crython_ore.drops"),
                Component.translatable("jei.averiummod.info.crython_ore.info")
        );


        // --- How to obtain Raw Infernium ---
        registration.addIngredientInfo(
                new ItemStack(ModItems.RAW_INFERNIUM.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.raw_infernium.line1"),
                Component.translatable("jei.averiummod.info.raw_infernium.line2"),
                Component.translatable("jei.averiummod.info.raw_infernium.line3"),
                Component.translatable("jei.averiummod.info.infernium_burn")
        );

        // Unstable and Stable Infernium
        registration.addIngredientInfo(
                new ItemStack(ModItems.UNSTABLE_INFERNIUM.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.infernium_burn")
        );
        registration.addIngredientInfo(
                new ItemStack(ModItems.STABLE_INFERNIUM.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.stable_infernium"),
                Component.translatable("jei.averiummod.info.stable_infernium.info")
        );

        // Add info to the Infernium Ore block
        registration.addIngredientInfo(
                new ItemStack(ModBlocks.INFERNIUM_ORE.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.infernium_ore.drops"),
                Component.translatable("jei.averiummod.info.infernium_ore.info")
        );


        RecipeManager recipeManager = Minecraft.getInstance().level.getRecipeManager();
        List<InertInfuserRecipe> infuserRecipes = recipeManager.getAllRecipesFor(InertInfuserRecipe.Type.INSTANCE);
        registration.addRecipes(InertInfuserRecipeCategory.RECIPE_TYPE, infuserRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.INERT_INFUSER.get()), InertInfuserRecipeCategory.RECIPE_TYPE);
    }
}