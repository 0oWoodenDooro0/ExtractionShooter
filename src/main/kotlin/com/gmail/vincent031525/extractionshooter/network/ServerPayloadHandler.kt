package com.gmail.vincent031525.extractionshooter.network

import com.gmail.vincent031525.extractionshooter.item.GunItem
import com.gmail.vincent031525.extractionshooter.network.payload.*
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
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

                player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.UI_BUTTON_CLICK.value(),
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
                )

                player.displayClientMessage(
                    Component.translatable("message.extractionshooter.firemode_changed", nextFireMode.getDisplayName()),
                    true
                )
            }
        }
    }

    fun handleShoot(payload: ShootPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val stack = player.mainHandItem
            val item = stack.item

            if (item is GunItem<*>) {
                item.tryShoot(player.level(), player, stack)
            }
        }
    }

    fun handleReload(payload: ReloadPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val gunStack = player.mainHandItem
            val gunItem = gunStack.item as? GunItem<*> ?: return@enqueueWork

            val magazineSlot = (0 until player.inventory.containerSize).find { i ->
                val magazineStack = player.inventory.getItem(i)
                val magazineData = magazineStack.get(ModDataComponents.MAGAZINE_DATA)
                magazineData != null && magazineData.ammoCount > 0
            } ?: return@enqueueWork

            gunItem.reload(player, gunStack, magazineSlot)
        }
    }

    fun handleOpenInventory(payload: com.gmail.vincent031525.extractionshooter.network.payload.OpenInventoryPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
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
                val syncPacket = SyncEquipmentPayload(equipment)
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, syncPacket)
            }
        }
    }

    fun handlePlaceToGrid(
        payload: com.gmail.vincent031525.extractionshooter.network.payload.PlaceToGridPayload,
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
                player.containerMenu.setCarried(net.minecraft.world.item.ItemStack.EMPTY)
                val syncPacket = SyncEquipmentPayload(equipment)
                net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, syncPacket)
            }
        }
    }
}