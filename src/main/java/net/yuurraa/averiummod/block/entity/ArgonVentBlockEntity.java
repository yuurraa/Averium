package net.yuurraa.averiummod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level; // Required for the tick method
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ArgonVentBlockEntity extends BlockEntity {
    // NBT Keys
    public static final String NBT_REMAINING_ARGON = "RemainingArgon";
    public static final String NBT_REGEN_COOLDOWN = "RegenCooldown";

    // Configuration
    private static final int MAX_ARGON_CAPACITY = 5;
    // Time in ticks for one unit of argon to regenerate. 20 ticks = 1 second.
    // Example: 5 minutes = 5 * 60 * 20 = 6000 ticks.
    private static final int TICKS_PER_REGEN = 20 * 5; // 5 minutes

    private int remaining = MAX_ARGON_CAPACITY;
    private int regenerationCooldown = 0;

    public ArgonVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARGON_VENT.get(), pos, state);
    }

    // This method will be called by the BlockEntityTicker
    public static void tick(Level level, BlockPos pos, BlockState state, ArgonVentBlockEntity be) {
        if (level.isClientSide()) {
            return; // Regeneration logic is server-side only
        }

        if (be.remaining < MAX_ARGON_CAPACITY) {
            if (be.regenerationCooldown > 0) {
                be.regenerationCooldown--;
            } else {
                // Regenerate one unit
                be.remaining++;
                be.regenerationCooldown = TICKS_PER_REGEN; // Reset cooldown
                be.setChanged(); // Mark dirty to save NBT data
                // Notify clients of the change so particles/interaction reflects new state
                level.sendBlockUpdated(pos, state, state, 3);
            }
        } else if (be.remaining == MAX_ARGON_CAPACITY) {
            // If it's full, ensure cooldown is reset (e.g. if it was set by mistake or for future logic)
            // or just ensure it doesn't count down if already 0.
            if (be.regenerationCooldown != 0 && be.regenerationCooldown < TICKS_PER_REGEN) {
                be.regenerationCooldown = 0; // Or TICKS_PER_REGEN if you want it to always wait full cycle before checking again
            }
        }
    }

    public boolean hasArgon() {
        return remaining > 0;
    }

    public int getRemainingArgon() {
        return remaining;
    }

    public void collectOne() {
        if (remaining > 0) {
            remaining--;
            // If it was full, and now it's not, ensure the regen process starts/continues
            if (remaining < MAX_ARGON_CAPACITY && regenerationCooldown == 0 && this.level != null && !this.level.isClientSide()) {
                // Start the cooldown for the next regeneration if it wasn't already ticking down
                // (e.g. if it was full and cooldown was 0)
                this.regenerationCooldown = TICKS_PER_REGEN;
            }
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        this.remaining = tag.getInt(NBT_REMAINING_ARGON);
        this.regenerationCooldown = tag.getInt(NBT_REGEN_COOLDOWN);
        // Ensure remaining doesn't exceed max on load, in case MAX_ARGON_CAPACITY changes
        if (this.remaining > MAX_ARGON_CAPACITY) {
            this.remaining = MAX_ARGON_CAPACITY;
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(NBT_REMAINING_ARGON, remaining);
        tag.putInt(NBT_REGEN_COOLDOWN, regenerationCooldown);
    }

    // For initial chunk data sync to client
    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt(NBT_REMAINING_ARGON, remaining);
        // Cooldown is server-side, client doesn't strictly need it unless for display
        return tag;
    }

    // When ClientboundBlockEntityDataPacket is received
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains(NBT_REMAINING_ARGON)) {
            this.remaining = tag.getInt(NBT_REMAINING_ARGON);
        }
    }

    // For block updates (level.sendBlockUpdated)
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        // This will call getUpdateTag() to populate the packet
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            handleUpdateTag(tag); // Use the centralized handler
        }
    }
}