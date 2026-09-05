package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import net.minecraft.world.phys.Vec3

data class ShootPayload(
    val origin: Vec3,
    val direction: Vec3
) : CustomPacketPayload {
    companion object {
        val TYPE =
            CustomPacketPayload.Type<ShootPayload>(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "shoot"))

        val STREAM_CODEC: StreamCodec<ByteBuf, ShootPayload> = StreamCodec.of(
            { buf, payload ->
                buf.writeDouble(payload.origin.x)
                buf.writeDouble(payload.origin.y)
                buf.writeDouble(payload.origin.z)
                buf.writeDouble(payload.direction.x)
                buf.writeDouble(payload.direction.y)
                buf.writeDouble(payload.direction.z)
            },
            { buf ->
                val origin = Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
                val direction = Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
                ShootPayload(origin, direction)
            }
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return TYPE
    }
}
