package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

class OpenInventoryPayload : CustomPacketPayload {
    companion object {
        val INSTANCE = OpenInventoryPayload()
        val ID = CustomPacketPayload.Type<OpenInventoryPayload>(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "open_inventory"))
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, OpenInventoryPayload> = StreamCodec.unit(INSTANCE)
    }
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
