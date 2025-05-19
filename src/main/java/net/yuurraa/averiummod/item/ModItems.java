package net.yuurraa.averiummod.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yuurraa.averiummod.AveriumMod;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AveriumMod.MOD_ID);

    // Ores and Ingots
    public static final RegistryObject<Item> RAW_CRYTHON = ITEMS.register("raw_crython",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRYTHON = ITEMS.register("crython",
            () -> new Item(new Item.Properties()));

    // Craftables
    public static final RegistryObject<Item> GOLD_REINFORCED_STICK = ITEMS.register("gold_reinforced_stick",
            () -> new Item(new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
