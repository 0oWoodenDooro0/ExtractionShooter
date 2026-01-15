package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import java.util.function.Consumer

class MagazineItem(properties: Properties, val magazineStats: MagazineStats) : Item(properties.stacksTo(1)) {

    data class MagazineStats(val maxAmmo: Int = 30, val reloadTick: Int = 40)

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

    override fun overrideOtherStackedOnMe(
        stack: ItemStack,
        other: ItemStack,
        slot: Slot,
        action: ClickAction,
        player: Player,
        access: SlotAccess
    ): Boolean {
        if (action != ClickAction.SECONDARY || other.isEmpty) return false

        val data = stack.getMagazineData() ?: return false

        val currentCount = data.ammoCount
        val maxCount = magazineStats.maxAmmo
        val spaceLeft = maxCount - currentCount

        if (spaceLeft > 0) {
            val amountToAdd = 1

            if (other.count >= amountToAdd) {
                stack.set(ModDataComponents.MAGAZINE_DATA, data.copy(ammoCount = currentCount + amountToAdd))

                other.shrink(amountToAdd)

                player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.ARMOR_EQUIP_GENERIC.value(),
                    SoundSource.PLAYERS,
                    1.0f, 1.5f
                )
                return true
            }
        }

        return false
    }
}
