package com.gmail.vincent031525.extractionshooter.inventory

import com.gmail.vincent031525.extractionshooter.datamap.ItemSize
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
import com.gmail.vincent031525.extractionshooter.util.EquipmentValidator
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import java.util.*

data class GridInventory(
    val columns: Int,
    val rows: Int,
    val items: List<GridItemInstance> = emptyList(),
    val filter: String? = null,
    val singleItem: Boolean = false
) {
    @Transient
    var sizeProvider: (ItemStack) -> ItemSize = { InventoryUtils.getItemSize(it) }

    companion object {
        val CODEC: Codec<GridInventory> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("columns").forGetter(GridInventory::columns),
                Codec.INT.fieldOf("rows").forGetter(GridInventory::rows),
                GridItemInstance.CODEC.listOf().fieldOf("items").forGetter(GridInventory::items),
                Codec.STRING.optionalFieldOf("filter").forGetter { Optional.ofNullable(it.filter) },
                Codec.BOOL.optionalFieldOf("singleItem", false).forGetter(GridInventory::singleItem)
            ).apply(instance) { c, r, i, f, s -> GridInventory(c, r, i, f.orElse(null), s) }
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, GridInventory> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, { it.columns },
            ByteBufCodecs.VAR_INT, { it.rows },
            ByteBufCodecs.collection({ java.util.ArrayList() }, GridItemInstance.STREAM_CODEC), { it.items },
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), { Optional.ofNullable(it.filter) },
            ByteBufCodecs.BOOL, { it.singleItem },
            { c, r, i, f, s -> GridInventory(c, r, i, f.orElse(null), s) }
        )
    }

    fun canPlace(itemStack: ItemStack, targetX: Int, targetY: Int, rotated: Boolean): Boolean {
        if (!isValidForItem(itemStack)) return false
        if (singleItem && items.isNotEmpty()) return false

        val tempInstance = GridItemInstance(itemStack, targetX, targetY, rotated)
        val size = if (singleItem) ItemSize(1, 1) else tempInstance.getActualSize(sizeProvider)

        if (targetX < 0 || targetY < 0 || targetX + size.width > columns || targetY + size.height > rows) {
            return false
        }

        if (!singleItem) {
            for (item in items) {
                if (isOverlapping(tempInstance, item)) return false
            }
        }
        return true
    }

    fun isValidForItem(itemStack: ItemStack): Boolean {
        if (filter == null) return true
        return EquipmentValidator.isValid(filter, itemStack)
    }

    fun addItem(itemStack: ItemStack, targetX: Int, targetY: Int, rotated: Boolean): GridInventory? {
        val actualX = if (singleItem) 0 else targetX
        val actualY = if (singleItem) 0 else targetY
        if (canPlace(itemStack, actualX, actualY, rotated)) {
            val newItems = items.toMutableList()
            newItems.add(GridItemInstance(itemStack, actualX, actualY, rotated))
            return copy(items = newItems)
        }
        return null
    }

    fun removeItem(targetX: Int, targetY: Int): Pair<GridInventory, ItemStack>? {
        val itemToRemove = if (singleItem) items.firstOrNull() else items.firstOrNull { it.occupies(targetX, targetY, sizeProvider) }
        
        if (itemToRemove != null) {
            val newItems = items.toMutableList()
            newItems.remove(itemToRemove)
            return Pair(copy(items = newItems), itemToRemove.stack)
        }
        return null
    }

    fun findSpaceForItem(itemStack: ItemStack): Pair<Int, Int>? {
        if (singleItem) {
            return if (canPlace(itemStack, 0, 0, false)) Pair(0, 0) else null
        }
        for (rot in listOf(false, true)) {
            for (y in 0 until rows) {
                for (x in 0 until columns) {
                    if (canPlace(itemStack, x, y, rot)) return Pair(x, y)
                }
            }
        }
        return null
    }

    fun getItemInstance(x: Int, y: Int): GridItemInstance? {
        if (singleItem) return items.firstOrNull()
        return items.firstOrNull { it.occupies(x, y, sizeProvider) }
    }

    private fun isOverlapping(a: GridItemInstance, b: GridItemInstance): Boolean {
        val sizeA = a.getActualSize(sizeProvider)
        val sizeB = b.getActualSize(sizeProvider)
        return a.x < b.x + sizeB.width &&
                a.x + sizeA.width > b.x &&
                a.y < b.y + sizeB.height &&
                a.y + sizeA.height > b.y
    }
}