package com.gmail.vincent031525.extractionshooter.inventory

import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.entity.player.Player
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
                    persistentGrids.putAll(grids.filterKeys { !InventoryUtils.isWeaponGrid(it) })
                }
            }
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PlayerEquipment> = StreamCodec.composite(
            ByteBufCodecs.map({ mutableMapOf() }, ByteBufCodecs.STRING_UTF8, GridInventory.STREAM_CODEC),
            { it.persistentGrids },
            { grids ->
                PlayerEquipment().apply {
                    persistentGrids.putAll(grids.filterKeys { !InventoryUtils.isWeaponGrid(it) })
                }
            }
        )
    }

    init {
        persistentGrids["helmet"] = GridInventory(2, 2, filter = "helmet", singleItem = true)
        persistentGrids["armor"] = GridInventory(2, 2, filter = "armor", singleItem = true)
        persistentGrids["tactical_rig"] = GridInventory(2, 2, filter = "tactical_rig", singleItem = true)
        persistentGrids["backpack"] = GridInventory(2, 2, filter = "backpack", singleItem = true)

        persistentGrids["pockets_1"] = GridInventory(1, 1)
        persistentGrids["pockets_2"] = GridInventory(1, 1)
        persistentGrids["pockets_3"] = GridInventory(1, 1)
        persistentGrids["pockets_4"] = GridInventory(1, 1)
        persistentGrids["secure_container"] = GridInventory(3, 3)
    }

    fun getAllActiveGrids(player: Player? = null): Map<String, GridInventory> {
        val all = mutableMapOf<String, GridInventory>()

        all.putAll(persistentGrids)

        listOf("helmet", "armor", "tactical_rig", "backpack").forEach { slotName ->
            val grid = persistentGrids[slotName]
            val stack = grid?.getItemInstance(0, 0)?.stack
            if (stack != null && !stack.isEmpty) {
                getGridFromItem(stack)?.let { all["${slotName}_grid"] = it }
            }
        }

        if (player != null) {
            val p1 = player.inventory.getItem(InventoryUtils.PRIMARY_1_SLOT)
            val p2 = player.inventory.getItem(InventoryUtils.PRIMARY_2_SLOT)
            val pistol = player.inventory.getItem(InventoryUtils.PISTOL_SLOT)

            all["primary_1"] = InventoryUtils.createWeaponGrid("primary_1", p1)
            all["primary_2"] = InventoryUtils.createWeaponGrid("primary_2", p2)
            all["pistol"] = InventoryUtils.createWeaponGrid("pistol", pistol)

            if (!p1.isEmpty) getGridFromItem(p1)?.let { all["primary_1_grid"] = it }
            if (!p2.isEmpty) getGridFromItem(p2)?.let { all["primary_2_grid"] = it }
            if (!pistol.isEmpty) getGridFromItem(pistol)?.let { all["pistol_grid"] = it }
        }

        return all
    }

    fun updateGrid(name: String, newGrid: GridInventory, player: Player? = null) {
        if (persistentGrids.containsKey(name)) {
            persistentGrids[name] = newGrid
        } else if (InventoryUtils.isWeaponGrid(name) && player != null) {
            val slot = InventoryUtils.getWeaponHotbarSlot(name) ?: return
            val stack = newGrid.getItemInstance(0, 0)?.stack ?: ItemStack.EMPTY
            player.inventory.setItem(slot, stack)
        } else if (name.endsWith("_grid")) {
            val slotName = name.removeSuffix("_grid")
            val stack = if (InventoryUtils.isWeaponGrid(slotName) && player != null) {
                val slot = InventoryUtils.getWeaponHotbarSlot(slotName) ?: return
                player.inventory.getItem(slot)
            } else {
                persistentGrids[slotName]?.getItemInstance(0, 0)?.stack
            }
            if (stack != null && !stack.isEmpty) {
                stack.set(ModDataComponents.GRID_INVENTORY, newGrid)
            }
        }
    }

    private fun getGridFromItem(stack: ItemStack): GridInventory? {
        return stack.get(ModDataComponents.GRID_INVENTORY)
    }
}
