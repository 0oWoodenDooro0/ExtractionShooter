package com.gmail.vincent031525.extractionshooter.datamap

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class AmmoStats(val damage: Int) {

    companion object {
        val CODEC: Codec<AmmoStats> =
            RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("damage").forGetter { it.damage },
                ).apply(instance, ::AmmoStats)
            }
    }
}
