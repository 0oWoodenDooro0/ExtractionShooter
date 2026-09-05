package com.gmail.vincent031525.extractionshooter.inventory

import com.gmail.vincent031525.extractionshooter.menu.GridInventoryMenu
import com.gmail.vincent031525.extractionshooter.registry.ModTags
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

object GridQuickMoveHelper {

    enum class EquipTarget {
        HELMET, ARMOR, RIG, BACKPACK, PRIMARY_WEAPON, PISTOL, NONE
    }

    fun getEquipTarget(stack: ItemStack): EquipTarget {
        return when {
            stack.`is`(ModTags.HELMETS) -> EquipTarget.HELMET
            stack.`is`(ModTags.ARMORS) -> EquipTarget.ARMOR
            stack.`is`(ModTags.RIGS) -> EquipTarget.RIG
            stack.`is`(ModTags.BACKPACKS) -> EquipTarget.BACKPACK
            stack.`is`(ModTags.PRIMARY_WEAPONS) -> EquipTarget.PRIMARY_WEAPON
            stack.`is`(ModTags.PISTOLS) -> EquipTarget.PISTOL
            else -> EquipTarget.NONE
        }
    }

    fun isEquippedSlot(gridName: String): Boolean {
        return gridName == "helmet" || gridName == "armor" || gridName == "tactical_rig" ||
                gridName == "backpack" || gridName == "primary_1" || gridName == "primary_2" ||
                gridName == "pistol"
    }

    fun quickMove(
        player: Player,
        menu: GridInventoryMenu,
        sourceGridName: String,
        sourceX: Int,
        sourceY: Int
    ): Boolean {
        val allGrids = menu.getAllActiveGrids()

        // 1. Get source item stack and remove function
        val sourceStack: ItemStack
        val removeSourceItem: () -> Boolean

        val weaponSlot = InventoryUtils.getWeaponHotbarSlot(sourceGridName)
        if (weaponSlot != null) {
            val stack = player.inventory.getItem(weaponSlot)
            if (stack.isEmpty) return false
            sourceStack = stack.copy()
            removeSourceItem = {
                player.inventory.setItem(weaponSlot, ItemStack.EMPTY)
                if (player is ServerPlayer) {
                    InventoryUtils.syncHotbarSlot(player, weaponSlot)
                }
                true
            }
        } else {
            val sourceGrid = allGrids[sourceGridName] ?: return false
            val instance = sourceGrid.getItemInstance(sourceX, sourceY) ?: return false
            sourceStack = instance.stack.copy()
            removeSourceItem = {
                val result = sourceGrid.removeItem(sourceX, sourceY)
                if (result != null) {
                    menu.updateGrid(sourceGridName, result.first)
                    true
                } else false
            }
        }

        // 2. Case A: Source is an EQUIPPED slot -> unequip to storage
        if (isEquippedSlot(sourceGridName)) {
            val storageCandidates = mutableListOf<String>()
            if (sourceGridName != "backpack") storageCandidates.add("backpack_grid")
            if (sourceGridName != "tactical_rig") storageCandidates.add("tactical_rig_grid")
            storageCandidates.addAll(listOf("pockets_1", "pockets_2", "pockets_3", "pockets_4", "secure_container"))
            if (menu.isLooting()) storageCandidates.add("container")

            for (targetName in storageCandidates) {
                val targetGrid = menu.getAllActiveGrids()[targetName] ?: continue
                val space = targetGrid.findSpaceForItem(sourceStack) ?: continue
                val newTargetGrid = targetGrid.addItem(sourceStack, space.first, space.second, false) ?: continue

                if (removeSourceItem()) {
                    menu.updateGrid(targetName, newTargetGrid)
                    return true
                }
            }
            return false
        }

        // 3. Case B: Source is CONTAINER -> try equip first, then quick-loot to player storage
        if (sourceGridName == "container") {
            // B1: Try auto-equipping
            if (tryAutoEquip(player, menu, sourceStack, removeSourceItem)) {
                return true
            }

            // B2: Quick-loot into player storage grids
            val playerStorage = listOf(
                "tactical_rig_grid",
                "pockets_1", "pockets_2", "pockets_3", "pockets_4",
                "backpack_grid",
                "secure_container"
            )
            for (targetName in playerStorage) {
                val targetGrid = menu.getAllActiveGrids()[targetName] ?: continue
                val space = targetGrid.findSpaceForItem(sourceStack) ?: continue
                val newTargetGrid = targetGrid.addItem(sourceStack, space.first, space.second, false) ?: continue

                if (removeSourceItem()) {
                    menu.updateGrid(targetName, newTargetGrid)
                    return true
                }
            }
            return false
        }

        // 4. Case C: Source is player storage -> try auto-equip first, then stash to container if open
        if (tryAutoEquip(player, menu, sourceStack, removeSourceItem)) {
            return true
        }

        if (menu.isLooting()) {
            val containerGrid = menu.containerGrid ?: return false
            val space = containerGrid.findSpaceForItem(sourceStack) ?: return false
            val newContainerGrid = containerGrid.addItem(sourceStack, space.first, space.second, false) ?: return false

            if (removeSourceItem()) {
                menu.updateGrid("container", newContainerGrid)
                return true
            }
        }

        return false
    }

