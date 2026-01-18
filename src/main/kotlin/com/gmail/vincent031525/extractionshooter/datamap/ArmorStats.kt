package com.gmail.vincent031525.extractionshooter.datamap

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class ArmorStats(
    val armorClass: Int,
    val maxDurability: Float,
    val bluntThroughput: Float
) {
    companion object {
        val CODEC: Codec<ArmorStats> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("armorClass").forGetter(ArmorStats::armorClass),
                Codec.FLOAT.fieldOf("maxDurability").forGetter(ArmorStats::maxDurability),
                Codec.FLOAT.fieldOf("bluntThroughput").forGetter(ArmorStats::bluntThroughput)
            ).apply(instance, ::ArmorStats)
        }
    }
}
