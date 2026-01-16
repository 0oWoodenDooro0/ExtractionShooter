package com.gmail.vincent031525.extractionshooter.datacomponent

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

data class MagazineData(
    val ammoCount: Int = 0,
    val ammoItem: Item = Items.AIR
) {
    companion object {
        val CODEC: Codec<MagazineData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("ammo_count").forGetter { it.ammoCount },
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("ammo_item").forGetter { it.ammoItem }
            ).apply(instance, ::MagazineData)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, MagazineData> = StreamCodec.composite(
            ByteBufCodecs.INT, MagazineData::ammoCount,
            ByteBufCodecs.registry(Registries.ITEM), MagazineData::ammoItem,
            ::MagazineData
        )
    }
}
