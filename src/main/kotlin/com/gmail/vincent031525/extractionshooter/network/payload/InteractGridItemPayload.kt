package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class InteractGridItemPayload(
    val gridName: String,
    val x: Int,
    val y: Int,
    val button: Int
) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<InteractGridItemPayload>(
            Identifier.fromNamespaceAndPath(Extractionshooter.ID, "interact_grid_item")
        )
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, InteractGridItemPayload> = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, InteractGridItemPayload::gridName,
            ByteBufCodecs.VAR_INT, InteractGridItemPayload::x,
            ByteBufCodecs.VAR_INT, InteractGridItemPayload::y,
            ByteBufCodecs.VAR_INT, InteractGridItemPayload::button,
            ::InteractGridItemPayload
        )
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
