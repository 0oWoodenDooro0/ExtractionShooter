package com.gmail.vincent031525.extractionshooter.dataattachment

import com.gmail.vincent031525.extractionshooter.health.BodyPart
import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class PlayerHealth(
    var head: Float = BodyPart.HEAD.maxHealth,
    var body: Float = BodyPart.BODY.maxHealth,
    var legs: Float = BodyPart.LEGS.maxHealth
) {
    companion object {
        val CODEC: MapCodec<PlayerHealth> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.FLOAT.fieldOf("head").forGetter { it.head },
                Codec.FLOAT.fieldOf("body").forGetter { it.body },
                Codec.FLOAT.fieldOf("legs").forGetter { it.legs }
            ).apply(instance, ::PlayerHealth)
        }

        val HEAL_ORDER = listOf(BodyPart.BODY, BodyPart.HEAD, BodyPart.LEGS)
    }

    fun getHealth(part: BodyPart): Float {
        return when (part) {
            BodyPart.HEAD -> head
            BodyPart.BODY -> body
            BodyPart.LEGS -> legs
        }
    }

    fun heal(part: BodyPart, amount: Float) {
        when (part) {
            BodyPart.HEAD -> head = (head + amount).coerceAtMost(BodyPart.HEAD.maxHealth)
            BodyPart.BODY -> body = (body + amount).coerceAtMost(BodyPart.BODY.maxHealth)
            BodyPart.LEGS -> legs = (legs + amount).coerceAtMost(BodyPart.LEGS.maxHealth)
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