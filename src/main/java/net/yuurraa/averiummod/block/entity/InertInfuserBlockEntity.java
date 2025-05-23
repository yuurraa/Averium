// src/main/java/net/yuurraa/averiummod/block/entity/InertInfuserBlockEntity.java
package net.yuurraa.averiummod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.yuurraa.averiummod.screen.InertInfuserMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InertInfuserBlockEntity extends BlockEntity implements MenuProvider {

    private final ItemStackHandler itemHandler = new ItemStackHandler(5) { // 3 inputs, 1 fuel, 1 output
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            // Later, we might want to send block updates to the client if needed
            // if(!level.isClientSide()) {
            //    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            // }
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // For syncing data like progress to the GUI
    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 72; // Example: 72 ticks for crafting
    private int fuelTime = 0;
    private int maxFuelTime = 0; // Will be set by the gas cell

    // Slot constants
    public static final int METAL_SLOT = 0;
    public static final int CATALYST_SLOT = 1;
    public static final int GAS_INPUT_SLOT = 2; // Where player puts a new gas cell to be "consumed" into fuelTime
    public static final int OUTPUT_SLOT = 3;
    public static final int FUEL_DISPLAY_SLOT = 4; // A virtual/display slot for the "active" gas cell or just to show fuel type.
    // Or we can simplify and make GAS_INPUT_SLOT also the fuel slot.
    // For now, let's assume GAS_INPUT_SLOT is where fuel is consumed from.

    public InertInfuserBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.INERT_INFUSER.get(), pPos, pBlockState); // Will create this registration next
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> InertInfuserBlockEntity.this.progress;
                    case 1 -> InertInfuserBlockEntity.this.maxProgress;
                    case 2 -> InertInfuserBlockEntity.this.fuelTime;
                    case 3 -> InertInfuserBlockEntity.this.maxFuelTime;
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
                }
            }

            @Override
            public int getCount() {
                return 4; // Number of integers to sync
            }
        };
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
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
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
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("inert_infuser.progress", progress);
        nbt.putInt("inert_infuser.fuelTime", fuelTime);
        nbt.putInt("inert_infuser.maxFuelTime", maxFuelTime);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("inert_infuser.progress");
        fuelTime = nbt.getInt("inert_infuser.fuelTime");
        maxFuelTime = nbt.getInt("inert_infuser.maxFuelTime");
    }

    public void drops() {
        // Helper for dropping items when block is broken
        // Not fully implemented yet, needs access to level and pos
        // SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        // for(int i = 0; i < itemHandler.getSlots(); i++) {
        //    inventory.setItem(i, itemHandler.getStackInSlot(i));
        // }
        // if(this.level != null) {
        //     Containers.dropContents(this.level, this.worldPosition, inventory);
        // }
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, InertInfuserBlockEntity pBlockEntity) {
        if(pLevel.isClientSide()) {
            return;
        }

        // Crafting Logic will go here:
        // 1. Check if it has fuel (pBlockEntity.fuelTime > 0)
        // 2. If not, try to consume fuel from GAS_INPUT_SLOT (if it's a valid gas cell)
        //    - If consumed, set pBlockEntity.fuelTime and pBlockEntity.maxFuelTime
        // 3. If it has fuel:
        //    a. Check if inputs (METAL_SLOT, CATALYST_SLOT) + current fuel type can make a recipe
        //    b. And if output slot (OUTPUT_SLOT) can accept the result
        //    c. If yes to all:
        //        i. Decrement fuelTime
        //       ii. Increment progress
        //      iii. If progress >= maxProgress:
        //           - Craft the item (consume inputs, place output)
        //           - Reset progress
        //    d. If no to recipe/output:
        //        i. Reset progress
        //        ii. (Optional: if fuel was only consumed while actively crafting, don't decrement fuel here)
        // 4. If no fuel and cannot consume new fuel:
        //    a. Reset progress

        // Mark for saving and update client if something changed
        // setChanged(pLevel, pPos, pState);
    }
}