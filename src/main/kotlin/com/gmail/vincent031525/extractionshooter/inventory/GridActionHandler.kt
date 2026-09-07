package com.gmail.vincent031525.extractionshooter.inventory

import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.item.AmmoItem
import com.gmail.vincent031525.extractionshooter.item.GunItem
import com.gmail.vincent031525.extractionshooter.item.MagazineItem
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level

object GridActionHandler {

    data class InteractionResult(
        val newGrid: GridInventory,
        val newCarried: ItemStack,
        val sound: SoundEvent? = null,
        val pitch: Float = 1.0f
    )

    fun canInteract(
        level: Level,
        grid: GridInventory,
        targetX: Int,
        targetY: Int,
        carried: ItemStack,
        button: Int
    ): Boolean {
        val instance = grid.getItemInstance(targetX, targetY) ?: return false
        val targetStack = instance.stack

        if (button == 1) { // Right-click
            if (carried.isEmpty) {
                if (targetStack.item is MagazineItem) {
                    val data = MagazineItem.getMagazineData(targetStack) ?: MagazineData()
                    return data.ammoCount > 0 && data.ammoItem is AmmoItem
                } else if (targetStack.item is GunItem<*>) {
                    return !GunItem.getMagazineStack(targetStack).isEmpty
                } else if (targetStack.count > 1) {
                    return true
                }
            } else {
                if (targetStack.item is MagazineItem && carried.item is AmmoItem) {
                    val data = MagazineItem.getMagazineData(targetStack) ?: MagazineData()
                    val stats = (targetStack.item as MagazineItem).getMagazineStats()
                    val spaceLeft = stats.maxAmmo - data.ammoCount
                    val isAmmoMatch = data.ammoCount == 0 || data.ammoItem == Items.AIR || data.ammoItem == carried.item
                    return isAmmoMatch && spaceLeft > 0
                } else if (targetStack.item is GunItem<*> && carried.item is MagazineItem) {
                    return GunItem.getMagazineStack(targetStack).isEmpty
                } else if (ItemStack.isSameItemSameComponents(targetStack, carried)) {
                    return targetStack.count < targetStack.maxStackSize
                }
            }
        } else if (button == 0) { // Left-click
            if (!carried.isEmpty) {
                if (targetStack.item is MagazineItem && carried.item is AmmoItem) {
                    val data = MagazineItem.getMagazineData(targetStack) ?: MagazineData()
                    val stats = (targetStack.item as MagazineItem).getMagazineStats()
                    val spaceLeft = stats.maxAmmo - data.ammoCount
                    val isAmmoMatch = data.ammoCount == 0 || data.ammoItem == Items.AIR || data.ammoItem == carried.item
                    return isAmmoMatch && spaceLeft > 0
                } else if (targetStack.item is GunItem<*> && carried.item is MagazineItem) {
                    return GunItem.getMagazineStack(targetStack).isEmpty
                } else if (ItemStack.isSameItemSameComponents(targetStack, carried)) {
                    return targetStack.count < targetStack.maxStackSize
                }
            }
        }

        return false
    }

    fun interact(
        level: Level,
        grid: GridInventory,
        targetX: Int,
        targetY: Int,
        carried: ItemStack,
        button: Int
    ): InteractionResult? {
        val instance = grid.getItemInstance(targetX, targetY) ?: return null
        val targetStack = instance.stack.copy()
        val workingCarried = carried.copy()

        if (button == 1 && workingCarried.isEmpty) {
            if (targetStack.item is MagazineItem) {
                val unloaded = MagazineItem.unloadAmmo(targetStack)
                if (!unloaded.isEmpty) {
                    val newGrid = grid.replaceItem(targetX, targetY, targetStack) ?: return null
                    return InteractionResult(newGrid, unloaded, SoundEvents.ITEM_PICKUP, 1.2f)
                }
            } else if (targetStack.item is GunItem<*>) {
                val gunItem = targetStack.item as GunItem<*>
                val unloadedMag = gunItem.unloadMagazine(targetStack)
                if (!unloadedMag.isEmpty) {
                    val newGrid = grid.replaceItem(targetX, targetY, targetStack) ?: return null
                    return InteractionResult(newGrid, unloadedMag, SoundEvents.ITEM_PICKUP, 1.2f)
                }
            } else if (targetStack.count > 1) {
                val takeAmount = (targetStack.count + 1) / 2
                val splitStack = targetStack.split(takeAmount)
                val newGrid = grid.replaceItem(targetX, targetY, targetStack) ?: return null
                return InteractionResult(newGrid, splitStack, SoundEvents.ITEM_PICKUP, 1.0f)
            }
        } else if (!workingCarried.isEmpty) {
            if (targetStack.item is MagazineItem && workingCarried.item is AmmoItem) {
                val loaded = MagazineItem.loadAmmo(targetStack, workingCarried)
                if (loaded) {
                    val newGrid = grid.replaceItem(targetX, targetY, targetStack) ?: return null
                    val newCarried = if (workingCarried.isEmpty) ItemStack.EMPTY else workingCarried
                    return InteractionResult(newGrid, newCarried, SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.5f)
                }
            } else if (targetStack.item is GunItem<*> && workingCarried.item is MagazineItem) {
                val gunItem = targetStack.item as GunItem<*>
                if (GunItem.getMagazineStack(targetStack).isEmpty) {
                    val loadedMag = gunItem.loadMagazine(level, targetStack, workingCarried)
                    if (!loadedMag.isEmpty) {
                        val newGrid = grid.replaceItem(targetX, targetY, targetStack) ?: return null
                        val newCarried = if (workingCarried.isEmpty) ItemStack.EMPTY else workingCarried
                        return InteractionResult(newGrid, newCarried, SoundEvents.ARMOR_EQUIP_GENERIC.value(), 1.0f)
                    }
                }
            } else if (ItemStack.isSameItemSameComponents(targetStack, workingCarried)) {
                if (button == 1) {
                    val spaceLeft = targetStack.maxStackSize - targetStack.count
                    if (spaceLeft > 0) {
                        targetStack.grow(1)
                        workingCarried.shrink(1)
                        val newGrid = grid.replaceItem(targetX, targetY, targetStack) ?: return null
                        val newCarried = if (workingCarried.isEmpty) ItemStack.EMPTY else workingCarried
                        return InteractionResult(newGrid, newCarried, SoundEvents.ITEM_PICKUP, 1.0f)
                    }
                } else if (button == 0) {
                    val spaceLeft = targetStack.maxStackSize - targetStack.count
                    if (spaceLeft > 0) {
                        val amountToAdd = minOf(spaceLeft, workingCarried.count)
                        targetStack.grow(amountToAdd)
                        workingCarried.shrink(amountToAdd)
                        val newGrid = grid.replaceItem(targetX, targetY, targetStack) ?: return null
                        val newCarried = if (workingCarried.isEmpty) ItemStack.EMPTY else workingCarried
                        return InteractionResult(newGrid, newCarried, SoundEvents.ITEM_PICKUP, 1.0f)
                    }
                }
            }
        }

        return null
    }
}
