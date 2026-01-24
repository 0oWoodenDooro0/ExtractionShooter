package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class PickFromGridPayload(
    val gridName: String,
    val x: Int,
    val y: Int
) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<PickFromGridPayload>(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "pick_from_grid"))
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PickFromGridPayload> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PickFromGridPayload::gridName,
            ByteBufCodecs.VAR_INT, PickFromGridPayload::x,
            ByteBufCodecs.VAR_INT, PickFromGridPayload::y,
            ::PickFromGridPayload
        )
    }
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
