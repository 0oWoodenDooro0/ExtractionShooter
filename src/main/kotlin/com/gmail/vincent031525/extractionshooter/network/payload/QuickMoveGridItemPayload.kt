package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class QuickMoveGridItemPayload(
    val sourceGrid: String,
    val x: Int,
    val y: Int
) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<QuickMoveGridItemPayload>(
            Identifier.fromNamespaceAndPath(Extractionshooter.ID, "quick_move_grid_item")
        )
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, QuickMoveGridItemPayload> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, QuickMoveGridItemPayload::sourceGrid,
            ByteBufCodecs.VAR_INT, QuickMoveGridItemPayload::x,
            ByteBufCodecs.VAR_INT, QuickMoveGridItemPayload::y,
            ::QuickMoveGridItemPayload
        )
    }
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
