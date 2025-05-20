// src/main/java/net/yuurraa/averiummod/item/InertCharmItem.java
package net.yuurraa.averiummod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.List;

public class InertCharmItem extends Item implements ICurioItem {

    public InertCharmItem(Properties properties) {
        super(properties);
    }

    /**
     * Determines if the item can be equipped in the given slot.
     * This is essential for ICurioItem.
     */
    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return slotContext.identifier().equals("charm");
    }

    /**
     * Adds tooltips to the item, including which Curio slots it can go into.
     * This method should return the modified list of tooltips.
     */
    @Override
    public List<Component> getSlotsTooltip(List<Component> tooltips, ItemStack stack) {
        return ICurioItem.super.getSlotsTooltip(tooltips, stack);
    }

    /**
     * Determines if the player can equip this item by right-clicking with it in hand.
     */
    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }
}