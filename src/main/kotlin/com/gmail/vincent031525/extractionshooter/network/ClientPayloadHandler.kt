package com.gmail.vincent031525.extractionshooter.network

import com.gmail.vincent031525.extractionshooter.network.payload.SyncEquipmentPayload
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import net.minecraft.client.Minecraft
import net.neoforged.neoforge.network.handling.IPayloadContext

object ClientPayloadHandler {
    fun handleSyncEquipment(payload: SyncEquipmentPayload, context: IPayloadContext) {
        context.enqueueWork {
            val player = Minecraft.getInstance().player ?: return@enqueueWork
            player.setData(ModDataAttachments.PLAYER_EQUIPMENT, payload.equipment)
        }
    }
}
