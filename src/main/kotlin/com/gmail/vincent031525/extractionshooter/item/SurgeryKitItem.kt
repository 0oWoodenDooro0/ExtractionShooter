package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class SurgeryKitItem(properties: Properties) : MedicalItem(properties) {

    override fun getUsageDuration(): Int = 240

    override fun canUse(player: Player): Boolean {
        val data = player.getData(ModDataAttachments.PLAYER_HEALTH)
        return data.legs <= 0f
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, entity: LivingEntity): ItemStack {
        if (!level.isClientSide && entity is Player) {
            val data = entity.getData(ModDataAttachments.PLAYER_HEALTH)

            if (data.legs <= 0f) {
                data.legs = 1.0f
            }

            entity.setData(ModDataAttachments.PLAYER_HEALTH, data)

            if (!entity.abilities.instabuild) {
                stack.hurtAndBreak(1, entity, EquipmentSlot.MAINHAND)
            }
        }
        return stack
    }
}
