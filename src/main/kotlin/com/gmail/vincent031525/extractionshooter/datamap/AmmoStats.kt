package com.gmail.vincent031525.extractionshooter.datamap

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class AmmoStats(
    val damage: Float,
    val penetration: Float
) {

    companion object {
        val CODEC: Codec<AmmoStats> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.FLOAT.fieldOf("damage").forGetter { it.damage },
                    Codec.FLOAT.fieldOf("penetration").forGetter { it.penetration },
                ).apply(instance, ::AmmoStats)
            }
    }
}
