package com.gmail.vincent031525.extractionshooter.util

import com.gmail.vincent031525.extractionshooter.datamap.ContainerStats
import com.gmail.vincent031525.extractionshooter.datamap.ItemRarity
import com.gmail.vincent031525.extractionshooter.datamap.ItemSize
import com.gmail.vincent031525.extractionshooter.datamap.LootStats
import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import com.gmail.vincent031525.extractionshooter.inventory.GridItemInstance
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.NbtOps
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity

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
        val items = if (stack.isEmpty) emptyList() else listOf(GridItemInstance(stack, 0, 0, false))
        return GridInventory(cols, rows, items, filter = gridName, singleItem = true)
    }

    fun syncHotbarSlot(player: ServerPlayer, slot: Int) {
        val stack = player.inventory.getItem(slot)
        player.connection.send(ClientboundContainerSetSlotPacket(0, player.inventoryMenu.stateId, 36 + slot, stack))
        player.connection.send(ClientboundContainerSetSlotPacket(-2, 0, slot, stack))
    }

    fun containerToGrid(container: Container, blockEntity: BlockEntity?): GridInventory {
        if (blockEntity != null && blockEntity.persistentData.contains("GridInventory")) {
            val optTag = blockEntity.persistentData.getCompound("GridInventory")
            if (optTag.isPresent) {
                val result = GridInventory.CODEC.parse(NbtOps.INSTANCE, optTag.get())
                if (result.isSuccess) {
                    val grid = result.orThrow
                    if (isGridConsistentWithContainer(grid, container)) {
                        return grid
                    }
                }
            }
        }

        val size = container.containerSize
        val cols = 9
        val rows = maxOf(3, (size + cols - 1) / cols)
        var grid = GridInventory(cols, rows)

        for (i in 0 until size) {
            val stack = container.getItem(i)
            if (!stack.isEmpty) {
                val col = i % cols
                val row = i / cols
                if (grid.canPlace(stack, col, row, false)) {
                    grid = grid.addItem(stack, col, row, false) ?: grid
                } else {
                    val space = grid.findSpaceForItem(stack)
                    if (space != null) {
                        grid = grid.addItem(stack, space.first, space.second, false) ?: grid
                    }
                }
            }
        }
        return grid
    }

    private fun isGridConsistentWithContainer(grid: GridInventory, container: Container): Boolean {
        var containerItemCount = 0
        for (i in 0 until container.containerSize) {
            if (!container.getItem(i).isEmpty) containerItemCount++
        }
        return containerItemCount == grid.items.size
    }

    fun gridToContainer(grid: GridInventory, container: Container, blockEntity: BlockEntity?) {
        container.clearContent()
        var slot = 0
        for (item in grid.items) {
            if (slot < container.containerSize) {
                container.setItem(slot, item.stack.copy())
                slot++
            }
        }
        container.setChanged()

        if (blockEntity != null) {
            val tagResult = GridInventory.CODEC.encodeStart(NbtOps.INSTANCE, grid)
            if (tagResult.isSuccess) {
                blockEntity.persistentData.put("GridInventory", tagResult.orThrow)
                blockEntity.setChanged()
            }
        }
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

    /**
     * Gets loot stats (rarity and value) of an item.
     */
    fun getLootStats(item: Item): LootStats? {
        val holder = BuiltInRegistries.ITEM.wrapAsHolder(item)
        return holder.getData(ModDataMaps.LOOT_STATS)
    }

    fun getLootStats(stack: ItemStack): LootStats? {
        if (stack.isEmpty) return null
        return getLootStats(stack.item)
    }

    /**
     * Gets rarity of an item, defaulting to COMMON.
     */
    fun getItemRarity(stack: ItemStack): ItemRarity {
        if (stack.isEmpty) return ItemRarity.COMMON
        return getLootStats(stack)?.rarity ?: ItemRarity.COMMON
    }

    /**
     * Gets background color in inventory/container grid based on rarity.
     */
    fun getItemBackgroundColor(stack: ItemStack): Int {
        if (stack.isEmpty) return ItemRarity.COMMON.backgroundColor
        return getItemRarity(stack).backgroundColor
    }
}
