package com.gmail.vincent031525.extractionshooter.util

import com.gmail.vincent031525.extractionshooter.datamap.ContainerStats
import com.gmail.vincent031525.extractionshooter.datamap.ItemSize
import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import com.gmail.vincent031525.extractionshooter.inventory.GridItemInstance
import com.gmail.vincent031525.extractionshooter.inventory.PlayerEquipment
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object InventoryUtils {
    const val PRIMARY_1_SLOT = 0
    const val PRIMARY_2_SLOT = 1
    const val PISTOL_SLOT = 2

    fun isWeaponGrid(gridName: String): Boolean =
        gridName == "primary_1" || gridName == "primary_2" || gridName == "pistol"

    fun getWeaponHotbarSlot(gridName: String): Int? = when (gridName) {
        "primary_1" -> PRIMARY_1_SLOT
        "primary_2" -> PRIMARY_2_SLOT
        "pistol" -> PISTOL_SLOT
        else -> null
    }

    fun getWeaponSubGridSlot(gridName: String): Int? = when (gridName) {
        "primary_1_grid" -> PRIMARY_1_SLOT
        "primary_2_grid" -> PRIMARY_2_SLOT
        "pistol_grid" -> PISTOL_SLOT
        else -> null
    }

    fun createWeaponGrid(gridName: String, stack: ItemStack): GridInventory {
        val (cols, rows) = when (gridName) {
            "primary_1", "primary_2" -> 4 to 2
            "pistol" -> 2 to 2
            else -> 1 to 1
        }
        val items = if (!stack.isEmpty) {
            listOf(GridItemInstance(stack, 0, 0))
        } else {
            emptyList()
        }
        return GridInventory(cols, rows, items, filter = gridName, singleItem = true)
    }

    fun syncHotbarSlot(player: ServerPlayer, slot: Int) {
        val stack = player.inventory.getItem(slot)
        player.connection.send(ClientboundContainerSetSlotPacket(0, player.inventoryMenu.stateId, 36 + slot, stack))
        player.connection.send(ClientboundContainerSetSlotPacket(-2, 0, slot, stack))
    }

    /**
     * Gets the container stats (columns/rows) of an item.
     */
    fun getContainerStats(item: Item): ContainerStats? {
        val holder = BuiltInRegistries.ITEM.wrapAsHolder(item)
        return holder.getData(ModDataMaps.CONTAINER_STATS)
    }

    fun getContainerStats(stack: ItemStack): ContainerStats? {
        if (stack.isEmpty) return null
        return getContainerStats(stack.item)
    }

    /**
     * Gets the grid size of an item.
     * Defaults to 1x1 if not defined in the item_size data map.
     */
    fun getItemSize(item: Item): ItemSize {
        val holder = BuiltInRegistries.ITEM.wrapAsHolder(item)
        return holder.getData(ModDataMaps.ITEM_SIZE) ?: ItemSize.DEFAULT
    }

    /**
     * Gets the grid size of an item stack.
     */
    fun getItemSize(stack: ItemStack): ItemSize {
        if (stack.isEmpty) return ItemSize(0, 0)
        return getItemSize(stack.item)
    }
}
