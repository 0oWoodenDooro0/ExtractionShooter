package com.gmail.vincent031525.extractionshooter.block.entity

import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import com.gmail.vincent031525.extractionshooter.registry.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput

class LootCrateBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(ModBlockEntities.LOOT_CRATE.get(), pos, state) {

    var grid: GridInventory = GridInventory(9, 6)

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.store("GridInventory", GridInventory.CODEC, grid)
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        input.read("GridInventory", GridInventory.CODEC).ifPresent {
            grid = it
        }
    }
}
