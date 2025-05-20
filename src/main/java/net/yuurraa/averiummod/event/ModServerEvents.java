// src/main/java/net/yuurraa/averiummod/event/ModServerEvents.java
package net.yuurraa.averiummod.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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

    // --- Crython Proximity Slowness Constants ---
    private static final int CRYTHON_PROXIMITY_SLOWNESS_RADIUS = 4;
    private static final int CRYTHON_PROXIMITY_SLOWNESS_DURATION = 60;
    private static final int CRYTHON_PROXIMITY_SLOWNESS_AMPLIFIER = 0;

    // --- Crython Inventory Slowness (unless charm) ---
    private static final Set<RegistryObject<Item>> CRYTHON_INVENTORY_SLOWNESS_ROBJS = Set.of(
            ModItems.RAW_CRYTHON,
            ModItems.UNSTABLE_CRYTHON
    );
    private static Set<Item> resolvedCrythonInventorySlownessItems = null;
    private static Set<Item> getResolvedCrythonInventorySlownessItems() {
        if (resolvedCrythonInventorySlownessItems == null) {
            resolvedCrythonInventorySlownessItems = CRYTHON_INVENTORY_SLOWNESS_ROBJS.stream()
                    .map(RegistryObject::get)
                    .collect(Collectors.toSet());
        }
        return resolvedCrythonInventorySlownessItems;
    }
    private static final int CRYTHON_INVENTORY_SLOWNESS_DURATION = 10;
    private static final int CRYTHON_INVENTORY_SLOWNESS_AMPLIFIER = 0;

    // --- Infernium Proximity Ablaze Constants ---
    private static final int INFERNIUM_PROXIMITY_ABLAZE_RADIUS = 3; // Slightly smaller radius for fire
    private static final int INFERNIUM_PROXIMITY_ABLAZE_SECONDS = 3; // Set on fire for 3 seconds
    private static final int INFERNIUM_PROXIMITY_CHECK_INTERVAL = 40; // Check every 2 seconds

    // --- Infernium Inventory Ablaze (unless charm) ---
    private static final Set<RegistryObject<Item>> INFERNIUM_INVENTORY_ABLAZE_ROBJS = Set.of(
            ModItems.RAW_INFERNIUM,
            ModItems.UNSTABLE_INFERNIUM
    );
    private static Set<Item> resolvedInferniumInventoryAblazeItems = null;
    private static Set<Item> getResolvedInferniumInventoryAblazeItems() {
        if (resolvedInferniumInventoryAblazeItems == null) {
            resolvedInferniumInventoryAblazeItems = INFERNIUM_INVENTORY_ABLAZE_ROBJS.stream()
                    .map(RegistryObject::get)
                    .collect(Collectors.toSet());
        }
        return resolvedInferniumInventoryAblazeItems;
    }
    private static final int INFERNIUM_INVENTORY_ABLAZE_SECONDS = 2; // Set on fire for 2 seconds
    private static final int INFERNIUM_INVENTORY_CHECK_INTERVAL = 30; // Chance to set ablaze every 1.5 seconds


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
            Level level = player.level();

            // --- Crython Ore Proximity Slowness (runs every 20 ticks) ---
            if (player.tickCount % 20 == 0) {
                // ... (existing Crython proximity slowness logic) ...
                BlockPos playerPos = player.blockPosition();
                boolean nearCrythonOre = false;
                for (int x = -CRYTHON_PROXIMITY_SLOWNESS_RADIUS; x <= CRYTHON_PROXIMITY_SLOWNESS_RADIUS; x++) {
                    for (int y = -CRYTHON_PROXIMITY_SLOWNESS_RADIUS; y <= CRYTHON_PROXIMITY_SLOWNESS_RADIUS; y++) {
                        for (int z = -CRYTHON_PROXIMITY_SLOWNESS_RADIUS; z <= CRYTHON_PROXIMITY_SLOWNESS_RADIUS; z++) {
                            BlockState blockState = level.getBlockState(playerPos.offset(x, y, z));
                            if (blockState.is(ModBlocks.CRYTHON_ORE.get()) || blockState.is(ModBlocks.DEEPSLATE_CRYTHON_ORE.get())) {
                                nearCrythonOre = true; break;
                            }
                        } if (nearCrythonOre) break;
                    } if (nearCrythonOre) break;
                }
                if (nearCrythonOre) {
                    MobEffectInstance existingSlowness = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
                    if (existingSlowness == null || (existingSlowness.getDuration() < CRYTHON_PROXIMITY_SLOWNESS_DURATION - 5 && existingSlowness.getAmplifier() <= CRYTHON_PROXIMITY_SLOWNESS_AMPLIFIER)) {
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, CRYTHON_PROXIMITY_SLOWNESS_DURATION, CRYTHON_PROXIMITY_SLOWNESS_AMPLIFIER, false, true, true));
                    }
                }
            }

            // --- Crython Inventory Slowness (runs every tick) ---
            boolean hasInertCharmEquipped = CuriosApi.getCuriosHelper().findFirstCurio(player, ModItems.INERT_CHARM.get()).isPresent();
            boolean carryingSlowingCrython = false;
            if (!hasInertCharmEquipped) {
                Set<Item> itemsThatSlowCrython = getResolvedCrythonInventorySlownessItems();
                Inventory inventory = player.getInventory();
                for (int i = 0; i < inventory.getContainerSize(); ++i) {
                    ItemStack stack = inventory.getItem(i);
                    if (itemsThatSlowCrython.contains(stack.getItem())) {
                        carryingSlowingCrython = true;
                        break;
                    }
                }
            }
            if (carryingSlowingCrython) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, CRYTHON_INVENTORY_SLOWNESS_DURATION, CRYTHON_INVENTORY_SLOWNESS_AMPLIFIER, true, false, true));
            }


            // --- NEW: Infernium Ore Proximity Ablaze (runs periodically) ---
            if (player.tickCount % INFERNIUM_PROXIMITY_CHECK_INTERVAL == 0) {
                if (!player.isCreative() && !player.isSpectator() && !player.fireImmune()) { // Don't affect creative/spectator/fire immune
                    BlockPos playerPos = player.blockPosition();
                    boolean nearInferniumOre = false;
                    for (int x = -INFERNIUM_PROXIMITY_ABLAZE_RADIUS; x <= INFERNIUM_PROXIMITY_ABLAZE_RADIUS; x++) {
                        for (int y = -INFERNIUM_PROXIMITY_ABLAZE_RADIUS; y <= INFERNIUM_PROXIMITY_ABLAZE_RADIUS; y++) {
                            for (int z = -INFERNIUM_PROXIMITY_ABLAZE_RADIUS; z <= INFERNIUM_PROXIMITY_ABLAZE_RADIUS; z++) {
                                if (level.getBlockState(playerPos.offset(x, y, z)).is(ModBlocks.INFERNIUM_ORE.get())) {
                                    nearInferniumOre = true; break;
                                }
                            } if (nearInferniumOre) break;
                        } if (nearInferniumOre) break;
                    }

                    if (nearInferniumOre) {
                        player.setSecondsOnFire(INFERNIUM_PROXIMITY_ABLAZE_SECONDS);
                    }
                }
            }

            // --- NEW: Infernium Inventory Ablaze (runs periodically, if no charm) ---
            if (player.tickCount % INFERNIUM_INVENTORY_CHECK_INTERVAL == 0) {
                if (!player.isCreative() && !player.isSpectator() && !player.fireImmune() && !hasInertCharmEquipped) { // Check charm here too
                    Set<Item> itemsThatSetAblaze = getResolvedInferniumInventoryAblazeItems();
                    Inventory inventory = player.getInventory();
                    boolean carryingAblazeItem = false;
                    for (int i = 0; i < inventory.getContainerSize(); ++i) {
                        ItemStack stack = inventory.getItem(i);
                        if (itemsThatSetAblaze.contains(stack.getItem())) {
                            carryingAblazeItem = true;
                            break;
                        }
                    }
                    if (carryingAblazeItem) {
                        player.setSecondsOnFire(INFERNIUM_INVENTORY_ABLAZE_SECONDS);
                    }
                }
            }
        }
    }
}