package com.gmail.vincent031525.extractionshooter.network

import com.gmail.vincent031525.extractionshooter.datacomponent.GunData
import com.gmail.vincent031525.extractionshooter.item.GunItem
import com.gmail.vincent031525.extractionshooter.network.payload.ReloadPayload
import com.gmail.vincent031525.extractionshooter.network.payload.ShootPayload
import com.gmail.vincent031525.extractionshooter.network.payload.SwitchModePayload
import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.neoforged.neoforge.network.handling.IPayloadContext

object ServerPayloadHandler {

    fun handleSwitchMode(payload: SwitchModePayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val stack = player.mainHandItem

            val currentData = stack.get(ModDataComponents.GUN_DATA)
            if (currentData != null) {

                val newMode = when (currentData.fireMode) {
                    GunData.FireMode.SEMI -> GunData.FireMode.AUTO
                    GunData.FireMode.AUTO -> GunData.FireMode.BURST
                    GunData.FireMode.BURST -> GunData.FireMode.SEMI
                }

                val newData = currentData.copy(fireMode = newMode)
                stack.set(ModDataComponents.GUN_DATA, newData)

                player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.UI_BUTTON_CLICK.value(),
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
                )

                player.displayClientMessage(
                    Component.translatable("message.extractionshooter.firemode_changed", newMode.getDisplayName()),
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

            if (item is GunItem) {
                item.tryShoot(player.level(), player, stack)
            }
        }
    }

    fun handleReload(payload: ReloadPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = context.player() as? ServerPlayer ?: return@enqueueWork
            val gunStack = player.mainHandItem
            val gunItem = gunStack.item as? GunItem ?: return@enqueueWork

            val magazineSlot = (0 until player.inventory.containerSize).find { i ->
                val magazineStack = player.inventory.getItem(i)
                val magazineData = magazineStack.get(ModDataComponents.MAGAZINE_DATA)
                magazineData != null && magazineData.ammoCount > 0
            } ?: return@enqueueWork

            gunItem.reload(player, gunStack, magazineSlot)
        }
    }
}