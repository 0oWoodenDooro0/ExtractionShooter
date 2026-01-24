package com.gmail.vincent031525.extractionshooter.datamap

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class ContainerStats(val columns: Int, val rows: Int) {
    companion object {
        val CODEC: Codec<ContainerStats> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("columns").forGetter(ContainerStats::columns),
                Codec.INT.fieldOf("rows").forGetter(ContainerStats::rows)
            ).apply(instance, ::ContainerStats)
        }
    }
}
