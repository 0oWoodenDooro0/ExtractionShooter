package com.gmail.vincent031525.extractionshooter.datamap

import com.mojang.serialization.Codec

data class MagazineStats(val maxAmmo: Int = 30, val reloadTick: Int = 40) {

    companion object {
        val CODEC: Codec<MagazineStats> =
            com.mojang.serialization.codecs.RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("maxAmmo").forGetter { it.maxAmmo },
                    Codec.INT.fieldOf("reloadTick").forGetter { it.reloadTick },
                ).apply(instance, ::MagazineStats)
            }
    }
}
