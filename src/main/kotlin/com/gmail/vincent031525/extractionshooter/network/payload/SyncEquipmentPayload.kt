package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.inventory.PlayerEquipment
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class SyncEquipmentPayload(val equipment: PlayerEquipment) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<SyncEquipmentPayload>(
            Identifier.fromNamespaceAndPath(
                Extractionshooter.ID,
                "sync_equipment"
            )
        )

        val STREAM_CODEC: StreamCodec<net.minecraft.network.RegistryFriendlyByteBuf, SyncEquipmentPayload> =
            StreamCodec.composite(
                PlayerEquipment.STREAM_CODEC, { it.equipment },
                ::SyncEquipmentPayload
            )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
