package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class DropCarriedPayload(
    val entireStack: Boolean
) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<DropCarriedPayload>(
            Identifier.fromNamespaceAndPath(Extractionshooter.ID, "drop_carried")
        )
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DropCarriedPayload> = StreamCodec.composite(
            ByteBufCodecs.BOOL, DropCarriedPayload::entireStack,
            ::DropCarriedPayload
        )
    }
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
