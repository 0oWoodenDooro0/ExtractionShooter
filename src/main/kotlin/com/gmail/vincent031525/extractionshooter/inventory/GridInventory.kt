package com.gmail.vincent031525.extractionshooter.inventory

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack

class GridInventory(val columns: Int, val rows: Int) {
    private val items = mutableListOf<GridItemInstance>()

    companion object {
        val CODEC: Codec<GridInventory> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("columns").forGetter(GridInventory::columns),
                Codec.INT.fieldOf("rows").forGetter(GridInventory::rows),
                GridItemInstance.CODEC.listOf().fieldOf("items").forGetter { it.items }
            ).apply(instance) { cols, rws, itemsList ->
                val inv = GridInventory(cols, rws)
                inv.items.addAll(itemsList)
                inv
            }
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, GridInventory> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, { it.columns },
            ByteBufCodecs.VAR_INT, { it.rows },
            GridItemInstance.STREAM_CODEC.apply(ByteBufCodecs.list()), { it.items },
            { cols, rws, itemsList ->
                val inv = GridInventory(cols, rws)
                inv.items.addAll(itemsList)
                inv
            }
        )
    }

    fun canPlace(itemStack: ItemStack, targetX: Int, targetY: Int, rotated: Boolean): Boolean {
        val tempInstance = GridItemInstance(itemStack, targetX, targetY, rotated)
        val size = tempInstance.actualSize

        if (targetX < 0 || targetY < 0 || targetX + size.width > columns || targetY + size.height > rows) {
            return false
        }

        for (item in items) {
            if (isOverlapping(tempInstance, item)) return false
        }
        return true
    }

    fun addItem(itemStack: ItemStack, targetX: Int, targetY: Int, rotated: Boolean): Boolean {
        if (canPlace(itemStack, targetX, targetY, rotated)) {
            items.add(GridItemInstance(itemStack, targetX, targetY, rotated))
            return true
        }
        return false
    }

    fun removeItem(targetX: Int, targetY: Int): ItemStack? {
        val iterator = items.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.occupies(targetX, targetY)) {
                iterator.remove()
                return item.stack
            }
        }
        return null
    }

    fun findSpaceForItem(itemStack: ItemStack): Pair<Int, Int>? {
        for (rot in listOf(false, true)) {
            for (y in 0 until rows) {
                for (x in 0 until columns) {
                    if (canPlace(itemStack, x, y, rot)) return Pair(x, y)
                }
            }
        }
        return null
    }

    private fun isOverlapping(a: GridItemInstance, b: GridItemInstance): Boolean {
        val sizeA = a.actualSize
        val sizeB = b.actualSize
        return a.x < b.x + sizeB.width &&
                a.x + sizeA.width > b.x &&
                a.y < b.y + sizeB.height &&
                a.y + sizeA.height > b.y
    }

    fun getItems(): List<GridItemInstance> = items
}
