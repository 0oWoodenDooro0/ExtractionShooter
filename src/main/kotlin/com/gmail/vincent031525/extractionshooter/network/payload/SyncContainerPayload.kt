package com.gmail.vincent031525.extractionshooter.network.payload

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier

data class SyncContainerPayload(
    val containerGrid: GridInventory
) : CustomPacketPayload {
    companion object {
        val ID = CustomPacketPayload.Type<SyncContainerPayload>(
            Identifier.fromNamespaceAndPath(Extractionshooter.ID, "sync_container")
        )
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncContainerPayload> = StreamCodec.composite(
            GridInventory.STREAM_CODEC, SyncContainerPayload::containerGrid,
            ::SyncContainerPayload
        )
    }
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID
}
