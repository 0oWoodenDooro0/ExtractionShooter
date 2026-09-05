package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class DropGridItemPayload(
    val gridName: String,
    val x: Int,
    val y: Int,
    val entireStack: Boolean
) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<DropGridItemPayload>(
            Identifier.fromNamespaceAndPath(Extractionshooter.ID, "drop_grid_item")
        )
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DropGridItemPayload> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, DropGridItemPayload::gridName,
            ByteBufCodecs.VAR_INT, DropGridItemPayload::x,
            ByteBufCodecs.VAR_INT, DropGridItemPayload::y,
            ByteBufCodecs.BOOL, DropGridItemPayload::entireStack,
            ::DropGridItemPayload
        )
    }
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
