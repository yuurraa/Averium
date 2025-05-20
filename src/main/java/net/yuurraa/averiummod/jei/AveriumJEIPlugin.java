// src/main/java/net/yuurraa/averiummod/jei/AveriumJEIPlugin.java
package net.yuurraa.averiummod.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes; // For ItemStack ingredients
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.block.ModBlocks;
import net.yuurraa.averiummod.item.ModItems;

@JeiPlugin // Marks this class as a JEI plugin
public class AveriumJEIPlugin implements IModPlugin {

    // Define a unique ID for your plugin
    private static final ResourceLocation PLUGIN_UID = new ResourceLocation(AveriumMod.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    /**
     * This method is used to register new recipes, add ingredient information, etc.
     */
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Inert Charm
        registration.addIngredientInfo(
                new ItemStack(ModItems.INERT_CHARM.get()), // The item this info is about
                VanillaTypes.ITEM_STACK, // The type of the ingredient
                Component.translatable("jei.averiummod.info.inert_charm")
        );

        // --- How to obtain Bottled Argon ---
        registration.addIngredientInfo(
                new ItemStack(ModItems.BOTTLED_ARGON.get()), // The item this info is about
                VanillaTypes.ITEM_STACK, // The type of the ingredient
                Component.translatable("jei.averiummod.info.bottled_argon.line1"),
                Component.translatable("jei.averiummod.info.bottled_argon.line2")
        );

        // Optional: Add info to Glass Bottle about its use with Argon Vents
        registration.addIngredientInfo(
                new ItemStack(Items.GLASS_BOTTLE),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.glass_bottle.argon_vent")
        );

        // Optional: Add info to Argon Vent block about its interaction
        registration.addIngredientInfo(
                new ItemStack(ModBlocks.ARGON_VENT.get()),
                VanillaTypes.ITEM_STACK,
                Component.translatable("jei.averiummod.info.argon_vent.interaction")
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
                Component.translatable("jei.averiummod.info.stable_crython")
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
    }
}