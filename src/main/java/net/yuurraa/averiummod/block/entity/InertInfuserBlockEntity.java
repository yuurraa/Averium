// src/main/java/net/yuurraa/averiummod/block/entity/InertInfuserBlockEntity.java
package net.yuurraa.averiummod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.yuurraa.averiummod.AveriumMod; // For LOGGER
import net.yuurraa.averiummod.item.ModItems;
import net.yuurraa.averiummod.recipe.InertInfuserRecipe;
import net.yuurraa.averiummod.recipe.ModRecipeTypes;
import net.yuurraa.averiummod.screen.InertInfuserMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InertInfuserBlockEntity extends BlockEntity implements MenuProvider {

    public static final int METAL_SLOT = 0;
    public static final int CATALYST_SLOT = 1;
    public static final int GAS_INPUT_SLOT = 2;
    public static final int OUTPUT_SLOT = 3;
    public static final int EMPTY_CELL_SLOT = 4;

    private static final int INVENTORY_SIZE = 5;

    private float experienceToAward = 0.0f;

    // ... (itemHandler remains the same) ...
    private final ItemStackHandler itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case METAL_SLOT -> InertInfuserBlockEntity.isUnstableMetal(stack);
                case CATALYST_SLOT -> InertInfuserBlockEntity.isCatalyst(stack);
                case GAS_INPUT_SLOT -> InertInfuserBlockEntity.isGasCell(stack);
                case EMPTY_CELL_SLOT -> stack.is(ModItems.GAS_CELL.get()); // Only empty cells
                case OUTPUT_SLOT -> true;
                default -> super.isItemValid(slot, stack);
            };
        }
    };


    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;
    private int fuelTime = 0;
    private int maxFuelTime = 0;
    private String activeGasType = "";
    private ItemStack currentFuelItemStack = ItemStack.EMPTY;
    private int activeGasRenderType = 0; // 0: none, 1: Xenon, 2: Argon

    public InertInfuserBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.INERT_INFUSER.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> InertInfuserBlockEntity.this.progress;
                    case 1 -> InertInfuserBlockEntity.this.maxProgress;
                    case 2 -> InertInfuserBlockEntity.this.fuelTime;
                    case 3 -> InertInfuserBlockEntity.this.maxFuelTime;
                    case 4 -> InertInfuserBlockEntity.this.activeGasRenderType; // New data field
                    default -> 0;
                };
            }
            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> InertInfuserBlockEntity.this.progress = value;
                    case 1 -> InertInfuserBlockEntity.this.maxProgress = value;
                    case 2 -> InertInfuserBlockEntity.this.fuelTime = value;
                    case 3 -> InertInfuserBlockEntity.this.maxFuelTime = value;
                    case 4 -> InertInfuserBlockEntity.this.activeGasRenderType = value; // New data field
                }
            }
            @Override
            public int getCount() { return 5; } // Increased count to 5
        };
    }

    public static boolean isUnstableMetal(ItemStack stack) {
        return stack.is(ModItems.UNSTABLE_CRYTHON.get()) || stack.is(ModItems.UNSTABLE_INFERNIUM.get());
    }

    public static boolean isGasCell(ItemStack stack) {
        return stack.is(ModItems.ARGON_GAS_CELL.get()) || stack.is(ModItems.XENON_GAS_CELL.get());
    }

    public static boolean isCatalyst(ItemStack stack) {
        return stack.is(Items.REDSTONE) || stack.is(Items.GLOWSTONE_DUST);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.averiummod.inert_infuser");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new InertInfuserMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("progress", progress);
        nbt.putInt("fuelTime", fuelTime);
        nbt.putInt("maxFuelTime", maxFuelTime);
        nbt.putString("activeGasType", activeGasType);
        nbt.put("currentFuelItem", currentFuelItemStack.save(new CompoundTag()));
        nbt.putInt("activeGasRenderType", activeGasRenderType); // Save the new field
        nbt.putFloat("experienceToAward", experienceToAward);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("progress");
        fuelTime = nbt.getInt("fuelTime");
        maxFuelTime = nbt.getInt("maxFuelTime");
        activeGasType = nbt.getString("activeGasType");
        currentFuelItemStack = ItemStack.of(nbt.getCompound("currentFuelItem"));
        activeGasRenderType = nbt.getInt("activeGasRenderType"); // Load the new field
        experienceToAward = nbt.getFloat("experienceToAward");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        if (this.level != null) {
            Containers.dropContents(this.level, this.worldPosition, inventory);
        }
    }

    private static int getGasCellFuelValue(ItemStack gasCell) {
        if (gasCell.is(ModItems.ARGON_GAS_CELL.get())) return 800;
        if (gasCell.is(ModItems.XENON_GAS_CELL.get())) return 800;
        return 0;
    }

    private static String getGasCellType(ItemStack gasCell) {
        if (gasCell.is(ModItems.ARGON_GAS_CELL.get())) return "argon";
        if (gasCell.is(ModItems.XENON_GAS_CELL.get())) return "xenon";
        return "";
    }

    private boolean canProcess(Optional<InertInfuserRecipe> recipeOpt) {
        if (recipeOpt.isEmpty() || this.activeGasType.isEmpty() || this.fuelTime <= 0) {
            return false;
        }
        InertInfuserRecipe recipe = recipeOpt.get();
        if (this.level == null) return false;
        ItemStack resultItem = recipe.getResultItem(this.level.registryAccess());

        return recipe.getRequiredGasType().equals(this.activeGasType) &&
                canInsertItemIntoOutputSlot(this.itemHandler, resultItem) &&
                canInsertAmountIntoOutputSlot(this.itemHandler, resultItem.getCount());
    }


    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, InertInfuserBlockEntity pBlockEntity) {
        if (pLevel.isClientSide()) {
            return;
        }

        boolean changed = false;

        SimpleContainer inventoryForRecipeCheck = new SimpleContainer(2);
        inventoryForRecipeCheck.setItem(0, pBlockEntity.itemHandler.getStackInSlot(METAL_SLOT));
        inventoryForRecipeCheck.setItem(1, pBlockEntity.itemHandler.getStackInSlot(CATALYST_SLOT));
        Optional<InertInfuserRecipe> recipeOpt = pLevel.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.INERT_INFUSING_TYPE.get(), inventoryForRecipeCheck, pLevel);

        ItemStack potentialFuelStack = pBlockEntity.itemHandler.getStackInSlot(GAS_INPUT_SLOT);
        if (isGasCell(potentialFuelStack)) {
            String potentialGasTypeString = getGasCellType(potentialFuelStack); // Renamed to avoid conflict
            boolean canReturnOldFuel = pBlockEntity.currentFuelItemStack.isEmpty() ||
                    pBlockEntity.itemHandler.getStackInSlot(EMPTY_CELL_SLOT).isEmpty() ||
                    (pBlockEntity.itemHandler.getStackInSlot(EMPTY_CELL_SLOT).is(ModItems.GAS_CELL.get()) &&
                            pBlockEntity.itemHandler.getStackInSlot(EMPTY_CELL_SLOT).getCount() < pBlockEntity.itemHandler.getStackInSlot(EMPTY_CELL_SLOT).getMaxStackSize());

            boolean loadBecauseDepleted = pBlockEntity.fuelTime <= 0;
            boolean loadBecauseOverride = false;

            if (!loadBecauseDepleted) {
                if (!potentialGasTypeString.equals(pBlockEntity.activeGasType) &&
                        pBlockEntity.progress == 0 &&
                        canReturnOldFuel &&
                        recipeOpt.isPresent() &&
                        recipeOpt.get().getRequiredGasType().equals(potentialGasTypeString)) {
                    loadBecauseOverride = true;
                }
            }

            if (loadBecauseDepleted || loadBecauseOverride) {
                int fuelValue = getGasCellFuelValue(potentialFuelStack);
                if (fuelValue > 0) {
                    if (!pBlockEntity.currentFuelItemStack.isEmpty()) {
                        ItemStack oldRemainder = pBlockEntity.currentFuelItemStack.getItem().getCraftingRemainingItem(pBlockEntity.currentFuelItemStack.copy());
                        if (oldRemainder != null && !oldRemainder.isEmpty()) {
                            pBlockEntity.itemHandler.insertItem(EMPTY_CELL_SLOT, oldRemainder, false);
                        }
                    }

                    pBlockEntity.fuelTime = fuelValue;
                    pBlockEntity.maxFuelTime = fuelValue;
                    pBlockEntity.activeGasType = potentialGasTypeString; // Use the renamed variable

                    // Update activeGasRenderType
                    if (potentialGasTypeString.equals("xenon")) {
                        pBlockEntity.activeGasRenderType = 1;
                    } else if (potentialGasTypeString.equals("argon")) {
                        pBlockEntity.activeGasRenderType = 2;
                    } else {
                        pBlockEntity.activeGasRenderType = 0; // Should not happen if isGasCell is true
                    }

                    pBlockEntity.currentFuelItemStack = potentialFuelStack.copy();
                    pBlockEntity.currentFuelItemStack.setCount(1);
                    pBlockEntity.itemHandler.extractItem(GAS_INPUT_SLOT, 1, false);
                    changed = true;
                }
            }
        }

        if (pBlockEntity.canProcess(recipeOpt)) {
            InertInfuserRecipe recipe = recipeOpt.get();
            pBlockEntity.maxProgress = recipe.getProcessingTime();
            pBlockEntity.progress++;
            pBlockEntity.fuelTime--;
            changed = true;

            if (pBlockEntity.progress >= pBlockEntity.maxProgress) {
                pBlockEntity.itemHandler.extractItem(METAL_SLOT, 1, false);
                pBlockEntity.itemHandler.extractItem(CATALYST_SLOT, 1, false);
                ItemStack resultCopy = recipe.getResultItem(pLevel.registryAccess()).copy();
                pBlockEntity.itemHandler.insertItem(OUTPUT_SLOT, resultCopy, false);
                pBlockEntity.experienceToAward = recipe.getExperience(); // <--- ADD THIS LINE
                pBlockEntity.progress = 0;
                changed = true;
            }
        } else {
            if (pBlockEntity.progress > 0) {
                pBlockEntity.progress = 0;
                changed = true;
            }
        }

        if (pBlockEntity.fuelTime <= 0 && !pBlockEntity.activeGasType.isEmpty()) {
            if (!pBlockEntity.currentFuelItemStack.isEmpty()) {
                ItemStack remainder = pBlockEntity.currentFuelItemStack.getItem().getCraftingRemainingItem(pBlockEntity.currentFuelItemStack.copy());
                if (remainder != null && !remainder.isEmpty()) {
                    pBlockEntity.itemHandler.insertItem(EMPTY_CELL_SLOT, remainder, false);
                }
                pBlockEntity.currentFuelItemStack = ItemStack.EMPTY;
            }
            pBlockEntity.activeGasType = "";
            pBlockEntity.maxFuelTime = 0;
            pBlockEntity.activeGasRenderType = 0; // Reset render type when fuel runs out
            if (pBlockEntity.progress > 0) pBlockEntity.progress = 0;
            changed = true;
        }

        if (changed) {
            setChanged(pLevel, pPos, pState);
        }
    }

    private static boolean canInsertItemIntoOutputSlot(ItemStackHandler handler, ItemStack outputToInsert) {
        ItemStack currentOutputStack = handler.getStackInSlot(OUTPUT_SLOT);
        if (currentOutputStack.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameTags(currentOutputStack, outputToInsert)) {
            return false;
        }
        return currentOutputStack.getCount() + outputToInsert.getCount() <= Math.min(handler.getSlotLimit(OUTPUT_SLOT), currentOutputStack.getMaxStackSize());
    }

    private static boolean canInsertAmountIntoOutputSlot(ItemStackHandler handler, int amountToInsert) {
        ItemStack currentOutputStack = handler.getStackInSlot(OUTPUT_SLOT);
        if (currentOutputStack.isEmpty()) {
            // If slot is empty, it can accept up to its limit or item's max stack size.
            // This check alone is insufficient without knowing the item.
            // However, used in conjunction with canInsertItemIntoOutputSlot, it's fine.
            return true;
        }
        return currentOutputStack.getCount() + amountToInsert <= Math.min(handler.getSlotLimit(OUTPUT_SLOT), currentOutputStack.getMaxStackSize());
    }

    public float getExperienceToAwardAndReset() {
        float xp = this.experienceToAward;
        this.experienceToAward = 0.0f;
        return xp;
    }
}