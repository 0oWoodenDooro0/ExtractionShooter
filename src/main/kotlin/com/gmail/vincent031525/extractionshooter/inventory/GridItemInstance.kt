package com.gmail.vincent031525.extractionshooter.inventory

import com.gmail.vincent031525.extractionshooter.datamap.ItemSize
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack

data class GridItemInstance(
    val stack: ItemStack,
    var x: Int,
    var y: Int,
    var rotated: Boolean = false
) {
    companion object {
        val CODEC: Codec<GridItemInstance> = RecordCodecBuilder.create { instance ->
            instance.group(
                ItemStack.CODEC.fieldOf("stack").forGetter(GridItemInstance::stack),
                Codec.INT.fieldOf("x").forGetter(GridItemInstance::x),
                Codec.INT.fieldOf("y").forGetter(GridItemInstance::y),
                Codec.BOOL.fieldOf("rotated").forGetter(GridItemInstance::rotated)
            ).apply(instance, ::GridItemInstance)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, GridItemInstance> = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, { it.stack },
            ByteBufCodecs.VAR_INT, { it.x },
            ByteBufCodecs.VAR_INT, { it.y },
            ByteBufCodecs.BOOL, { it.rotated },
            ::GridItemInstance
        )
    }

    val actualSize: ItemSize
        get() {
            val baseSize =
                BuiltInRegistries.ITEM.wrapAsHolder(stack.item).getData(ModDataMaps.ITEM_SIZE) ?: ItemSize.DEFAULT
            return if (rotated) ItemSize(baseSize.height, baseSize.width) else baseSize
        }

    fun occupies(checkX: Int, checkY: Int): Boolean {
        val size = actualSize
        return checkX in x until (x + size.width) && checkY in y until (y + size.height)
    }
}
