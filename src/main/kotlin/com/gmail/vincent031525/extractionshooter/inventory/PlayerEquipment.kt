package com.gmail.vincent031525.extractionshooter.inventory

import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack

class PlayerEquipment {

    val persistentGrids = mutableMapOf<String, GridInventory>()

    companion object {
        val CODEC: MapCodec<PlayerEquipment> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.unboundedMap(Codec.STRING, GridInventory.CODEC).fieldOf("persistentGrids")
                    .forGetter { it.persistentGrids }
            ).apply(instance) { grids ->
                PlayerEquipment().apply {
                    persistentGrids.putAll(grids)
                }
            }
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PlayerEquipment> = StreamCodec.composite(
            ByteBufCodecs.map({ mutableMapOf() }, ByteBufCodecs.STRING_UTF8, GridInventory.STREAM_CODEC),
            { it.persistentGrids },
            { grids ->
                PlayerEquipment().apply {
                    persistentGrids.putAll(grids)
                }
            }
        )
    }

    init {
        listOf("helmet", "armor", "tactical_rig", "backpack", "primary_1", "primary_2", "pistol").forEach {
            persistentGrids[it] = GridInventory(1, 1, filter = it)
        }

        persistentGrids["pockets_1"] = GridInventory(1, 1)
        persistentGrids["pockets_2"] = GridInventory(1, 1)
        persistentGrids["pockets_3"] = GridInventory(1, 1)
        persistentGrids["pockets_4"] = GridInventory(1, 1)
        persistentGrids["secure_container"] = GridInventory(3, 3)
    }

    fun getAllActiveGrids(): Map<String, GridInventory> {
        val all = mutableMapOf<String, GridInventory>()

        all.putAll(persistentGrids)

        listOf("helmet", "armor", "tactical_rig", "backpack", "primary_1", "primary_2", "pistol").forEach { slotName ->
            val grid = persistentGrids[slotName]
            val stack = grid?.getItemInstance(0, 0)?.stack
            if (stack != null && !stack.isEmpty) {
                getGridFromItem(stack)?.let { all["${slotName}_grid"] = it }
            }
        }

        return all
    }

    fun updateGrid(name: String, newGrid: GridInventory) {
        if (persistentGrids.containsKey(name)) {
            persistentGrids[name] = newGrid
        } else if (name.endsWith("_grid")) {
            val slotName = name.removeSuffix("_grid")
            val grid = persistentGrids[slotName]
            val stack = grid?.getItemInstance(0, 0)?.stack
            if (stack != null && !stack.isEmpty) {
                stack.set(ModDataComponents.GRID_INVENTORY, newGrid)
            }
        }
    }

    private fun getGridFromItem(stack: ItemStack): GridInventory? {
        return stack.get(ModDataComponents.GRID_INVENTORY)
    }
}
