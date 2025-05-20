// src/main/java/net/yuurraa/averiummod/item/FrostEbberItem.java
package net.yuurraa.averiummod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class FrostEbberItem extends Item implements ICurioItem {
    public FrostEbberItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().equals("charm"); // Belongs to the "charm" slot
    }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {
        // Use the default ICurioItem tooltip generation
        return ICurioItem.super.getSlotsTooltip(tooltips, stack);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true; // Allow right-click equipping
    }
    // Add other ICurioItem overrides if needed for specific behaviors
}