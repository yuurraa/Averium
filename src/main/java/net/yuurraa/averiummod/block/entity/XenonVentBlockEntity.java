// src/main/java/net/yuurraa/averiummod/block/entity/XenonVentBlockEntity.java
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
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class XenonVentBlockEntity extends BlockEntity {
    // NBT Keys
    public static final String NBT_REMAINING_XENON = "RemainingXenon";
    public static final String NBT_REGEN_COOLDOWN_XENON = "RegenCooldownXenon";

    // Xenon Gas Properties
    private static final int MAX_XENON_CAPACITY = 1;
    private static final int TICKS_PER_XENON_REGEN = 20 * 60 * 10; // 7 minutes

    // Xenon Vent Proximity Poison Constants
    private static final double POISON_DETECTION_RADIUS = 3.5D;
    private static final int POISON_INITIAL_EXPOSURE_THRESHOLD_TICKS = 20 * 5; // 5 seconds to get first poisoned
    private static final int POISON_EFFECT_DURATION_TICKS = 20 * 6;    // Apply Poison for 6 seconds (allows multiple damage ticks)
    private static final int POISON_EFFECT_AMPLIFIER = 0;              // Poison I
    // Refresh poison if its remaining duration is less than this (e.g., 2 seconds)
    private static final int POISON_REFRESH_THRESHOLD_TICKS = 20 * 2;

    private int remaining = MAX_XENON_CAPACITY;
    private int regenerationCooldown = 0;
    private final Map<UUID, Integer> playerExposureTicksMap = new HashMap<>();

    public XenonVentBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.XENON_VENT.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, XenonVentBlockEntity be) {
        if (level.isClientSide()) {
            return;
        }

        // --- Xenon Gas Regeneration Logic ---
        if (be.remaining < MAX_XENON_CAPACITY) {
            if (be.regenerationCooldown > 0) {
                be.regenerationCooldown--;
            } else {
                be.remaining++;
                be.regenerationCooldown = TICKS_PER_XENON_REGEN;
                be.setChanged();
                level.sendBlockUpdated(pos, state, state, 3);
            }
        } else if (be.remaining == MAX_XENON_CAPACITY && be.regenerationCooldown != 0) {
            be.regenerationCooldown = 0;
        }

        // --- Xenon Proximity Poison Logic (Revised for Consistent Damage) ---
        if (!be.hasXenon()) {
            if (!be.playerExposureTicksMap.isEmpty()) {
                be.playerExposureTicksMap.clear();
            }
            return;
        }

        AABB detectionArea = new AABB(pos).inflate(POISON_DETECTION_RADIUS);
        Map<UUID, Player> playersCurrentlyInArea = new HashMap<>();
        for (Player player : level.getEntitiesOfClass(Player.class, detectionArea)) {
            if (!player.isCreative() && !player.isSpectator()) {
                playersCurrentlyInArea.put(player.getUUID(), player);
            }
        }

        Iterator<Map.Entry<UUID, Integer>> iterator = be.playerExposureTicksMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            UUID playerUUID = entry.getKey();
            int currentExposureTicks = entry.getValue();
            Player playerInstance = playersCurrentlyInArea.get(playerUUID);

            if (playerInstance != null) { // Player is still in the detection area
                if (currentExposureTicks < POISON_INITIAL_EXPOSURE_THRESHOLD_TICKS) {
                    currentExposureTicks++;
                    be.playerExposureTicksMap.put(playerUUID, currentExposureTicks);
                }

                if (currentExposureTicks >= POISON_INITIAL_EXPOSURE_THRESHOLD_TICKS) {
                    MobEffectInstance existingPoison = playerInstance.getEffect(MobEffects.POISON);
                    // Apply new poison effect if:
                    // 1. Player doesn't have poison.
                    // 2. Or, player has poison, but its amplifier is different (e.g., from another source).
                    // 3. Or, player has poison from us, but its duration is below our refresh threshold.
                    if (existingPoison == null ||
                            existingPoison.getAmplifier() != POISON_EFFECT_AMPLIFIER ||
                            (existingPoison.getAmplifier() == POISON_EFFECT_AMPLIFIER && existingPoison.getDuration() < POISON_REFRESH_THRESHOLD_TICKS)) {

                        playerInstance.addEffect(new MobEffectInstance(
                                MobEffects.POISON,
                                POISON_EFFECT_DURATION_TICKS,
                                POISON_EFFECT_AMPLIFIER,
                                false, // isAmbient
                                true,  // showParticles
                                true   // showIcon
                        ));
                    }
                    // Keep exposure ticks high to indicate they are in the "continuously affected" state
                    be.playerExposureTicksMap.put(playerUUID, Math.min(currentExposureTicks, POISON_INITIAL_EXPOSURE_THRESHOLD_TICKS + 40));
                }
            } else {
                iterator.remove(); // Player left the area
            }
        }

        for (Player newPlayerInArea : playersCurrentlyInArea.values()) {
            be.playerExposureTicksMap.putIfAbsent(newPlayerInArea.getUUID(), 0);
        }
    }

    public boolean hasXenon() {
        return remaining > 0;
    }

    public void collectOneXenon() {
        if (remaining > 0) {
            remaining--;
            if (remaining < MAX_XENON_CAPACITY && regenerationCooldown == 0 && this.level != null && !this.level.isClientSide()) {
                this.regenerationCooldown = TICKS_PER_XENON_REGEN;
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
        this.remaining = tag.getInt(NBT_REMAINING_XENON);
        this.regenerationCooldown = tag.getInt(NBT_REGEN_COOLDOWN_XENON);
        if (this.remaining > MAX_XENON_CAPACITY) {
            this.remaining = MAX_XENON_CAPACITY;
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt(NBT_REMAINING_XENON, remaining);
        tag.putInt(NBT_REGEN_COOLDOWN_XENON, regenerationCooldown);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putInt(NBT_REMAINING_XENON, remaining);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        if (tag.contains(NBT_REMAINING_XENON)) {
            this.remaining = tag.getInt(NBT_REMAINING_XENON);
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        CompoundTag tag = pkt.getTag();
        if (tag != null) {
            handleUpdateTag(tag);
        }
    }
}