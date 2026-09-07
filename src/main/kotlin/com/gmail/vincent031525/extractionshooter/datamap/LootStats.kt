package com.gmail.vincent031525.extractionshooter.datamap

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class LootStats(
    val rarity: ItemRarity = ItemRarity.COMMON,
    val value: Int = 0,
) {
    companion object {
        val CODEC: Codec<LootStats> = RecordCodecBuilder.create { instance ->
            instance.group(
                ItemRarity.CODEC.fieldOf("rarity").forGetter(LootStats::rarity),
                Codec.INT.fieldOf("value").forGetter(LootStats::value),
            ).apply(instance, ::LootStats)
        }

        val DEFAULT = LootStats(ItemRarity.COMMON, 0)
    }
}
