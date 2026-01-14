package com.gmail.vincent031525.extractionshooter.datacomponent

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack

data class GunData(
    val fireMode: FireMode = FireMode.SEMI,
    val nextAttackTick: Long = 0,
    val burstRemaining: Int = 0,
    val burstTickDelay: Int = 0,
    val magazineStack: ItemStack = ItemStack.EMPTY
) {
    enum class FireMode {
        SEMI, AUTO, BURST;

        fun getDisplayName(): Component {
            return Component.translatable("firemode.extractionshooter.${name.lowercase()}")
        }
    }

    companion object {
        val CODEC: Codec<GunData> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.xmap(FireMode::valueOf, FireMode::name).fieldOf("fire_mode").forGetter { it.fireMode },
                Codec.LONG.fieldOf("nextAttackTick").forGetter { it.nextAttackTick },
                Codec.INT.fieldOf("burstRemaining").forGetter { it.burstRemaining },
                Codec.INT.fieldOf("burstTickDelay").forGetter { it.burstTickDelay },
                ItemStack.OPTIONAL_CODEC.fieldOf("magazineStack").forGetter { it.magazineStack }
            ).apply(instance, ::GunData)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, GunData> = StreamCodec.composite(
            ByteBufCodecs.idMapper({ FireMode.entries.getOrElse(it) { FireMode.SEMI } }, FireMode::ordinal),
            GunData::fireMode,
            ByteBufCodecs.LONG,
            GunData::nextAttackTick,
            ByteBufCodecs.INT,
            GunData::burstRemaining,
            ByteBufCodecs.INT,
            GunData::burstTickDelay,
            ItemStack.OPTIONAL_STREAM_CODEC,
            GunData::magazineStack,
            ::GunData
        )
    }
}