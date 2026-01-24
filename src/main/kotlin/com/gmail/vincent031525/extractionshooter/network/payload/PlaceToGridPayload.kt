package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class PlaceToGridPayload(
    val gridName: String,
    val x: Int,
    val y: Int,
    val rotated: Boolean
) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<PlaceToGridPayload>(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "place_to_grid"))
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PlaceToGridPayload> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PlaceToGridPayload::gridName,
            ByteBufCodecs.VAR_INT, PlaceToGridPayload::x,
            ByteBufCodecs.VAR_INT, PlaceToGridPayload::y,
            ByteBufCodecs.BOOL, PlaceToGridPayload::rotated,
            ::PlaceToGridPayload
        )
    }
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
