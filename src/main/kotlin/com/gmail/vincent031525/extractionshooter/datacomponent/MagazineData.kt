package com.gmail.vincent031525.extractionshooter.datacomponent

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec

data class MagazineData(
    val ammoCount: Int = 0
) {
    companion object {
        val CODEC: Codec<MagazineData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("ammo_count").forGetter { it.ammoCount },
            ).apply(instance, ::MagazineData)
        }

        val STREAM_CODEC: StreamCodec<ByteBuf, MagazineData> = StreamCodec.composite(
            ByteBufCodecs.INT, MagazineData::ammoCount,
            ::MagazineData
        )
    }
}
