package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

class SwitchModePayload : CustomPacketPayload {
    companion object {
        val TYPE = CustomPacketPayload.Type<SwitchModePayload>(
            Identifier.fromNamespaceAndPath(
                Extractionshooter.ID,
                "switch_mode"
            )
        )

        val STREAM_CODEC: StreamCodec<ByteBuf, SwitchModePayload> = StreamCodec.of({ _, _ -> }, { SwitchModePayload() })
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}