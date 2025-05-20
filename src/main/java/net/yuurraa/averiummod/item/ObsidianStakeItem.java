// src/main/java/net/yuurraa/averiummod/item/ObsidianStakeItem.java
package net.yuurraa.averiummod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class ObsidianStakeItem extends Item implements ICurioItem {
    public ObsidianStakeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().equals("charm"); // Belongs to the "charm" slot
    }

    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {
        return ICurioItem.super.getSlotsTooltip(tooltips, stack);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
    // Add other ICurioItem overrides if needed
}