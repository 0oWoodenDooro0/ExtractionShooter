package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.registry.ModEffects
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class PainkillersItem(properties: Properties) : MedicalItem(properties) {

    override fun getUsageDuration() = 20

    override fun canUse(player: Player) = true

    override fun finishUsingItem(stack: ItemStack, level: Level, entity: LivingEntity): ItemStack {
        if (!level.isClientSide && entity is Player) {
            entity.addEffect(MobEffectInstance(ModEffects.ON_PAINKILLERS, 1200, 0))

            if (!entity.abilities.instabuild) {
                stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND)
            }
        }
        return stack
    }
}