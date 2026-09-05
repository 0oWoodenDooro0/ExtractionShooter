package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.dataattachment.PlayerHealth
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class SyncHealthPayload(val health: PlayerHealth) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<SyncHealthPayload>(
            Identifier.fromNamespaceAndPath(
                Extractionshooter.ID,
                "sync_health"
            )
        )

        val STREAM_CODEC: StreamCodec<ByteBuf, SyncHealthPayload> =
            StreamCodec.composite(
                PlayerHealth.STREAM_CODEC, { it.health },
                ::SyncHealthPayload
            )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
