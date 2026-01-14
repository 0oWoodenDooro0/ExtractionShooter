package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

class ReloadPayload : CustomPacketPayload {
    companion object {
        val TYPE =
            CustomPacketPayload.Type<ReloadPayload>(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "reload"))

        val STREAM_CODEC: StreamCodec<ByteBuf, ReloadPayload> = StreamCodec.of(
            { _, _ -> },
            { _ -> ReloadPayload() }
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}
