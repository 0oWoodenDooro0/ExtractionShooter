package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

class ShootPayload : CustomPacketPayload {
    companion object {
        val TYPE =
            CustomPacketPayload.Type<ShootPayload>(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "shoot"))

        val STREAM_CODEC: StreamCodec<ByteBuf, ShootPayload> = StreamCodec.of(
            { _, _ -> },
            { _ -> ShootPayload() }
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}