package com.gmail.vincent031525.extractionshooter.event

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.dataattachment.PlayerHealth
import com.gmail.vincent031525.extractionshooter.network.payload.SyncEquipmentPayload
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.util.HealthUtils
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.network.PacketDistributor

@EventBusSubscriber(modid = Extractionshooter.ID)
object PlayerEventHandler {

    @SubscribeEvent
    fun onPlayerLoggedIn(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity
        if (player is ServerPlayer) {
            syncEquipment(player)
            HealthUtils.syncHealth(player)
        }
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        val player = event.entity
        if (player is ServerPlayer) {
            player.setData(ModDataAttachments.PLAYER_HEALTH, PlayerHealth())
            syncEquipment(player)
            HealthUtils.syncHealth(player)
        }
    }

    @SubscribeEvent
    fun onPlayerChangedDimension(event: PlayerEvent.PlayerChangedDimensionEvent) {
        val player = event.entity
        if (player is ServerPlayer) {
            syncEquipment(player)
            HealthUtils.syncHealth(player)
        }
    }

    private fun syncEquipment(player: ServerPlayer) {
        val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
        PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(equipment))
    }
}
