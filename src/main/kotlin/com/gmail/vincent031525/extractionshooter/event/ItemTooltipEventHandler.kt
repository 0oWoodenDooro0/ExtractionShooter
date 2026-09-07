package com.gmail.vincent031525.extractionshooter.event

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent
import java.text.NumberFormat
import java.util.Locale

@EventBusSubscriber(modid = Extractionshooter.ID)
object ItemTooltipEventHandler {
    @SubscribeEvent
    fun onTooltip(event: ItemTooltipEvent) {
        val stack = event.itemStack
        if (stack.isEmpty) return

        val lootStats = InventoryUtils.getLootStats(stack)
        if (lootStats != null) {
            val rarity = lootStats.rarity
            event.toolTip.add(
                Component.translatable("tooltip.extractionshooter.rarity")
                    .withStyle(ChatFormatting.GRAY)
                    .append(
                        Component.translatable(rarity.translationKey)
                            .withStyle(rarity.formatting)
                    )
            )

            if (lootStats.value > 0) {
                val formatter = NumberFormat.getIntegerInstance(Locale.US)
                val formattedUnit = formatter.format(lootStats.value)
                if (stack.count > 1) {
                    val totalVal = lootStats.value.toLong() * stack.count
                    val formattedTotal = formatter.format(totalVal)
                    event.toolTip.add(
                        Component.translatable("tooltip.extractionshooter.value_stack", formattedTotal, formattedUnit)
                            .withStyle(ChatFormatting.GOLD)
                    )
                } else {
                    event.toolTip.add(
                        Component.translatable("tooltip.extractionshooter.value", formattedUnit)
                            .withStyle(ChatFormatting.GOLD)
                    )
                }
            }
        }

        val size = InventoryUtils.getItemSize(stack)
        event.toolTip.add(
            Component.translatable("tooltip.extractionshooter.size", size.width, size.height)
                .withStyle(ChatFormatting.GRAY)
        )
    }
}
