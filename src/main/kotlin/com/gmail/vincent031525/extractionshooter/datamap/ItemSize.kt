package com.gmail.vincent031525.extractionshooter.datamap

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class ItemSize(val width: Int, val height: Int) {
    companion object {
        val CODEC: Codec<ItemSize> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("width").forGetter(ItemSize::width),
                Codec.INT.fieldOf("height").forGetter(ItemSize::height)
            ).apply(instance, ::ItemSize)
        }

        val DEFAULT = ItemSize(1, 1)
    }
}
