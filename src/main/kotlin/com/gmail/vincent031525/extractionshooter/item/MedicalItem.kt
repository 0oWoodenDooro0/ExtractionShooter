package com.gmail.vincent031525.extractionshooter.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.level.Level

abstract class MedicalItem(properties: Properties) : Item(properties.stacksTo(1)) {

    abstract fun getUsageDuration(): Int

    abstract fun canUse(player: Player): Boolean

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int = getUsageDuration()

    override fun getUseAnimation(stack: ItemStack): ItemUseAnimation = ItemUseAnimation.BOW

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        if (canUse(player)) {
            player.startUsingItem(hand)
            return InteractionResult.CONSUME
        }
        return InteractionResult.FAIL
    }
}
