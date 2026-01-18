package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.dataattachment.PlayerHealth
import com.gmail.vincent031525.extractionshooter.health.BodyPart
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class HealItem(properties: Properties, val maxHealAmount: Float, val duration: Int) : MedicalItem(properties) {
    override fun getUsageDuration(): Int = duration

    override fun canUse(player: Player): Boolean {
        val data = player.getData(ModDataAttachments.PLAYER_HEALTH)
        return data.head < BodyPart.HEAD.maxHealth || data.body < BodyPart.BODY.maxHealth || data.legs < BodyPart.LEGS.maxHealth
    }

    override fun finishUsingItem(stack: ItemStack, level: Level, entity: LivingEntity): ItemStack {
        if (!level.isClientSide && entity is Player) {
            val health = entity.getData(ModDataAttachments.PLAYER_HEALTH)
            var pool = maxHealAmount

            for (part in PlayerHealth.HEAL_ORDER) {
                if (pool <= 0) break

                val currentHealth = health.getHealth(part)
                val maxHealth = part.maxHealth

                if (currentHealth <= 0f) continue

                if (currentHealth < maxHealth) {
                    val needed = maxHealth - currentHealth
                    val healAmount = minOf(pool, needed)

                    health.heal(part, healAmount)
                    pool -= healAmount
                }
            }

            entity.setData(ModDataAttachments.PLAYER_HEALTH, health)

            if (!entity.abilities.instabuild) {
                stack.hurtAndBreak((maxHealAmount - pool).toInt(), entity, EquipmentSlot.MAINHAND)
            }
        }
        return stack
    }
}
