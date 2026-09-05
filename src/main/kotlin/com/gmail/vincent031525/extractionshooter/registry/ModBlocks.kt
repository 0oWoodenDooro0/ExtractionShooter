package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.block.LootCrateBlock
import net.minecraft.world.level.block.SoundType
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

object ModBlocks {
    val BLOCKS: DeferredRegister.Blocks = DeferredRegister.createBlocks(Extractionshooter.ID)

    val LOOT_CRATE: DeferredBlock<LootCrateBlock> = BLOCKS.registerBlock("loot_crate") { properties ->
        LootCrateBlock(properties.strength(2.0f).sound(SoundType.WOOD).noOcclusion())
    }
}
