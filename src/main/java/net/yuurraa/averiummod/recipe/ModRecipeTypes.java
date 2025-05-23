// src/main/java/net/yuurraa/averiummod/recipe/ModRecipeTypes.java
package net.yuurraa.averiummod.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yuurraa.averiummod.AveriumMod;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AveriumMod.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, AveriumMod.MOD_ID);

    public static final RegistryObject<RecipeSerializer<InertInfuserRecipe>> INERT_INFUSING_SERIALIZER =
            SERIALIZERS.register("inert_infusing", () -> InertInfuserRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<InertInfuserRecipe>> INERT_INFUSING_TYPE =
            RECIPE_TYPES.register("inert_infusing", () -> InertInfuserRecipe.Type.INSTANCE);


    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}
