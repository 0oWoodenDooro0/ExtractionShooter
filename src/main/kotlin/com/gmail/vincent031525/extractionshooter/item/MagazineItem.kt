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

        fun loadAmmo(magazineStack: ItemStack, ammoStack: ItemStack): Boolean {
            if (ammoStack.isEmpty || ammoStack.item !is AmmoItem) return false
            val magazineItem = magazineStack.item as? MagazineItem ?: return false
            val stats = magazineItem.getMagazineStats()
            val data = getMagazineData(magazineStack) ?: MagazineData()

            val currentCount = data.ammoCount
            val maxCount = stats.maxAmmo
            val spaceLeft = maxCount - currentCount

            if (currentCount > 0 && data.ammoItem != Items.AIR && data.ammoItem != ammoStack.item) return false
            if (spaceLeft <= 0) return false

            val amountToAdd = minOf(spaceLeft, ammoStack.count)
            if (amountToAdd <= 0) return false

            magazineStack.set(
                ModDataComponents.MAGAZINE_DATA,
                data.copy(
                    ammoCount = currentCount + amountToAdd,
                    ammoItem = if (currentCount == 0) ammoStack.item else data.ammoItem
                )
            )

            ammoStack.shrink(amountToAdd)
            return true
        }

        fun unloadAmmo(magazineStack: ItemStack): ItemStack {
            if (magazineStack.isEmpty || magazineStack.item !is MagazineItem) return ItemStack.EMPTY
            val data = getMagazineData(magazineStack) ?: return ItemStack.EMPTY
            val ammoItem = data.ammoItem

            if (data.ammoCount <= 0 || ammoItem == Items.AIR || ammoItem !is AmmoItem) return ItemStack.EMPTY
            val amountToRemove = minOf(data.ammoCount, 64)
            val ammoStack = ItemStack(ammoItem, amountToRemove)

            val newCount = data.ammoCount - amountToRemove
            magazineStack.set(
                ModDataComponents.MAGAZINE_DATA, data.copy(
                    ammoCount = newCount,
                    ammoItem = if (newCount <= 0) Items.AIR else ammoItem
                )
            )

            return ammoStack
        }
    }

    override fun overrideOtherStackedOnMe(
        stack: ItemStack,
        other: ItemStack,
        slot: Slot,
        action: ClickAction,
        player: Player,
        access: SlotAccess
    ): Boolean {
        if (action == ClickAction.SECONDARY && !other.isEmpty) {
            val loaded = loadAmmo(stack, other)
            if (loaded) {
                player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.ARMOR_EQUIP_GENERIC.value(),
                    SoundSource.PLAYERS,
                    1.0f, 1.5f
                )
                return true
            }
            return false
        }

        if (action == ClickAction.SECONDARY && other.isEmpty) {
            val ammoStack = unloadAmmo(stack)
            if (ammoStack.isEmpty) return false

            access.set(ammoStack)
            player.level()
                .playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0f, 1.2f)
            return true
        }

        return false
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

    fun getMagazineStats(): MagazineStats =
        BuiltInRegistries.ITEM.wrapAsHolder(this).getData(ModDataMaps.MAGAZINE_STATS) ?: MagazineStats()
}
