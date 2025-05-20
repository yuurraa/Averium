// src/main/java/net/yuurraa/averiummod/block/entity/ArgonVentBlockEntity.java
package net.yuurraa.averiummod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ArgonVentBlockEntity extends BlockEntity {
    public static final String NBT_REMAINING_ARGON = "RemainingArgon";
    public static final String NBT_REGEN_COOLDOWN = "RegenCooldown";

    private static final int MAX_ARGON_CAPACITY = 5;
    private static final int TICKS_PER_REGEN = 20 * 60 * 5; // 5 minutes

    // Nausea effect settings
    private static final double NAUSEA_DETECTION_RADIUS = 3.5D;
    private static final int NAUSEA_EXPOSURE_THRESHOLD_TICKS = 20 * 6; // 7 seconds of initial exposure
    // Duration of nausea effect applied/refreshed. Should be > 1 tick.
    // e.g., 60 ticks (3 seconds) will ensure it feels continuous if re-applied often.
    private static final int NAUSEA_EFFECT_DURATION_TICKS = 20 * 4; // 4 seconds duration for each application/refresh

    private int remaining = MAX_ARGON_CAPACITY;
    private int regenerationCooldown = 0;

    // Map to store player UUIDs and their current exposure ticks
    private final Map<UUID, Integer> playerExposureTicksMap = new HashMap<>();

    public ArgonVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ARGON_VENT.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ArgonVentBlockEntity be) {
        if (level.isClientSide()) {
            return;
        }

        // --- Regeneration Logic (existing) ---
        if (be.remaining < MAX_ARGON_CAPACITY) {
            if (be.regenerationCooldown > 0) {
                be.regenerationCooldown--;
            } else {
                be.remaining++;
                be.regenerationCooldown = TICKS_PER_REGEN;
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        } else if (be.remaining == MAX_ARGON_CAPACITY && be.regenerationCooldown != 0) {
            be.regenerationCooldown = 0;
        }

        // --- Nausea Effect Logic (Revised) ---
        if (!be.hasArgon()) {
            if (!be.playerExposureTicksMap.isEmpty()) {
                be.playerExposureTicksMap.clear(); // Clear tracking if vent is inactive
            }
            return;
        }

        AABB detectionArea = new AABB(pos).inflate(NAUSEA_DETECTION_RADIUS);
        List<Player> playersCurrentlyInArea = level.getEntitiesOfClass(Player.class, detectionArea);
        Set<UUID> currentPlayersInAreaUUIDs = new HashSet<>();
        for(Player p : playersCurrentlyInArea) {
            currentPlayersInAreaUUIDs.add(p.getUUID());
        }

        // Iterate over tracked players
        Iterator<Map.Entry<UUID, Integer>> iterator = be.playerExposureTicksMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            UUID playerUUID = entry.getKey();
            int exposureTicks = entry.getValue();

            Player playerInstance = level.getPlayerByUUID(playerUUID); // Get the player instance

            if (playerInstance != null && currentPlayersInAreaUUIDs.contains(playerUUID)) {
                // Player is still in the area
                if (exposureTicks < NAUSEA_EXPOSURE_THRESHOLD_TICKS) {
                    exposureTicks++; // Increment exposure until threshold is met
                    be.playerExposureTicksMap.put(playerUUID, exposureTicks);
                }

                // If threshold is met or exceeded, apply/refresh nausea
                if (exposureTicks >= NAUSEA_EXPOSURE_THRESHOLD_TICKS) {
                    playerInstance.addEffect(new MobEffectInstance(MobEffects.CONFUSION, NAUSEA_EFFECT_DURATION_TICKS, 0, false, true, true));
                    // Keep exposureTicks at threshold or slightly above to indicate they are being continuously affected
                    // No need for a separate cooldown; effect is reapplied as long as they are in the zone post-threshold.
                    be.playerExposureTicksMap.put(playerUUID, NAUSEA_EXPOSURE_THRESHOLD_TICKS); // Keep them at threshold
                }
            } else {
                // Player has left the area or is no longer valid (e.g., logged out)
                iterator.remove();
            }
        }

        // Add new players who have entered the area
        for (Player player : playersCurrentlyInArea) {
            be.playerExposureTicksMap.putIfAbsent(player.getUUID(), 0); // Start new players at 0 exposure ticks
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