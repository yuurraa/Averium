// src/main/java/net/yuurraa/averiummod/event/ModServerEvents.java
package net.yuurraa.averiummod.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.block.ModBlocks;
import net.yuurraa.averiummod.item.ModItems;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = AveriumMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModServerEvents {
    // --- Proximity Effect Constants ---
    private static final int CRYTHON_PROXIMITY_SLOWNESS_RADIUS = 4;
    private static final int CRYTHON_PROXIMITY_SLOWNESS_DURATION = 60;
    private static final int CRYTHON_PROXIMITY_SLOWNESS_AMPLIFIER = 0;

    private static final int INFERNIUM_PROXIMITY_ABLAZE_RADIUS = 3;
    private static final int INFERNIUM_PROXIMITY_ABLAZE_SECONDS = 3;

    // --- Inventory Effect Constants & Item Sets (as before) ---
    private static final Set<RegistryObject<Item>> CRYTHON_HAZARDOUS_ITEMS_ROBJS = Set.of(
            ModItems.RAW_CRYTHON,
            ModItems.UNSTABLE_CRYTHON
    );
    private static Set<Item> resolvedCrythonHazardousItems = null;
    private static Set<Item> getResolvedCrythonHazardousItems() {
        if (resolvedCrythonHazardousItems == null) {
            resolvedCrythonHazardousItems = CRYTHON_HAZARDOUS_ITEMS_ROBJS.stream()
                    .map(RegistryObject::get).collect(Collectors.toSet());
        }
        return resolvedCrythonHazardousItems;
    }
    private static final int CRYTHON_INVENTORY_SLOWNESS_DURATION = 10;
    private static final int CRYTHON_INVENTORY_SLOWNESS_AMPLIFIER = 0;

    private static final Set<RegistryObject<Item>> INFERNIUM_HAZARDOUS_ITEMS_ROBJS = Set.of(
            ModItems.RAW_INFERNIUM,
            ModItems.UNSTABLE_INFERNIUM
    );
    private static Set<Item> resolvedInferniumHazardousItems = null;
    private static Set<Item> getResolvedInferniumHazardousItems() {
        if (resolvedInferniumHazardousItems == null) {
            resolvedInferniumHazardousItems = INFERNIUM_HAZARDOUS_ITEMS_ROBJS.stream()
                    .map(RegistryObject::get).collect(Collectors.toSet());
        }
        return resolvedInferniumHazardousItems;
    }
    private static final int INFERNIUM_INVENTORY_ABLAZE_SECONDS = 2;
    private static final int INFERNIUM_INVENTORY_CHECK_INTERVAL = 30;


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
            Level level = player.level();

            // Check equipped charms once per tick
            boolean hasFrostEbber = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.FROST_EBBER.get()).isPresent();
            boolean hasObsidianStake = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.OBSIDIAN_STAKE.get()).isPresent();

            // --- Proximity Effects from Ore Blocks (runs periodically) ---
            if (player.tickCount % 20 == 0) { // Combined check interval for proximity effects
                // Crython Ore Proximity Slowness
                if (!hasFrostEbber) {
                    boolean nearCrythonOre = checkNearbyBlocks(player, level,
                            Set.of(ModBlocks.CRYTHON_ORE.get(), ModBlocks.DEEPSLATE_CRYTHON_ORE.get()),
                            CRYTHON_PROXIMITY_SLOWNESS_RADIUS);
                    if (nearCrythonOre) {
                        MobEffectInstance existingSlowness = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
                        if (existingSlowness == null ||
                                (existingSlowness.getDuration() < CRYTHON_PROXIMITY_SLOWNESS_DURATION - 5 &&
                                        existingSlowness.getAmplifier() <= CRYTHON_PROXIMITY_SLOWNESS_AMPLIFIER)) {
                            player.addEffect(new MobEffectInstance(
                                    MobEffects.MOVEMENT_SLOWDOWN,
                                    CRYTHON_PROXIMITY_SLOWNESS_DURATION,
                                    CRYTHON_PROXIMITY_SLOWNESS_AMPLIFIER,
                                    false, true, true
                            ));
                        }
                    }
                }

                // Infernium Ore Proximity Ablaze
                // For simplicity here, using the same 20-tick interval as Crython proximity.
                if (!hasObsidianStake && !player.isCreative() && !player.isSpectator() && !player.fireImmune()) {
                    boolean nearInferniumOre = checkNearbyBlocks(player, level,
                            Set.of(ModBlocks.INFERNIUM_ORE.get()),
                            INFERNIUM_PROXIMITY_ABLAZE_RADIUS);
                    if (nearInferniumOre) {
                        player.setSecondsOnFire(INFERNIUM_PROXIMITY_ABLAZE_SECONDS);
                    }
                }
            }

            // --- Inventory Item Effects ---
            // Crython Inventory Slowness (runs every tick for responsiveness)
            if (!hasFrostEbber && checkInventoryForItems(player, getResolvedCrythonHazardousItems())) {
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        CRYTHON_INVENTORY_SLOWNESS_DURATION,
                        CRYTHON_INVENTORY_SLOWNESS_AMPLIFIER,
                        true, false, true
                ));
            }

            // Infernium Inventory Ablaze (runs periodically)
            if (player.tickCount % INFERNIUM_INVENTORY_CHECK_INTERVAL == 0) {
                if (!hasObsidianStake && !player.isCreative() && !player.isSpectator() && !player.fireImmune()) {
                    if (checkInventoryForItems(player, getResolvedInferniumHazardousItems())) {
                        player.setSecondsOnFire(INFERNIUM_INVENTORY_ABLAZE_SECONDS);
                    }
                }
            }
        }
    }

    // Helper method to check for specific blocks around a player
    private static boolean checkNearbyBlocks(Player player, Level level, Set<Block> targetBlocks, int radius) {
        BlockPos playerPos = player.blockPosition();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    // OPTIMIZATION: Check if the offset position is within distance SQUARED to avoid sqrt
                    if (playerPos.offset(x, y, z).distSqr(playerPos) <= radius * radius) {
                        if (targetBlocks.contains(level.getBlockState(playerPos.offset(x, y, z)).getBlock())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // Helper method to check player's inventory for a set of items
    private static boolean checkInventoryForItems(Player player, Set<Item> itemsToCheck) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack stack = inventory.getItem(i);
            if (itemsToCheck.contains(stack.getItem())) {
                return true;
            }
        }
        return false;
    }
}