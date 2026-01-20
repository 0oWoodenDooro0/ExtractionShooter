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

    val equipmentSlots = mutableMapOf<String, ItemStack>()

    val persistentGrids = mutableMapOf<String, GridInventory>()

    companion object {
        val CODEC: MapCodec<PlayerEquipment> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.unboundedMap(Codec.STRING, ItemStack.CODEC).fieldOf("equipmentSlots")
                    .forGetter { it.equipmentSlots },
                Codec.unboundedMap(Codec.STRING, GridInventory.CODEC).fieldOf("persistentGrids")
                    .forGetter { it.persistentGrids }
            ).apply(instance) { slots, grids ->
                PlayerEquipment().apply {
                    equipmentSlots.putAll(slots)
                    persistentGrids.putAll(grids)
                }
            }
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PlayerEquipment> = StreamCodec.composite(
            ByteBufCodecs.map({ mutableMapOf() }, ByteBufCodecs.STRING_UTF8, ItemStack.OPTIONAL_STREAM_CODEC),
            { it.equipmentSlots },
            ByteBufCodecs.map({ mutableMapOf() }, ByteBufCodecs.STRING_UTF8, GridInventory.STREAM_CODEC),
            { it.persistentGrids },
            { slots, grids ->
                PlayerEquipment().apply {
                    equipmentSlots.putAll(slots)
                    persistentGrids.putAll(grids)
                }
            }
        )
    }

    init {
        listOf("helmet", "armor", "tactical_rig", "backpack", "primary_1", "primary_2", "pistol").forEach {
            equipmentSlots[it] = ItemStack.EMPTY
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

        val rig = equipmentSlots["tactical_rig"]
        if (!rig!!.isEmpty) {
            getGridFromItem(rig)?.let { all["rig_grid"] = it }
        }

        val backpack = equipmentSlots["backpack"]
        if (!backpack!!.isEmpty) {
            getGridFromItem(backpack)?.let { all["backpack_grid"] = it }
        }

        return all
    }

    private fun getGridFromItem(stack: ItemStack): GridInventory? {
        return stack.get(ModDataComponents.GRID_INVENTORY)
    }
}
