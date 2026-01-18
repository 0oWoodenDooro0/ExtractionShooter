package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.registry.ModEffects
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class BandageItem(properties: Properties) : MedicalItem(properties) {

    override fun getUsageDuration(): Int = 40

    override fun canUse(player: Player): Boolean = player.hasEffect(ModEffects.BLEEDING)

    override fun finishUsingItem(stack: ItemStack, level: Level, entity: LivingEntity): ItemStack {
        if (!level.isClientSide && entity is Player) {
            entity.removeEffect(ModEffects.BLEEDING)
            if (!entity.abilities.instabuild) {
                stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND)
            }
        }
        return stack
    }
}
