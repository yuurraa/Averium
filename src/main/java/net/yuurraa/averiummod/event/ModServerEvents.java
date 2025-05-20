// src/main/java/net/yuurraa/averiummod/event/ModServerEvents.java
package net.yuurraa.averiummod.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

    // Proximity Slowness constants
    private static final int PROXIMITY_SLOWNESS_CHECK_RADIUS = 4;
    private static final int PROXIMITY_SLOWNESS_EFFECT_DURATION = 60;
    private static final int PROXIMITY_SLOWNESS_AMPLIFIER = 0;

    // Items that cause slowness if in inventory
    private static final Set<RegistryObject<Item>> INVENTORY_SLOWNESS_ITEM_ROBJS = Set.of(
            ModItems.RAW_CRYTHON,
            ModItems.UNSTABLE_CRYTHON
    );
    private static Set<Item> resolvedInventorySlownessItems = null;

    private static Set<Item> getResolvedInventorySlownessItems() {
        if (resolvedInventorySlownessItems == null) {
            resolvedInventorySlownessItems = INVENTORY_SLOWNESS_ITEM_ROBJS.stream()
                    .map(RegistryObject::get)
                    .collect(Collectors.toSet());
        }
        return resolvedInventorySlownessItems;
    }

    private static final int INVENTORY_SLOWNESS_EFFECT_DURATION = 10;
    private static final int INVENTORY_SLOWNESS_AMPLIFIER = 0;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer player) {
            Level level = player.level();
            boolean appliedProximitySlownessThisTick = false; // Keep this if you still want proximity logic

            // --- Proximity Slowness for Crython Ore Blocks (runs every 20 ticks) ---
            if (player.tickCount % 20 == 0) {
                // ... (your existing proximity slowness logic remains here) ...
                BlockPos playerPos = player.blockPosition();
                boolean nearCrythonOre = false;
                for (int x = -PROXIMITY_SLOWNESS_CHECK_RADIUS; x <= PROXIMITY_SLOWNESS_CHECK_RADIUS; x++) {
                    for (int y = -PROXIMITY_SLOWNESS_CHECK_RADIUS; y <= PROXIMITY_SLOWNESS_CHECK_RADIUS; y++) {
                        for (int z = -PROXIMITY_SLOWNESS_CHECK_RADIUS; z <= PROXIMITY_SLOWNESS_CHECK_RADIUS; z++) {
                            BlockState blockState = level.getBlockState(playerPos.offset(x, y, z));
                            if (blockState.is(ModBlocks.CRYTHON_ORE.get()) ||
                                    blockState.is(ModBlocks.DEEPSLATE_CRYTHON_ORE.get())) {
                                nearCrythonOre = true; break;
                            }
                        } if (nearCrythonOre) break;
                    } if (nearCrythonOre) break;
                }

                if (nearCrythonOre) {
                    MobEffectInstance existingSlowness = player.getEffect(MobEffects.MOVEMENT_SLOWDOWN);
                    if (existingSlowness == null ||
                            (existingSlowness.getDuration() < PROXIMITY_SLOWNESS_EFFECT_DURATION - 5 &&
                                    existingSlowness.getAmplifier() <= PROXIMITY_SLOWNESS_AMPLIFIER)) {
                        player.addEffect(new MobEffectInstance(
                                MobEffects.MOVEMENT_SLOWDOWN,
                                PROXIMITY_SLOWNESS_EFFECT_DURATION,
                                PROXIMITY_SLOWNESS_AMPLIFIER,
                                false, true, true
                        ));
                        // appliedProximitySlownessThisTick = true; // Not strictly needed with how effects merge
                    }
                }
            }

            // --- Slowness for Carrying Raw/Unstable Crython (unless Inert Charm is equipped) ---
            boolean hasInertCharmEquipped = CuriosApi.getCuriosHelper()
                    .findFirstCurio(player, ModItems.INERT_CHARM.get())
                    .isPresent();

            boolean carryingSlowingCrython = false;
            if (!hasInertCharmEquipped) { // Only check for slowing Crython if charm is NOT equipped
                Set<Item> itemsThatSlow = getResolvedInventorySlownessItems();
                // Check main inventory + offhand. Armor slots usually don't hold raw materials.
                for (int i = 0; i < player.getInventory().items.size(); ++i) { // Main inventory slots (0-35)
                    ItemStack stack = player.getInventory().items.get(i);
                    if (itemsThatSlow.contains(stack.getItem())) {
                        carryingSlowingCrython = true;
                        break;
                    }
                }
                if (!carryingSlowingCrython) {
                    ItemStack offHandStack = player.getInventory().offhand.get(0);
                    if (itemsThatSlow.contains(offHandStack.getItem())) {
                        carryingSlowingCrython = true;
                    }
                }
                // Note: player.getInventory().getContainerSize() includes armor and offhand.
                // A more thorough check could use that loop as in the previous "satchel" version,
                // but typically raw materials aren't in armor slots.
            }

            if (carryingSlowingCrython) { // This implies !hasInertCharmEquipped
                player.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        INVENTORY_SLOWNESS_EFFECT_DURATION,
                        INVENTORY_SLOWNESS_AMPLIFIER,
                        true,  // ambient
                        false, // no particles
                        true   // show icon
                ));
            }
        }
    }
}