package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.block.entity.LootCrateBlockEntity
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister

object ModBlockEntities {
    val BLOCK_ENTITIES: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Extractionshooter.ID)

    val LOOT_CRATE = BLOCK_ENTITIES.register("loot_crate") { ->
        BlockEntityType(
            ::LootCrateBlockEntity,
            ModBlocks.LOOT_CRATE.get()
        )
    }
}
