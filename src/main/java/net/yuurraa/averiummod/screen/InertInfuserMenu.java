// src/main/java/net/yuurraa/averiummod/screen/InertInfuserMenu.java
package net.yuurraa.averiummod.screen;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import net.yuurraa.averiummod.block.ModBlocks; // For stillValid check
import net.yuurraa.averiummod.block.entity.InertInfuserBlockEntity; // Import your BlockEntity

public class InertInfuserMenu extends AbstractContainerMenu {
    public final InertInfuserBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    // Slot count for the Block Entity's inventory part of this menu
    private static final int TE_INVENTORY_SLOT_COUNT = 4; // Metal, Catalyst, Gas, Output

    // Client-side constructor
    public InertInfuserMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    // Server-side constructor
    public InertInfuserMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.INERT_INFUSER_MENU.get(), pContainerId); // We'll register ModMenuTypes next
        checkContainerSize(inv, TE_INVENTORY_SLOT_COUNT); // Check against the number of BE slots in the menu
        blockEntity = ((InertInfuserBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        // Add slots for the BlockEntity's inventory
        // These are the first slots the menu "knows" about after player inventory/hotbar if not careful with indexing.
        // However, standard practice is to add player inv/hotbar first, then BE slots.
        // The SlotItemHandler uses indices relative to the IItemHandler (0, 1, 2, 3 for the BE).
        // The menu's internal slot indexing will be sequential.

        // BE Slots (Slot indices for the menu: 0, 1, 2, 3 for BE if added first, or 36, 37, 38, 39 if after player inv)
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Slot Index in ItemHandler, X position in GUI, Y position in GUI
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.METAL_SLOT, 26, 17));     // Metal Input
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.CATALYST_SLOT, 26, 53));  // Catalyst Input
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.GAS_INPUT_SLOT, 62, 35)); // Gas Cell Input (Fuel)
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.OUTPUT_SLOT, 116, 35));   // Output Slot
        });

        addPlayerInventory(inv); // Adds player inventory slots (menu indices 4-30 or similar, after BE slots)
        addPlayerHotbar(inv);  // Adds player hotbar slots (menu indices 31-39 or similar)


        addDataSlots(data);
    }

    // Getter methods for GUI screen to access synced data
    public boolean isCrafting() {
        return data.get(0) > 0; // progress > 0
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24; // Width of your progress arrow texture in pixels

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    public int getScaledFuel() {
        int fuelTime = this.data.get(2);
        int maxFuelTime = this.data.get(3);
        int fuelBarSize = 13; // Height of your fuel bar texture in pixels (example)

        return maxFuelTime != 0 ? (int)(((float)fuelTime / (float)maxFuelTime) * fuelBarSize) : 0;
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a slot to the container, it automatically increases the slot index.
    // This means queries like "is a given slot index in the tile inventory?" are possible queries.
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // TE_INVENTORY_SLOT_COUNT is defined at the top of the class (currently 4)

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla slot so merge the stack into the tile inventory
            // Try to merge into output slot first if applicable (e.g. fuel from player inv to fuel slot)
            // Then try to merge into input slots
            // For Inert Infuser:
            // - Metal (slot 0 in BE handler)
            // - Catalyst (slot 1 in BE handler)
            // - Gas (slot 2 in BE handler)
            // Output (slot 3 in BE handler) is usually not a target for shift-click *from* player inventory.

            // Example: if item is a gas cell, try to move to gas input slot
            // if (sourceStack.getItem() == ModItems.ARGON_GAS_CELL.get() || sourceStack.getItem() == ModItems.XENON_GAS_CELL.get()) { // You'll need to define what items are fuel
            //    if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + InertInfuserBlockEntity.GAS_INPUT_SLOT, TE_INVENTORY_FIRST_SLOT_INDEX + InertInfuserBlockEntity.GAS_INPUT_SLOT + 1, false)) {
            //        return ItemStack.EMPTY;
            //    }
            // } else if (true) { // TODO: Add logic for metal input, check item type
            //    if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + InertInfuserBlockEntity.METAL_SLOT, TE_INVENTORY_FIRST_SLOT_INDEX + InertInfuserBlockEntity.METAL_SLOT + 1, false)) {
            //        return ItemStack.EMPTY;
            //    }
            // } else if (true) { // TODO: Add logic for catalyst input, check item type
            //    if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX + InertInfuserBlockEntity.CATALYST_SLOT, TE_INVENTORY_FIRST_SLOT_INDEX + InertInfuserBlockEntity.CATALYST_SLOT + 1, false)) {
            //        return ItemStack.EMPTY;
            //    }
            // }
            // Generic fallback: try to merge into any of the BE input slots (0, 1, 2)
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT - 1, false)) { // -1 because output slot is usually not a target from player inv
                // If it failed to merge into the BE input slots, try merging into other player inventory slots
                if (pIndex < HOTBAR_SLOT_COUNT) { // If source is hotbar
                    if (!moveItemStackTo(sourceStack, HOTBAR_SLOT_COUNT, HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex < HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT) { // If source is player inventory
                    if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + HOTBAR_SLOT_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY; // Should not happen
                }
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the player's inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot to empty
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }


    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                pPlayer, ModBlocks.INERT_INFUSER.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}
