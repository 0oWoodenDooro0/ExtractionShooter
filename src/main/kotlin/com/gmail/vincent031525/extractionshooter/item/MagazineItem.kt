package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.datamap.MagazineStats
import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import net.minecraft.ChatFormatting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.TooltipDisplay
import java.util.function.Consumer

class MagazineItem(properties: Properties) : Item(properties.stacksTo(1)) {

    companion object {

        fun getMagazineData(stack: ItemStack): MagazineData? {
            return stack.get(ModDataComponents.MAGAZINE_DATA)
        }

        fun getAmmoItem(stack: ItemStack): Item {
            return getMagazineData(stack)?.ammoItem ?: Items.AIR
        }
    }

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

        val data = getMagazineData(stack) ?: return false

        val currentCount = data.ammoCount
        val maxCount = getMagazineStats().maxAmmo
        val spaceLeft = maxCount - currentCount

        if (data.ammoItem != Items.AIR && data.ammoItem != other.item) return false

        if (spaceLeft > 0) {
            val amountToAdd = 1

            if (other.count >= amountToAdd) {
                stack.set(
                    ModDataComponents.MAGAZINE_DATA,
                    data.copy(
                        ammoCount = currentCount + amountToAdd,
                        ammoItem = if (currentCount == 0) other.item else data.ammoItem
                    )
                )

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

    fun getMagazineStats(): MagazineStats =
        BuiltInRegistries.ITEM.wrapAsHolder(this).getData(ModDataMaps.MAGAZINE_STATS)!!

}
