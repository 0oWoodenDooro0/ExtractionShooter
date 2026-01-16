package com.gmail.vincent031525.extractionshooter.datamap

import net.minecraft.network.chat.Component
import net.minecraft.util.StringRepresentable

data class GunStats(
    val rps: Int,
    val range: Double,
    val verticalRecoil: Float,
    val horizontalRecoil: Float,
    val fireModeCycle: List<FireMode>
) {
    companion object {
        val CODEC: com.mojang.serialization.Codec<GunStats> =
            com.mojang.serialization.codecs.RecordCodecBuilder.create { instance ->
                instance.group(
                    com.mojang.serialization.Codec.INT.fieldOf("rps").forGetter { it.rps },
                    com.mojang.serialization.Codec.DOUBLE.fieldOf("range").forGetter { it.range },
                    com.mojang.serialization.Codec.FLOAT.fieldOf("verticalRecoil").forGetter { it.verticalRecoil },
                    com.mojang.serialization.Codec.FLOAT.fieldOf("horizontalRecoil").forGetter { it.horizontalRecoil },
                    FireMode.CODEC.listOf().fieldOf("fireModeCycle").forGetter { it.fireModeCycle }
                ).apply(instance, ::GunStats)
            }
    }

    val shootTickDelay: Int
        get() = if (rps > 0) (20 / rps).coerceAtLeast(1) else 20
    val animationSpeed: Double
        get() = 20.0 / shootTickDelay.toDouble()


    enum class FireMode(private val serializedName: String) : StringRepresentable {
        SEMI("semi"), AUTO("auto"), BURST("burst");

        override fun getSerializedName(): String = serializedName

        companion object {
            val CODEC: com.mojang.serialization.Codec<FireMode> =
                StringRepresentable.fromEnum { FireMode.entries.toTypedArray() }
        }

        fun getDisplayName(): Component {
            return Component.translatable("firemode.extractionshooter.${serializedName}")
        }
    }
}
