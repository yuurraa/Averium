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
import net.minecraft.client.Minecraft; // Import Minecraft for font renderer
import net.minecraft.client.gui.Font; // Import Font
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.block.ModBlocks;
import net.yuurraa.averiummod.item.ModItems;
import net.yuurraa.averiummod.recipe.InertInfuserRecipe;

public class InertInfuserRecipeCategory implements IRecipeCategory<InertInfuserRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(AveriumMod.MOD_ID, "inert_infusing");
    public static final RecipeType<InertInfuserRecipe> RECIPE_TYPE =
            RecipeType.create(AveriumMod.MOD_ID, "inert_infusing", InertInfuserRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable progressRectDrawable;
    private final IDrawable progressTriDrawable;

    // Fuel bar parts for Xenon
    private final IDrawable fuelBarXenonTop;
    private final IDrawable fuelBarXenonMiddle;
    private final IDrawable fuelBarXenonBottom;

    // Fuel bar parts for Argon
    private final IDrawable fuelBarArgonTop;
    private final IDrawable fuelBarArgonMiddle;
    private final IDrawable fuelBarArgonBottom;

    // Constants for original texture parts
    private static final int PROGRESS_RECT_U = 176;
    private static final int PROGRESS_RECT_V = 2;
    private static final int PROGRESS_RECT_WIDTH = 30;
    private static final int PROGRESS_RECT_HEIGHT = 4;

    private static final int PROGRESS_TRI_U = 206;
    private static final int PROGRESS_TRI_V = 0;
    private static final int PROGRESS_TRI_WIDTH = 4;
    private static final int PROGRESS_TRI_HEIGHT = 7;

    // Constants for the fuel bar sprite on the texture sheet
    private static final int FUEL_BAR_SPRITE_U = 176;
    private static final int FUEL_BAR_SPRITE_XENON_V = 8;
    private static final int FUEL_BAR_SPRITE_ARGON_V = 14; // XENON_V + 6
    private static final int FUEL_BAR_SPRITE_WIDTH = 14;
    private static final int FUEL_BAR_SPRITE_HEIGHT = 4;

    private static final int BACKGROUND_WIDTH = 176; // Width of your background drawable


    public InertInfuserRecipeCategory(IGuiHelper guiHelper) {
        ResourceLocation guiTexture = new ResourceLocation(AveriumMod.MOD_ID, "textures/gui/inert_infuser_jei_gui.png");
        this.background = guiHelper.createDrawable(guiTexture, 0, 0, BACKGROUND_WIDTH, 83); // Used BACKGROUND_WIDTH
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.INERT_INFUSER.get()));

        this.progressRectDrawable = guiHelper.createDrawable(guiTexture,
                PROGRESS_RECT_U, PROGRESS_RECT_V,
                PROGRESS_RECT_WIDTH, PROGRESS_RECT_HEIGHT);
        this.progressTriDrawable = guiHelper.createDrawable(guiTexture,
                PROGRESS_TRI_U, PROGRESS_TRI_V,
                PROGRESS_TRI_WIDTH, PROGRESS_TRI_HEIGHT);

        // --- Create Fuel Bar Parts ---

        // Xenon Fuel Bar Parts
        this.fuelBarXenonTop = guiHelper.createDrawable(guiTexture,
                FUEL_BAR_SPRITE_U + 1, FUEL_BAR_SPRITE_XENON_V, // U + 1, V
                FUEL_BAR_SPRITE_WIDTH - 2, 1);                 // Width - 2, Height 1
        this.fuelBarXenonMiddle = guiHelper.createDrawable(guiTexture,
                FUEL_BAR_SPRITE_U, FUEL_BAR_SPRITE_XENON_V + 1, // U, V + 1
                FUEL_BAR_SPRITE_WIDTH, FUEL_BAR_SPRITE_HEIGHT - 2); // Width, Height - 2
        this.fuelBarXenonBottom = guiHelper.createDrawable(guiTexture,
                FUEL_BAR_SPRITE_U + 1, FUEL_BAR_SPRITE_XENON_V + FUEL_BAR_SPRITE_HEIGHT - 1, // U + 1, V + Height - 1
                FUEL_BAR_SPRITE_WIDTH - 2, 1);                 // Width - 2, Height 1

        // Argon Fuel Bar Parts
        this.fuelBarArgonTop = guiHelper.createDrawable(guiTexture,
                FUEL_BAR_SPRITE_U + 1, FUEL_BAR_SPRITE_ARGON_V,
                FUEL_BAR_SPRITE_WIDTH - 2, 1);
        this.fuelBarArgonMiddle = guiHelper.createDrawable(guiTexture,
                FUEL_BAR_SPRITE_U, FUEL_BAR_SPRITE_ARGON_V + 1,
                FUEL_BAR_SPRITE_WIDTH, FUEL_BAR_SPRITE_HEIGHT - 2);
        this.fuelBarArgonBottom = guiHelper.createDrawable(guiTexture,
                FUEL_BAR_SPRITE_U + 1, FUEL_BAR_SPRITE_ARGON_V + FUEL_BAR_SPRITE_HEIGHT - 1,
                FUEL_BAR_SPRITE_WIDTH - 2, 1);
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
        builder.addSlot(RecipeIngredientRole.INPUT, 25, 19).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 25, 45).addIngredients(recipe.getIngredients().get(1));

        ItemStack gasCellStack = ItemStack.EMPTY;
        if ("argon".equalsIgnoreCase(recipe.getRequiredGasType())) {
            gasCellStack = new ItemStack(ModItems.ARGON_GAS_CELL.get());
        } else if ("xenon".equalsIgnoreCase(recipe.getRequiredGasType())) {
            gasCellStack = new ItemStack(ModItems.XENON_GAS_CELL.get());
        }
        if (!gasCellStack.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 61, 51).addItemStack(gasCellStack);
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 32).addItemStack(recipe.getResultItem(null));
    }

    @Override
    public void draw(InertInfuserRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.progressRectDrawable.draw(guiGraphics, 63, 27);
        this.progressTriDrawable.draw(guiGraphics, 63 + PROGRESS_RECT_WIDTH, 25);

        // Original screen draw position for the 14x4 fuel bar bounding box
        int fuelBarScreenX = 86;
        int fuelBarScreenY = 57;

        if ("argon".equalsIgnoreCase(recipe.getRequiredGasType())) {
            fuelBarArgonTop.draw(guiGraphics, fuelBarScreenX + 1, fuelBarScreenY);
            fuelBarArgonMiddle.draw(guiGraphics, fuelBarScreenX, fuelBarScreenY + 1);
            fuelBarArgonBottom.draw(guiGraphics, fuelBarScreenX + 1, fuelBarScreenY + FUEL_BAR_SPRITE_HEIGHT - 1);
        } else { // Default to Xenon
            fuelBarXenonTop.draw(guiGraphics, fuelBarScreenX + 1, fuelBarScreenY);
            fuelBarXenonMiddle.draw(guiGraphics, fuelBarScreenX, fuelBarScreenY + 1);
            fuelBarXenonBottom.draw(guiGraphics, fuelBarScreenX + 1, fuelBarScreenY + FUEL_BAR_SPRITE_HEIGHT - 1);
        }

        // --- Centralize Time String ---
        Font font = Minecraft.getInstance().font;
        Component timeStringComponent = Component.translatable("gui.jei.category.smelting.time.seconds", recipe.getProcessingTime() / 20);
        int stringWidth = font.width(timeStringComponent);
        // Calculate the x position to center the string within the background width
        int timeStringX = (BACKGROUND_WIDTH - stringWidth) / 2;
        // Y position for the time string (currently 5, can be adjusted if needed)
        int timeStringY = 5;

        guiGraphics.drawString(font, timeStringComponent, timeStringX, timeStringY, 0xFF808080, false);
    }
}
