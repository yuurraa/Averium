// src/main/java/net/yuurraa/averiummod/jei/InertInfuserRecipeCategory.java
package net.yuurraa.averiummod.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.block.ModBlocks;
import net.yuurraa.averiummod.item.ModItems;
import net.yuurraa.averiummod.recipe.InertInfuserRecipe;
import net.yuurraa.averiummod.screen.InertInfuserMenu; // For progress bar width

public class InertInfuserRecipeCategory implements IRecipeCategory<InertInfuserRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(AveriumMod.MOD_ID, "inert_infusing");
    public static final RecipeType<InertInfuserRecipe> RECIPE_TYPE =
            RecipeType.create(AveriumMod.MOD_ID, "inert_infusing", InertInfuserRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable progressArrow;
    private final IDrawable fuelBarFilledXenon; // Example for Xenon
    private final IDrawable fuelBarFilledArgon;  // Example for Argon

    public InertInfuserRecipeCategory(IGuiHelper guiHelper) {
        // Dimensions of the relevant part of your GUI texture for recipes
        // Adjust u, v, width, height as needed to capture the machine's recipe area
        // e.g., width up to output slot, height to cover inputs and bars.
        // Slots: Metal (25,19), Catalyst (25,45), Gas (61,51), Output (112,32)
        // Progress bar: (63,27), Fuel bar: (86,57)
        // Max X for slots is around 134+16 = 150. Max Y is around 51+16=67. Let's pick a background size.
        // Width: 150, Height: 70. (0,0) UV for this section from main GUI.
        ResourceLocation guiTexture = new ResourceLocation(AveriumMod.MOD_ID, "textures/gui/inert_infuser_gui.png");
        this.background = guiHelper.createDrawable(guiTexture, 0, 0, 176, 83); // Changed from 150, 70

        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.INERT_INFUSER.get()));

        // Progress arrow (filled part) - Ensure these UVs and dimensions are correct for your texture
        this.progressArrow = guiHelper.createDrawable(guiTexture,
                176, // U for filled rect part of arrow
                2,   // V for filled rect part of arrow
                InertInfuserMenu.PROGRESS_ARROW_TOTAL_SCALABLE_WIDTH, // total width
                7); // Max height of progress arrow (triangle part is 7px high)

        // Fuel bar textures - Ensure these UVs and dimensions are correct
        this.fuelBarFilledXenon = guiHelper.createDrawable(guiTexture,
                176, // FUEL_BAR_U
                8,   // FUEL_BAR_XENON_V
                14,  // FUEL_BAR_WIDTH_ON_SHEET
                4);  // FUEL_BAR_HEIGHT_ON_SHEET
        this.fuelBarFilledArgon = guiHelper.createDrawable(guiTexture,
                176, // FUEL_BAR_U
                14,  // FUEL_BAR_ARGON_V (XENON_V + 6)
                14,
                4);
    }

    @Override
    public RecipeType<InertInfuserRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.averiummod.inert_infuser");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, InertInfuserRecipe recipe, IFocusGroup focuses) {
        // Slot positions from your InertInfuserMenu/Screen, adjusted for JEI background
        // These are relative to the background drawable's top-left (0,0)
        // Metal Input (Original GUI: 25, 19)
        builder.addSlot(RecipeIngredientRole.INPUT, 25, 19).addIngredients(recipe.getIngredients().get(0));
        // Catalyst Input (Original GUI: 25, 45)
        builder.addSlot(RecipeIngredientRole.INPUT, 25, 45).addIngredients(recipe.getIngredients().get(1));

        // Gas Input - Represent with the corresponding gas cell item (Original GUI: 61, 51)
        ItemStack gasCellStack = ItemStack.EMPTY;
        if ("argon".equalsIgnoreCase(recipe.getRequiredGasType())) {
            gasCellStack = new ItemStack(ModItems.ARGON_GAS_CELL.get());
        } else if ("xenon".equalsIgnoreCase(recipe.getRequiredGasType())) {
            gasCellStack = new ItemStack(ModItems.XENON_GAS_CELL.get());
        }
        if (!gasCellStack.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 61, 51).addItemStack(gasCellStack);
        }

        // Output (Original GUI: 112, 32)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 32).addItemStack(recipe.getResultItem(null)); // RegistryAccess can be null for getResultItem
    }

    @Override
    public void draw(InertInfuserRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        // Animate progress arrow and fuel bar
        // Progress Bar (Original GUI X: 63, Y: 27)
        progressArrow.draw(guiGraphics, 63, 27); // Static full arrow for JEI, or animate it

        // Fuel Bar (Original GUI X: 86, Y: 57)
        IDrawable currentFuelBar;
        if ("argon".equalsIgnoreCase(recipe.getRequiredGasType())) {
            currentFuelBar = fuelBarFilledArgon;
        } else { // Default to Xenon if not argon or if gas type is different
            currentFuelBar = fuelBarFilledXenon;
        }
        currentFuelBar.draw(guiGraphics, 86, 57); // Static full fuel bar

        // You can draw processing time or other info here too
        Component timeString = Component.translatable("gui.jei.category.smelting.time.seconds", recipe.getProcessingTime() / 20);
        guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, timeString, 60, 5, 0xFF808080, false); // Adjust position
    }
}