// src/main/java/net/yuurraa/averiummod/item/ModItems.java
package net.yuurraa.averiummod.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yuurraa.averiummod.AveriumMod;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, AveriumMod.MOD_ID);

    // Averium Charms
    public static final RegistryObject<Item> FROST_EBBER = ITEMS.register("frost_ebber", // For Crython
            () -> new FrostEbberItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> OBSIDIAN_STAKE = ITEMS.register("obsidian_stake", // For Infernium
            () -> new ObsidianStakeItem(new Item.Properties().stacksTo(1)));

    // Crython
    public static final RegistryObject<Item> RAW_CRYTHON = ITEMS.register("raw_crython",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UNSTABLE_CRYTHON = ITEMS.register("unstable_crython",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STABLE_CRYTHON = ITEMS.register("stable_crython",
            () -> new Item(new Item.Properties()));

    // Infernium
    public static final RegistryObject<Item> RAW_INFERNIUM = ITEMS.register("raw_infernium",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> UNSTABLE_INFERNIUM = ITEMS.register("unstable_infernium",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> STABLE_INFERNIUM = ITEMS.register("stable_infernium",
            () -> new Item(new Item.Properties()));

    // Processable
    public static final RegistryObject<Item> GOLD_REINFORCED_STICK = ITEMS.register("gold_reinforced_stick",
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BOTTLED_ARGON = ITEMS.register("bottled_argon",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .craftRemainder(Items.GLASS_BOTTLE)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
