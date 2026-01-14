package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import java.util.function.Consumer

abstract class MagazineItem(properties: Properties) : Item(properties.stacksTo(1)) {

    abstract val magazineStats: MagazineStats

    data class MagazineStats(val maxAmmo: Int, val reloadTick: Int)

    @Deprecated("Deprecated in Java")
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipDisplay: TooltipDisplay,
        tooltipAdder: Consumer<Component>,
        flag: TooltipFlag
    ) {
        val data = stack.get(ModDataComponents.MAGAZINE_DATA) ?: return
        tooltipAdder.accept(
            Component.translatable("tooltip.extractionshooter.ammo", data.ammoCount).withStyle(ChatFormatting.GRAY)
        )
    }
}
