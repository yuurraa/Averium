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
// import net.minecraftforge.items.IItemHandler; // Keep this if used by quickMoveStack indirectly // No longer needed directly
import net.minecraftforge.items.SlotItemHandler;
import net.yuurraa.averiummod.block.ModBlocks;
import net.yuurraa.averiummod.block.entity.InertInfuserBlockEntity;
import net.yuurraa.averiummod.item.ModItems;
import org.jetbrains.annotations.NotNull;

public class InertInfuserMenu extends AbstractContainerMenu {
    public final InertInfuserBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    private static final int TE_INVENTORY_SLOT_COUNT = 5;

    public static final int PROGRESS_ARROW_TOTAL_SCALABLE_WIDTH = 34;
    public static final int FUEL_BAR_SCALABLE_WIDTH = 14;

    // Client-side constructor
    public InertInfuserMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        // Pass 5 for ContainerData size to match BE
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(5));
    }

    // Server-side constructor
    public InertInfuserMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.INERT_INFUSER_MENU.get(), pContainerId);
        checkContainerSize(inv, 36); // Player inventory size, not BE slots

        blockEntity = ((InertInfuserBlockEntity) entity);
        this.level = inv.player.level();
        this.data = data;

        // ... (addSlot logic remains the same) ...
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // Slot 0: Metal Input (Updated X: 25, Y: 19)
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.METAL_SLOT, 25, 19) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return InertInfuserBlockEntity.isUnstableMetal(stack);
                }
            });
            // Slot 1: Catalyst Input (Updated X: 25, Y: 45)
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.CATALYST_SLOT, 25, 45) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return InertInfuserBlockEntity.isCatalyst(stack);
                }
            });
            // Slot 2: Gas Cell Input (Updated X: 61, Y: 51)
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.GAS_INPUT_SLOT, 61, 51) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return InertInfuserBlockEntity.isGasCell(stack);
                }
            });
            // Slot 3: Output Slot (Updated X: 112, Y: 32)
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.OUTPUT_SLOT, 112, 32) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false; // Cannot manually place items in output
                }

                @Override
                public void onTake(Player player, ItemStack stack) {
                    // Award experience
                    float experience = blockEntity.getExperienceToAwardAndReset();
                    if (experience > 0.0f) {
                        if (experience > 0) { // Check to ensure some XP to give
                            int expAmount = (int) Math.floor(experience); // Base XP
                            float fractional = experience - expAmount;
                            if (fractional > 0 && Math.random() < fractional) { // Handle fractional XP
                                expAmount++;
                            }
                            if (expAmount > 0) {
                                player.giveExperiencePoints(expAmount);
                            }
                        }
                    }
                    super.onTake(player, stack);
                }
            });
            // Slot 4: Empty Cell Output Slot (New X: 134, Y: 32)
            this.addSlot(new SlotItemHandler(handler, InertInfuserBlockEntity.EMPTY_CELL_SLOT, 134, 32) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return stack.is(ModItems.GAS_CELL.get());
                }
            });
        });

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addDataSlots(data);
    }

    public boolean isCrafting() { return data.get(0) > 0; }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        return maxProgress != 0 && progress != 0 ? progress * PROGRESS_ARROW_TOTAL_SCALABLE_WIDTH / maxProgress : 0;
    }

    public int getScaledFuel() {
        int fuelTime = this.data.get(2);
        int maxFuelTime = this.data.get(3);
        return maxFuelTime != 0 ? fuelTime * FUEL_BAR_SCALABLE_WIDTH / maxFuelTime : 0;
    }

    // New getter for the active gas render type
    public int getActiveGasRenderType() {
        return this.data.get(4); // Index 4 for activeGasRenderType
    }

    // Slot indexing constants for quickMoveStack
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT; // 27
    private static final int VANILLA_PLAYER_SLOT_COUNT = PLAYER_INVENTORY_SLOT_COUNT + HOTBAR_SLOT_COUNT; // 36

    private static final int BE_FIRST_SLOT_INDEX = 0;
    private static final int BE_LAST_SLOT_INDEX = TE_INVENTORY_SLOT_COUNT - 1; // 0-4

    private static final int PLAYER_INVENTORY_FIRST_SLOT_INDEX = TE_INVENTORY_SLOT_COUNT; // Starts at 5
    private static final int PLAYER_INVENTORY_LAST_SLOT_INDEX = PLAYER_INVENTORY_FIRST_SLOT_INDEX + PLAYER_INVENTORY_SLOT_COUNT - 1; // 5 + 27 - 1 = 31

    private static final int PLAYER_HOTBAR_FIRST_SLOT_INDEX = PLAYER_INVENTORY_LAST_SLOT_INDEX + 1; // Starts at 32
    private static final int PLAYER_HOTBAR_LAST_SLOT_INDEX = PLAYER_HOTBAR_FIRST_SLOT_INDEX + HOTBAR_SLOT_COUNT - 1; // 32 + 9 - 1 = 40

    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        if (pIndex >= BE_FIRST_SLOT_INDEX && pIndex <= BE_LAST_SLOT_INDEX) {
            if (!moveItemStackTo(sourceStack, PLAYER_INVENTORY_FIRST_SLOT_INDEX, PLAYER_HOTBAR_LAST_SLOT_INDEX + 1, true)) {
                return ItemStack.EMPTY;
            }
        }
        else if (pIndex >= PLAYER_INVENTORY_FIRST_SLOT_INDEX && pIndex <= PLAYER_HOTBAR_LAST_SLOT_INDEX) {
            boolean movedToBE = false;
            if (InertInfuserBlockEntity.isUnstableMetal(sourceStack)) {
                movedToBE = moveItemStackTo(sourceStack, InertInfuserBlockEntity.METAL_SLOT, InertInfuserBlockEntity.METAL_SLOT + 1, false);
            } else if (InertInfuserBlockEntity.isGasCell(sourceStack)) { // Filled Gas Cells
                movedToBE = moveItemStackTo(sourceStack, InertInfuserBlockEntity.GAS_INPUT_SLOT, InertInfuserBlockEntity.GAS_INPUT_SLOT + 1, false);
            } else if (sourceStack.is(ModItems.GAS_CELL.get())) { // Empty Gas Cells
                movedToBE = moveItemStackTo(sourceStack, InertInfuserBlockEntity.EMPTY_CELL_SLOT, InertInfuserBlockEntity.EMPTY_CELL_SLOT + 1, false);
            } else if (InertInfuserBlockEntity.isCatalyst(sourceStack)) {
                movedToBE = moveItemStackTo(sourceStack, InertInfuserBlockEntity.CATALYST_SLOT, InertInfuserBlockEntity.CATALYST_SLOT + 1, false);
            }

            if (!movedToBE) {
                if (pIndex >= PLAYER_INVENTORY_FIRST_SLOT_INDEX && pIndex <= PLAYER_INVENTORY_LAST_SLOT_INDEX) {
                    if (!moveItemStackTo(sourceStack, PLAYER_HOTBAR_FIRST_SLOT_INDEX, PLAYER_HOTBAR_LAST_SLOT_INDEX + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= PLAYER_HOTBAR_FIRST_SLOT_INDEX && pIndex <= PLAYER_HOTBAR_LAST_SLOT_INDEX) {
                    if (!moveItemStackTo(sourceStack, PLAYER_INVENTORY_FIRST_SLOT_INDEX, PLAYER_INVENTORY_LAST_SLOT_INDEX + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        } else {
            System.out.println("Invalid slotIndex for quickMoveStack: " + pIndex);
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
