package com.gmail.vincent031525.extractionshooter.util

import com.gmail.vincent031525.extractionshooter.network.payload.SyncHealthPayload
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object HealthUtils {
    fun syncHealth(player: Player) {
        if (player is ServerPlayer) {
            val health = player.getData(ModDataAttachments.PLAYER_HEALTH)
            PacketDistributor.sendToPlayer(player, SyncHealthPayload(health))
        }
    }
}
