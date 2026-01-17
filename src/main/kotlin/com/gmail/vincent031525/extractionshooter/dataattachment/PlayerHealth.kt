package com.gmail.vincent031525.extractionshooter.dataattachment

import com.gmail.vincent031525.extractionshooter.health.BodyPart
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class PlayerHealth(
    var head: Float = 35f,
    var body: Float = 85f,
    var legs: Float = 65f
) {
    companion object {
        val CODEC: MapCodec<PlayerHealth> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("head").forGetter { it.head },
                Codec.FLOAT.fieldOf("body").forGetter { it.body },
                Codec.FLOAT.fieldOf("legs").forGetter { it.legs }
            ).apply(instance, ::PlayerHealth)
        }
    }

    fun damage(part: BodyPart, amount: Float) {
        when (part) {
            BodyPart.HEAD -> head = (head - amount).coerceAtLeast(0f)
            BodyPart.BODY -> body = (body - amount).coerceAtLeast(0f)
            BodyPart.LEGS -> legs = (legs - amount).coerceAtLeast(0f)
        }
    }
}