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
import net.yuurraa.averiummod.block.ModBlocks;
import net.yuurraa.averiummod.block.entity.InertInfuserBlockEntity;

public class InertInfuserMenu extends AbstractContainerMenu {
    public final InertInfuserBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    private static final int TE_INVENTORY_SLOT_COUNT = 4;

    // New constants for progress/fuel bar dimensions for scaling logic
    public static final int PROGRESS_ARROW_TOTAL_SCALABLE_WIDTH = 34; // 30 for rect + 4 for triangle
    public static final int FUEL_BAR_SCALABLE_WIDTH = 14; // Assuming horizontal fill

    public InertInfuserMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public InertInfuserMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.INERT_INFUSER_MENU.get(), pContainerId);
        checkContainerSize(inv, TE_INVENTORY_SLOT_COUNT);
        blockEntity = ((InertInfuserBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Updated Slot Positions:
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.METAL_SLOT, 36, 19));     // Metal Input
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.CATALYST_SLOT, 36, 45));  // Catalyst Input
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.GAS_INPUT_SLOT, 72, 51)); // Gas Cell Input
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.OUTPUT_SLOT, 123, 32));   // Output Slot
        });

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        // Scale to the total visual width of the arrow
        return maxProgress != 0 && progress != 0 ? progress * PROGRESS_ARROW_TOTAL_SCALABLE_WIDTH / maxProgress : 0;
    }

    public int getScaledFuel() {
        int fuelTime = this.data.get(2);
        int maxFuelTime = this.data.get(3);
        // Scale to the width of the fuel bar, assuming horizontal fill
        return maxFuelTime != 0 ? fuelTime * FUEL_BAR_SCALABLE_WIDTH / maxFuelTime : 0;
    }

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex < VANILLA_SLOT_COUNT) {
            // Try to move to TE input slots (indices 0, 1, 2 in BE handler).
            // The corresponding menu slot indices are TE_INVENTORY_FIRST_SLOT_INDEX to TE_INVENTORY_FIRST_SLOT_INDEX + 2
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + 3, false)) { // Try to merge into first 3 BE slots
                if (pIndex < HOTBAR_SLOT_COUNT) { // From hotbar to main inventory
                    if (!moveItemStackTo(sourceStack, HOTBAR_SLOT_COUNT, VANILLA_SLOT_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex < VANILLA_SLOT_COUNT) { // From main inventory to hotbar
                    if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, HOTBAR_SLOT_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) { // From TE slots to player inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }

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
