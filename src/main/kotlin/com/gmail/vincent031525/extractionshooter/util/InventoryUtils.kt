package com.gmail.vincent031525.extractionshooter.util

import com.gmail.vincent031525.extractionshooter.datamap.ContainerStats
import com.gmail.vincent031525.extractionshooter.datamap.ItemSize
import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import com.gmail.vincent031525.extractionshooter.inventory.GridItemInstance
import com.gmail.vincent031525.extractionshooter.inventory.PlayerEquipment
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

object InventoryUtils {
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

    /**
     * Syncs the primary_1, primary_2, and pistol grids from PlayerEquipment into
     * hotbar slot 0 (key 1), slot 1 (key 2), and slot 2 (key 3).
     */
    fun syncHotbarWithEquipment(player: Player, equipment: PlayerEquipment) {
        val p1 = equipment.persistentGrids["primary_1"]?.getItemInstance(0, 0)?.stack ?: ItemStack.EMPTY
        val p2 = equipment.persistentGrids["primary_2"]?.getItemInstance(0, 0)?.stack ?: ItemStack.EMPTY
        val pistol = equipment.persistentGrids["pistol"]?.getItemInstance(0, 0)?.stack ?: ItemStack.EMPTY

        player.inventory.setItem(0, p1.copy())
        player.inventory.setItem(1, p2.copy())
        player.inventory.setItem(2, pistol.copy())

        if (player is ServerPlayer) {
            player.containerMenu.broadcastChanges()
        }
    }

    /**
     * If the player is currently holding hotbar slot 0, 1, or 2, syncs the held item back
     * into primary_1, primary_2, or pistol in PlayerEquipment.
     */
    fun syncWeaponFromHotbarToEquipment(player: Player, equipment: PlayerEquipment) {
        val slotName = when (player.inventory.selectedSlot) {
            0 -> "primary_1"
            1 -> "primary_2"
            2 -> "pistol"
            else -> return
        }

        val stack = player.mainHandItem
        val grid = equipment.persistentGrids[slotName] ?: return
        val newGrid = if (stack.isEmpty) {
            grid.removeItem(0, 0)?.first ?: grid
        } else {
            grid.replaceItem(0, 0, stack.copy())
                ?: GridInventory(
                    grid.columns, grid.rows,
                    listOf(GridItemInstance(stack.copy(), 0, 0)),
                    grid.filter, grid.singleItem
                )
        }
        equipment.updateGrid(slotName, newGrid)
    }
}
