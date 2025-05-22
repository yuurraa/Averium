// src/main/java/net/yuurraa/averiummod/block/entity/ModBlockEntities.java
package net.yuurraa.averiummod.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yuurraa.averiummod.AveriumMod;
import net.yuurraa.averiummod.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AveriumMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<ArgonVentBlockEntity>> ARGON_VENT =
            BLOCK_ENTITIES.register("argon_vent",
                    () -> BlockEntityType.Builder.of(
                                    ArgonVentBlockEntity::new,
                                    ModBlocks.ARGON_VENT.get()
                            )
                            .build(null)
            );
    public static final RegistryObject<BlockEntityType<XenonVentBlockEntity>> XENON_VENT =
            BLOCK_ENTITIES.register("xenon_vent",
                    () -> BlockEntityType.Builder.of(
                                    XenonVentBlockEntity::new,
                                    ModBlocks.XENON_VENT.get()
                            )
                            .build(null)
            );

    // Add registration for InertInfuserBlockEntity
    public static final RegistryObject<BlockEntityType<InertInfuserBlockEntity>> INERT_INFUSER =
            BLOCK_ENTITIES.register("inert_infuser_be", () ->
                    BlockEntityType.Builder.of(InertInfuserBlockEntity::new,
                            ModBlocks.INERT_INFUSER.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}