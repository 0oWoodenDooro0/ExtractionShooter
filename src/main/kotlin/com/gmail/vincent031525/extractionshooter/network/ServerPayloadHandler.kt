package com.gmail.vincent031525.extractionshooter.network

import com.gmail.vincent031525.extractionshooter.inventory.GridActionHandler
import com.gmail.vincent031525.extractionshooter.item.GunItem
import com.gmail.vincent031525.extractionshooter.item.MagazineItem
import com.gmail.vincent031525.extractionshooter.network.payload.*
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
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

                val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
                InventoryUtils.syncWeaponFromHotbarToEquipment(player, equipment)
                player.setData(ModDataAttachments.PLAYER_EQUIPMENT, equipment)
                PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(equipment))
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

            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
            InventoryUtils.syncWeaponFromHotbarToEquipment(player, equipment)
            player.setData(ModDataAttachments.PLAYER_EQUIPMENT, equipment)
            player.containerMenu.broadcastChanges()
        }
    }

    fun handleReload(payload: ReloadPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val stack = player.mainHandItem
            val item = stack.item as? GunItem<*> ?: return@enqueueWork
            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
            val allGrids = equipment.getAllActiveGrids()

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

            for (i in 0 until player.inventory.containerSize) {
                val invStack = player.inventory.getItem(i)
                if (invStack.item is MagazineItem) {
                    val magData = MagazineItem.getMagazineData(invStack)
                    val ammoCount = magData?.ammoCount ?: 0
                    candidates.add(MagCandidate(invStack, ammoCount, null, invSlot = i))
                }
            }

            if (candidates.isEmpty()) return@enqueueWork

            val chosen = candidates.maxByOrNull { it.ammoCount } ?: return@enqueueWork

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
                    equipment.updateGrid(chosen.gridName, newGrid)
                }
            } else {
                val invStack = player.inventory.getItem(chosen.invSlot)
                newMagStack = invStack.split(1)
            }

            val oldMag = item.unloadMagazine(stack)
            if (!oldMag.isEmpty) {
                var stored = false
                val updatedGrids = equipment.getAllActiveGrids()
                for (storeName in gridSearchOrder) {
                    val storeGrid = updatedGrids[storeName] ?: continue
                    val space = storeGrid.findSpaceForItem(oldMag)
                    if (space != null) {
                        val updatedStoreGrid = storeGrid.addItem(oldMag, space.first, space.second, false)
                        if (updatedStoreGrid != null) {
                            equipment.updateGrid(storeName, updatedStoreGrid)
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

            InventoryUtils.syncWeaponFromHotbarToEquipment(player, equipment)
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

            // Ensure held weapons are synced to equipment before opening GUI
            InventoryUtils.syncWeaponFromHotbarToEquipment(player, equipment)
            player.setData(ModDataAttachments.PLAYER_EQUIPMENT, equipment)
            PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(equipment))

            player.openMenu(object : MenuProvider {
                override fun getDisplayName(): Component = Component.literal("Inventory")
                override fun createMenu(id: Int, inv: Inventory, p: Player): AbstractContainerMenu {
                    return com.gmail.vincent031525.extractionshooter.menu.GridInventoryMenu(id, inv, equipment)
                }
            })
        }
    }

    fun handlePickFromGrid(payload: PickFromGridPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
            val allGrids = equipment.getAllActiveGrids()
            val grid = allGrids[payload.gridName] ?: return@enqueueWork

            if (!player.containerMenu.carried.isEmpty) return@enqueueWork // Already holding something

            val result = grid.removeItem(payload.x, payload.y)
            if (result != null) {
                val (newGrid, stack) = result
                equipment.updateGrid(payload.gridName, newGrid)
                player.containerMenu.setCarried(stack)

                InventoryUtils.syncHotbarWithEquipment(player, equipment)
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
            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
            val allGrids = equipment.getAllActiveGrids()
            val grid = allGrids[payload.gridName] ?: return@enqueueWork

            val carried = player.containerMenu.carried
            if (carried.isEmpty) return@enqueueWork

            val newGrid = grid.addItem(carried, payload.x, payload.y, payload.rotated)
            if (newGrid != null) {
                equipment.updateGrid(payload.gridName, newGrid)
                player.containerMenu.setCarried(ItemStack.EMPTY)

                InventoryUtils.syncHotbarWithEquipment(player, equipment)
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
            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
            val allGrids = equipment.getAllActiveGrids()
            val grid = allGrids[payload.gridName] ?: return@enqueueWork

            val carried = player.containerMenu.carried
            val result = GridActionHandler.interact(
                player.level(),
                grid,
                payload.x,
                payload.y,
                carried,
                payload.button
            ) ?: return@enqueueWork

            equipment.updateGrid(payload.gridName, result.newGrid)
            player.containerMenu.setCarried(result.newCarried)

            InventoryUtils.syncHotbarWithEquipment(player, equipment)
            val syncPacket = SyncEquipmentPayload(equipment)
            PacketDistributor.sendToPlayer(player, syncPacket)
        }
    }
}
