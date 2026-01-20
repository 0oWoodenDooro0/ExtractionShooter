package com.gmail.vincent031525.extractionshooter.event

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.datamap.ItemSize
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent

@EventBusSubscriber(modid = Extractionshooter.ID)
object ItemTooltipEventHandler {
    @SubscribeEvent
    fun onTooltip(event: ItemTooltipEvent) {
        val stack = event.itemStack

        val size = BuiltInRegistries.ITEM.wrapAsHolder(stack.item).getData(ModDataMaps.ITEM_SIZE) ?: ItemSize.DEFAULT

        event.toolTip.add(
            Component.literal("佔用空間: ${size.width} x ${size.height}")
                .withStyle(ChatFormatting.GRAY)
        )
    }
}
