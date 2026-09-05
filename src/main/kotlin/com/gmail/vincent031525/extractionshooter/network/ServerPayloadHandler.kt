package com.gmail.vincent031525.extractionshooter.network

import com.gmail.vincent031525.extractionshooter.inventory.GridActionHandler
import com.gmail.vincent031525.extractionshooter.inventory.GridQuickMoveHelper
import com.gmail.vincent031525.extractionshooter.item.GunItem
import com.gmail.vincent031525.extractionshooter.item.MagazineItem
import com.gmail.vincent031525.extractionshooter.menu.GridInventoryMenu
import com.gmail.vincent031525.extractionshooter.network.payload.*
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import com.gmail.vincent031525.extractionshooter.util.EquipmentValidator
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.common.extensions.IMenuProviderExtension
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext

object ServerPayloadHandler {

    fun handleSwitchMode(payload: SwitchModePayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val stack = player.mainHandItem

            val currentData = stack.get(ModDataComponents.GUN_DATA)
            if (currentData != null) {
                val nextFireModeIndex = GunItem.nextFireMode(stack)
                if (nextFireModeIndex == -1) return@enqueueWork
                val nextFireMode = GunItem.getFireMode(stack) ?: return@enqueueWork

                player.containerMenu.broadcastChanges()

                player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.UI_BUTTON_CLICK.value(),
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
                )

                player.displayClientMessage(
                    Component.literal("Switch to fire mode: ${nextFireMode.name}"),
                    true
                )
            }
        }
    }

    fun handleShoot(payload: ShootPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val stack = player.mainHandItem
            val item = stack.item as? GunItem<*> ?: return@enqueueWork

            val shotFired = item.tryShoot(player.level() as ServerLevel, player, stack, payload.origin, payload.direction)
            if (!shotFired) return@enqueueWork

            player.containerMenu.broadcastChanges()
        }
    }

    fun handleReload(payload: ReloadPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val stack = player.mainHandItem
            val item = stack.item as? GunItem<*> ?: return@enqueueWork
            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
            val allGrids = equipment.getAllActiveGrids(player)

            val gridSearchOrder = listOf(
                "tactical_rig_grid",
                "pockets_1", "pockets_2", "pockets_3", "pockets_4",
                "backpack_grid",
                "secure_container"
            )

            data class MagCandidate(
                val stack: ItemStack,
                val ammoCount: Int,
                val gridName: String?,
                val gridX: Int = 0,
                val gridY: Int = 0,
                val invSlot: Int = -1
            )

            val candidates = mutableListOf<MagCandidate>()

            // 1. Search in tactical rig, pockets, backpack, etc.
            for (gridName in gridSearchOrder) {
                val grid = allGrids[gridName] ?: continue
                for (instance in grid.items) {
                    if (instance.stack.item is MagazineItem) {
                        val magData = MagazineItem.getMagazineData(instance.stack)
                        val ammoCount = magData?.ammoCount ?: 0
                        candidates.add(MagCandidate(instance.stack, ammoCount, gridName, instance.x, instance.y))
                    }
                }
            }

            // 2. Search in vanilla inventory (excluding hands)
            for (i in 0 until player.inventory.containerSize) {
                if (i == player.inventory.selectedSlot) continue
                val invStack = player.inventory.getItem(i)
                if (invStack.item is MagazineItem) {
                    val magData = MagazineItem.getMagazineData(invStack)
                    val ammoCount = magData?.ammoCount ?: 0
                    candidates.add(MagCandidate(invStack, ammoCount, null, invSlot = i))
                }
            }

            if (candidates.isEmpty()) return@enqueueWork

            val chosen = candidates.maxByOrNull { it.ammoCount } ?: return@enqueueWork

            // Remove the chosen magazine from its location
            val newMagStack: ItemStack
            if (chosen.gridName != null) {
                val grid = allGrids[chosen.gridName] ?: return@enqueueWork
                val sourceStack = chosen.stack.copy()
                newMagStack = sourceStack.split(1)
                val newGrid = if (sourceStack.isEmpty) {
                    grid.removeItem(chosen.gridX, chosen.gridY)?.first
                } else {
                    grid.replaceItem(chosen.gridX, chosen.gridY, sourceStack)
                }
                if (newGrid != null) {
                    equipment.updateGrid(chosen.gridName, newGrid, player)
                }
            } else {
                val invStack = player.inventory.getItem(chosen.invSlot)
                newMagStack = invStack.split(1)
            }

            val oldMag = item.unloadMagazine(stack)
            if (!oldMag.isEmpty) {
                var stored = false
                val updatedGrids = equipment.getAllActiveGrids(player)
                for (storeName in gridSearchOrder) {
                    val storeGrid = updatedGrids[storeName] ?: continue
                    val space = storeGrid.findSpaceForItem(oldMag)
                    if (space != null) {
                        val updatedStoreGrid = storeGrid.addItem(oldMag, space.first, space.second, false)
                        if (updatedStoreGrid != null) {
                            equipment.updateGrid(storeName, updatedStoreGrid, player)
                            stored = true
                            break
                        }
                    }
                }

                if (!stored) {
                    if (!player.inventory.add(oldMag)) {
                        player.drop(oldMag, false)
                    }
                }
            }

            item.loadMagazine(player.level(), stack, newMagStack)

            player.setData(ModDataAttachments.PLAYER_EQUIPMENT, equipment)
            PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(equipment))
            player.containerMenu.broadcastChanges()

            player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.ARMOR_EQUIP_GENERIC.value(),
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )
        }
    }

    fun handleOpenInventory(payload: OpenInventoryPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)

            PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(equipment))

            player.openMenu(object : MenuProvider, IMenuProviderExtension {
                override fun getDisplayName(): Component = Component.literal("Inventory")
                override fun createMenu(id: Int, inv: Inventory, p: Player): AbstractContainerMenu {
                    return GridInventoryMenu(id, inv, equipment)
                }
                override fun writeClientSideData(menu: AbstractContainerMenu, buf: RegistryFriendlyByteBuf) {
                    buf.writeBoolean(false)
                }
            })
        }
    }

    fun handlePickFromGrid(payload: PickFromGridPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            if (!player.containerMenu.carried.isEmpty) return@enqueueWork

            val weaponSlot = InventoryUtils.getWeaponHotbarSlot(payload.gridName)
            if (weaponSlot != null) {
                val stack = player.inventory.getItem(weaponSlot)
                if (!stack.isEmpty) {
                    player.inventory.setItem(weaponSlot, ItemStack.EMPTY)
                    player.containerMenu.carried = stack
                    InventoryUtils.syncHotbarSlot(player, weaponSlot)
                    player.containerMenu.broadcastChanges()
                }
                return@enqueueWork
            }

            val weaponSubSlot = InventoryUtils.getWeaponSubGridSlot(payload.gridName)
            if (weaponSubSlot != null) {
                val weaponStack = player.inventory.getItem(weaponSubSlot)
                val grid = weaponStack.get(ModDataComponents.GRID_INVENTORY) ?: return@enqueueWork
                val result = grid.removeItem(payload.x, payload.y) ?: return@enqueueWork
                weaponStack.set(ModDataComponents.GRID_INVENTORY, result.first)
                player.containerMenu.carried = result.second
                InventoryUtils.syncHotbarSlot(player, weaponSubSlot)
                player.containerMenu.broadcastChanges()
                return@enqueueWork
            }

            if (payload.gridName == "container") {
                val menu = player.containerMenu as? GridInventoryMenu ?: return@enqueueWork
                val grid = menu.containerGrid ?: return@enqueueWork
                val result = grid.removeItem(payload.x, payload.y) ?: return@enqueueWork
                menu.updateGrid("container", result.first)
                player.containerMenu.carried = result.second
                player.containerMenu.broadcastChanges()
                PacketDistributor.sendToPlayer(player, SyncContainerPayload(result.first))
                return@enqueueWork
            }

            // Regular equipment grid
            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
            val allGrids = equipment.getAllActiveGrids(player)
            val grid = allGrids[payload.gridName] ?: return@enqueueWork

            val result = grid.removeItem(payload.x, payload.y)
            if (result != null) {
                val (newGrid, stack) = result
                equipment.updateGrid(payload.gridName, newGrid, player)
                player.containerMenu.carried = stack
                player.containerMenu.broadcastChanges()

                val syncPacket = SyncEquipmentPayload(equipment)
                PacketDistributor.sendToPlayer(player, syncPacket)
            }
        }
    }

    fun handlePlaceToGrid(
        payload: PlaceToGridPayload,
        context: IPayloadContext
    ) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val carried = player.containerMenu.carried
            if (carried.isEmpty) return@enqueueWork

            val weaponSlot = InventoryUtils.getWeaponHotbarSlot(payload.gridName)
            if (weaponSlot != null) {
                if (EquipmentValidator.isValid(payload.gridName, carried) && player.inventory.getItem(weaponSlot).isEmpty) {
                    player.inventory.setItem(weaponSlot, carried)
                    player.containerMenu.carried = ItemStack.EMPTY
                    InventoryUtils.syncHotbarSlot(player, weaponSlot)
                    player.containerMenu.broadcastChanges()
                }
                return@enqueueWork
            }

            val weaponSubSlot = InventoryUtils.getWeaponSubGridSlot(payload.gridName)
            if (weaponSubSlot != null) {
                val weaponStack = player.inventory.getItem(weaponSubSlot)
                val grid = weaponStack.get(ModDataComponents.GRID_INVENTORY) ?: return@enqueueWork
                val newGrid = grid.addItem(carried, payload.x, payload.y, payload.rotated) ?: return@enqueueWork
                weaponStack.set(ModDataComponents.GRID_INVENTORY, newGrid)
                player.containerMenu.carried = ItemStack.EMPTY
                InventoryUtils.syncHotbarSlot(player, weaponSubSlot)
                player.containerMenu.broadcastChanges()
                return@enqueueWork
            }

            if (payload.gridName == "container") {
                val menu = player.containerMenu as? GridInventoryMenu ?: return@enqueueWork
                val grid = menu.containerGrid ?: return@enqueueWork
                val newGrid = grid.addItem(carried, payload.x, payload.y, payload.rotated) ?: return@enqueueWork
                menu.updateGrid("container", newGrid)
                player.containerMenu.carried = ItemStack.EMPTY
                player.containerMenu.broadcastChanges()
                PacketDistributor.sendToPlayer(player, SyncContainerPayload(newGrid))
                return@enqueueWork
            }

            // Regular equipment grid
            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
            val allGrids = equipment.getAllActiveGrids(player)
            val grid = allGrids[payload.gridName] ?: return@enqueueWork

            val newGrid = grid.addItem(carried, payload.x, payload.y, payload.rotated)
            if (newGrid != null) {
                equipment.updateGrid(payload.gridName, newGrid, player)
                player.containerMenu.carried = ItemStack.EMPTY
                player.containerMenu.broadcastChanges()

                val syncPacket = SyncEquipmentPayload(equipment)
                PacketDistributor.sendToPlayer(player, syncPacket)
            }
        }
    }

    fun handleInteractGridItem(
        payload: InteractGridItemPayload,
        context: IPayloadContext
    ) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val carried = player.containerMenu.carried

            val weaponSlot = InventoryUtils.getWeaponHotbarSlot(payload.gridName)
            if (weaponSlot != null) {
                val weaponStack = player.inventory.getItem(weaponSlot)
                val tempGrid = InventoryUtils.createWeaponGrid(payload.gridName, weaponStack)
                val result = GridActionHandler.interact(
                    player.level(),
                    tempGrid,
                    payload.x,
                    payload.y,
                    carried,
                    payload.button
                ) ?: return@enqueueWork

                val newWeapon = result.newGrid.getItemInstance(0, 0)?.stack ?: ItemStack.EMPTY
                player.inventory.setItem(weaponSlot, newWeapon)
                player.containerMenu.carried = result.newCarried
                InventoryUtils.syncHotbarSlot(player, weaponSlot)
                player.containerMenu.broadcastChanges()
                return@enqueueWork
            }

            val weaponSubSlot = InventoryUtils.getWeaponSubGridSlot(payload.gridName)
            if (weaponSubSlot != null) {
                val weaponStack = player.inventory.getItem(weaponSubSlot)
                val grid = weaponStack.get(ModDataComponents.GRID_INVENTORY) ?: return@enqueueWork
                val result = GridActionHandler.interact(
                    player.level(),
                    grid,
                    payload.x,
                    payload.y,
                    carried,
                    payload.button
                ) ?: return@enqueueWork

                weaponStack.set(ModDataComponents.GRID_INVENTORY, result.newGrid)
                player.containerMenu.carried = result.newCarried
                InventoryUtils.syncHotbarSlot(player, weaponSubSlot)
                player.containerMenu.broadcastChanges()
                return@enqueueWork
            }

            if (payload.gridName == "container") {
                val menu = player.containerMenu as? GridInventoryMenu ?: return@enqueueWork
                val grid = menu.containerGrid ?: return@enqueueWork
                val result = GridActionHandler.interact(
                    player.level(),
                    grid,
                    payload.x,
                    payload.y,
                    carried,
                    payload.button
                ) ?: return@enqueueWork

                menu.updateGrid("container", result.newGrid)
                player.containerMenu.carried = result.newCarried
                player.containerMenu.broadcastChanges()
                PacketDistributor.sendToPlayer(player, SyncContainerPayload(result.newGrid))
                return@enqueueWork
            }

            // Regular equipment grid
            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
            val allGrids = equipment.getAllActiveGrids(player)
            val grid = allGrids[payload.gridName] ?: return@enqueueWork

            val result = GridActionHandler.interact(
                player.level(),
                grid,
                payload.x,
                payload.y,
                carried,
                payload.button
            ) ?: return@enqueueWork

            equipment.updateGrid(payload.gridName, result.newGrid, player)
            player.containerMenu.carried = result.newCarried
            player.containerMenu.broadcastChanges()

            val syncPacket = SyncEquipmentPayload(equipment)
            PacketDistributor.sendToPlayer(player, syncPacket)
        }
    }

    fun handleDropGridItem(payload: DropGridItemPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork

            val weaponSlot = InventoryUtils.getWeaponHotbarSlot(payload.gridName)
            if (weaponSlot != null) {
                val stack = player.inventory.getItem(weaponSlot)
                if (!stack.isEmpty) {
                    val toDrop = if (payload.entireStack || stack.count <= 1) {
                        player.inventory.setItem(weaponSlot, ItemStack.EMPTY)
                        stack
                    } else {
                        stack.split(1)
                    }
                    val itemEntity = player.drop(toDrop, false)
                    itemEntity?.setPickUpDelay(40)
                    InventoryUtils.syncHotbarSlot(player, weaponSlot)
                    player.containerMenu.broadcastChanges()
                }
                return@enqueueWork
            }

            val weaponSubSlot = InventoryUtils.getWeaponSubGridSlot(payload.gridName)
            if (weaponSubSlot != null) {
                val weaponStack = player.inventory.getItem(weaponSubSlot)
                val grid = weaponStack.get(ModDataComponents.GRID_INVENTORY) ?: return@enqueueWork
                val itemInstance = grid.getItemInstance(payload.x, payload.y) ?: return@enqueueWork
                val toDrop: ItemStack
                val newGrid = if (payload.entireStack || itemInstance.stack.count <= 1) {
                    val result = grid.removeItem(payload.x, payload.y) ?: return@enqueueWork
                    toDrop = result.second
                    result.first
                } else {
                    toDrop = itemInstance.stack.split(1)
                    grid.replaceItem(payload.x, payload.y, itemInstance.stack) ?: return@enqueueWork
                }
                weaponStack.set(ModDataComponents.GRID_INVENTORY, newGrid)
                val itemEntity = player.drop(toDrop, false)
                itemEntity?.setPickUpDelay(40)
                InventoryUtils.syncHotbarSlot(player, weaponSubSlot)
                player.containerMenu.broadcastChanges()
                return@enqueueWork
            }

            if (payload.gridName == "container") {
                val menu = player.containerMenu as? GridInventoryMenu ?: return@enqueueWork
                val grid = menu.containerGrid ?: return@enqueueWork
                val itemInstance = grid.getItemInstance(payload.x, payload.y) ?: return@enqueueWork
                val toDrop: ItemStack
                val newGrid = if (payload.entireStack || itemInstance.stack.count <= 1) {
                    val result = grid.removeItem(payload.x, payload.y) ?: return@enqueueWork
                    toDrop = result.second
                    result.first
                } else {
                    toDrop = itemInstance.stack.split(1)
                    grid.replaceItem(payload.x, payload.y, itemInstance.stack) ?: return@enqueueWork
                }
                menu.updateGrid("container", newGrid)
                val itemEntity = player.drop(toDrop, false)
                itemEntity?.setPickUpDelay(40)
                PacketDistributor.sendToPlayer(player, SyncContainerPayload(newGrid))
                player.containerMenu.broadcastChanges()
                return@enqueueWork
            }

            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
            val allGrids = equipment.getAllActiveGrids(player)
            val grid = allGrids[payload.gridName] ?: return@enqueueWork
            val itemInstance = grid.getItemInstance(payload.x, payload.y) ?: return@enqueueWork
            val toDrop: ItemStack
            val newGrid = if (payload.entireStack || itemInstance.stack.count <= 1) {
                val result = grid.removeItem(payload.x, payload.y) ?: return@enqueueWork
                toDrop = result.second
                result.first
            } else {
                toDrop = itemInstance.stack.split(1)
                grid.replaceItem(payload.x, payload.y, itemInstance.stack) ?: return@enqueueWork
            }
            equipment.updateGrid(payload.gridName, newGrid, player)
            val itemEntity = player.drop(toDrop, false)
            itemEntity?.setPickUpDelay(40)
            player.containerMenu.broadcastChanges()
            PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(equipment))
        }
    }

    fun handleDropCarried(payload: DropCarriedPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val carried = player.containerMenu.carried
            if (!carried.isEmpty) {
                val toDrop = if (payload.entireStack || carried.count <= 1) {
                    player.containerMenu.carried = ItemStack.EMPTY
                    carried
                } else {
                    carried.split(1)
                }
                val itemEntity = player.drop(toDrop, false)
                itemEntity?.setPickUpDelay(40)
                player.containerMenu.broadcastChanges()
            }
        }
    }

    fun handleQuickMoveGridItem(payload: QuickMoveGridItemPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val menu = player.containerMenu as? GridInventoryMenu ?: return@enqueueWork

            val moved = GridQuickMoveHelper.quickMove(
                player,
                menu,
                payload.sourceGrid,
                payload.x,
                payload.y
            )

            if (moved) {
                val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
                PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(equipment))
                menu.containerGrid?.let {
                    PacketDistributor.sendToPlayer(player, SyncContainerPayload(it))
                }
                player.containerMenu.broadcastChanges()
            }
        }
    }
}
