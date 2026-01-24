package com.gmail.vincent031525.extractionshooter.util

import com.gmail.vincent031525.extractionshooter.datamap.ContainerStats
import com.gmail.vincent031525.extractionshooter.datamap.ItemSize
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import net.minecraft.core.registries.BuiltInRegistries
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
}
