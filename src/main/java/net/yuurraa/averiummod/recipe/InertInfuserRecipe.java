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
    private final int processingTime;
    private final String requiredGasType;
    private final float experience; // New field for XP

    public InertInfuserRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> inputItems, int processingTime, String requiredGasType, float experience) {
        this.id = id;
        this.output = output;
        this.inputItems = inputItems;
        this.processingTime = processingTime;
        this.requiredGasType = requiredGasType.toLowerCase();
        this.experience = experience; // Store XP
    }

    // ... (matches, assemble, canCraftInDimensions, getResultItem, getIngredients, getProcessingTime, getRequiredGasType, getId, getType methods remain the same) ...

    public float getExperience() { // Getter for XP
        return this.experience;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }
        if (inputItems.size() < 2) return false;

        return inputItems.get(0).test(pContainer.getItem(0)) &&
                inputItems.get(1).test(pContainer.getItem(1));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
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

    public static class Type implements RecipeType<InertInfuserRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "inert_infusing";
    }

    public static class Serializer implements RecipeSerializer<InertInfuserRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(AveriumMod.MOD_ID, "inert_infusing");

        @Override
        public InertInfuserRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            int processingTime = GsonHelper.getAsInt(pSerializedRecipe, "processing_time", 200);
            String requiredGas = GsonHelper.getAsString(pSerializedRecipe, "gas_type", "argon");
            float experience = GsonHelper.getAsFloat(pSerializedRecipe, "experience", 0.0F); // Read XP from JSON

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new InertInfuserRecipe(pRecipeId, output, inputs, processingTime, requiredGas, experience);
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
            float experience = pBuffer.readFloat(); // Read XP from buffer
            return new InertInfuserRecipe(pRecipeId, output, inputs, processingTime, gasType, experience);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, InertInfuserRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.getIngredients().size());
            for (Ingredient ing : pRecipe.getIngredients()) {
                ing.toNetwork(pBuffer);
            }
            pBuffer.writeItemStack(pRecipe.getResultItem(RegistryAccess.EMPTY), false);
            pBuffer.writeInt(pRecipe.getProcessingTime());
            pBuffer.writeUtf(pRecipe.getRequiredGasType());
            pBuffer.writeFloat(pRecipe.getExperience()); // Write XP to buffer
        }
    }
}