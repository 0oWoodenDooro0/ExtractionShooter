package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class MoveGridItemPayload(
    val fromGrid: String,
    val fromX: Int,
    val fromY: Int,
    val toGrid: String,
    val toX: Int,
    val toY: Int,
    val rotated: Boolean
) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<MoveGridItemPayload>(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "move_grid_item"))
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MoveGridItemPayload> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, MoveGridItemPayload::fromGrid,
            ByteBufCodecs.VAR_INT, MoveGridItemPayload::fromX,
            ByteBufCodecs.VAR_INT, MoveGridItemPayload::fromY,
            ByteBufCodecs.STRING_UTF8, MoveGridItemPayload::toGrid,
            ByteBufCodecs.VAR_INT, MoveGridItemPayload::toX,
            ByteBufCodecs.VAR_INT, MoveGridItemPayload::toY,
            ByteBufCodecs.BOOL, MoveGridItemPayload::rotated,
            ::MoveGridItemPayload
        )
    }
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
