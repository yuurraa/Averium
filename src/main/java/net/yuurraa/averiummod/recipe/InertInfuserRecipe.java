// src/main/java/net/yuurraa/averiummod/recipe/InertInfuserRecipe.java
package net.yuurraa.averiummod.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.yuurraa.averiummod.AveriumMod;
import org.jetbrains.annotations.Nullable;

public class InertInfuserRecipe implements Recipe<SimpleContainer> {
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ResourceLocation id;
    private final int processingTime; // Time in ticks for this recipe
    private final String requiredGasType; // e.g., "argon" or "xenon"

    public InertInfuserRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> inputItems, int processingTime, String requiredGasType) {
        this.id = id;
        this.output = output;
        this.inputItems = inputItems; // Should contain metal and catalyst
        this.processingTime = processingTime;
        this.requiredGasType = requiredGasType.toLowerCase();
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }
        // pContainer will have items from Metal Slot and Catalyst Slot
        // Slot 0: Metal, Slot 1: Catalyst (based on how we'll check in BE)
        if (inputItems.size() < 2) return false; // Ensure we have enough ingredients defined

        return inputItems.get(0).test(pContainer.getItem(0)) && // Metal slot
                inputItems.get(1).test(pContainer.getItem(1));   // Catalyst slot
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess registryAccess) {
        return output.copy(); // Return a copy to prevent modification of the recipe's output stack
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true; // Not a grid-based recipe
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    public NonNullList<Ingredient> getIngredients() {
        return inputItems;
    }

    public int getProcessingTime() {
        return processingTime;
    }

    public String getRequiredGasType() {
        return requiredGasType;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    // Define the RecipeType
    public static class Type implements RecipeType<InertInfuserRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "inert_infusing"; // Matches the "type" in your recipe JSON
    }

    // Define the RecipeSerializer
    public static class Serializer implements RecipeSerializer<InertInfuserRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(AveriumMod.MOD_ID, "inert_infusing");

        @Override
        public InertInfuserRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            int processingTime = GsonHelper.getAsInt(pSerializedRecipe, "processing_time", 200); // Default 200 ticks (10s)
            String requiredGas = GsonHelper.getAsString(pSerializedRecipe, "gas_type", "argon"); // Default to argon

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY); // Expecting 2 inputs (metal, catalyst)

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new InertInfuserRecipe(pRecipeId, output, inputs, processingTime, requiredGas);
        }

        @Override
        public @Nullable InertInfuserRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(pBuffer.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }
            ItemStack output = pBuffer.readItem();
            int processingTime = pBuffer.readInt();
            String gasType = pBuffer.readUtf();
            return new InertInfuserRecipe(pRecipeId, output, inputs, processingTime, gasType);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, InertInfuserRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.getIngredients().size());
            for (Ingredient ing : pRecipe.getIngredients()) {
                ing.toNetwork(pBuffer);
            }
            pBuffer.writeItemStack(pRecipe.getResultItem(RegistryAccess.EMPTY), false); // Use RegistryAccess.EMPTY for client-side simplicity
            pBuffer.writeInt(pRecipe.getProcessingTime());
            pBuffer.writeUtf(pRecipe.getRequiredGasType());
        }
    }
}