    private fun tryAutoEquip(
        player: Player,
        menu: GridInventoryMenu,
        stack: ItemStack,
        removeSourceItem: () -> Boolean
    ): Boolean {
        val allGrids = menu.getAllActiveGrids()
        when (getEquipTarget(stack)) {
            EquipTarget.HELMET -> {
                val grid = allGrids["helmet"] ?: return false
                if (grid.items.isEmpty() && grid.canPlace(stack, 0, 0, false)) {
                    val newGrid = grid.addItem(stack, 0, 0, false) ?: return false
                    if (removeSourceItem()) {
                        menu.updateGrid("helmet", newGrid)
                        return true
                    }
                }
            }
            EquipTarget.ARMOR -> {
                val grid = allGrids["armor"] ?: return false
                if (grid.items.isEmpty() && grid.canPlace(stack, 0, 0, false)) {
                    val newGrid = grid.addItem(stack, 0, 0, false) ?: return false
                    if (removeSourceItem()) {
                        menu.updateGrid("armor", newGrid)
                        return true
                    }
                }
            }
            EquipTarget.RIG -> {
                val grid = allGrids["tactical_rig"] ?: return false
                if (grid.items.isEmpty() && grid.canPlace(stack, 0, 0, false)) {
                    val newGrid = grid.addItem(stack, 0, 0, false) ?: return false
                    if (removeSourceItem()) {
                        menu.updateGrid("tactical_rig", newGrid)
                        return true
                    }
                }
            }
            EquipTarget.BACKPACK -> {
                val grid = allGrids["backpack"] ?: return false
                if (grid.items.isEmpty() && grid.canPlace(stack, 0, 0, false)) {
                    val newGrid = grid.addItem(stack, 0, 0, false) ?: return false
                    if (removeSourceItem()) {
                        menu.updateGrid("backpack", newGrid)
                        return true
                    }
                }
            }
            EquipTarget.PRIMARY_WEAPON -> {
                val p1 = player.inventory.getItem(InventoryUtils.PRIMARY_1_SLOT)
                val p2 = player.inventory.getItem(InventoryUtils.PRIMARY_2_SLOT)
                val targetSlot = when {
                    p1.isEmpty -> InventoryUtils.PRIMARY_1_SLOT
                    p2.isEmpty -> InventoryUtils.PRIMARY_2_SLOT
                    else -> null
                }
                if (targetSlot != null) {
                    if (removeSourceItem()) {
                        player.inventory.setItem(targetSlot, stack)
                        if (player is ServerPlayer) {
                            InventoryUtils.syncHotbarSlot(player, targetSlot)
                        }
                        return true
                    }
                }
            }
            EquipTarget.PISTOL -> {
                val p = player.inventory.getItem(InventoryUtils.PISTOL_SLOT)
                if (p.isEmpty) {
                    if (removeSourceItem()) {
                        player.inventory.setItem(InventoryUtils.PISTOL_SLOT, stack)
                        if (player is ServerPlayer) {
                            InventoryUtils.syncHotbarSlot(player, InventoryUtils.PISTOL_SLOT)
                        }
                        return true
                    }
                }
            }
            EquipTarget.NONE -> return false
        }
        return false
    }
}
