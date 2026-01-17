package com.gmail.vincent031525.extractionshooter.event

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.dataattachment.PlayerHealth
import com.gmail.vincent031525.extractionshooter.health.BodyPart
import com.gmail.vincent031525.extractionshooter.health.LimbAABBHelper
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent

@EventBusSubscriber(modid = Extractionshooter.ID)
object DamageHandler {

    @SubscribeEvent
    fun onLivingDamage(event: LivingIncomingDamageEvent) {
        val entity = event.entity
        if (entity !is Player || entity.level().isClientSide) return

        val source = event.source
        val amount = event.amount
        val healthData = entity.getData(ModDataAttachments.PLAYER_HEALTH)

        val hitVec = source.sourcePosition
        val part = when {
            source.`is`(DamageTypes.FALL) -> BodyPart.LEGS
            else -> LimbAABBHelper.getTargetPart(entity, hitVec)
        }

        applyPartDamage(entity, healthData, part, amount)

        if (healthData.head <= 0f || healthData.body <= 0f) {
            event.amount = Float.MAX_VALUE
            return
        }

        event.amount = 0f
    }

    private fun applyPartDamage(player: Player, data: PlayerHealth, part: BodyPart?, amount: Float) {
        when (part) {
            BodyPart.HEAD,
            BodyPart.BODY -> {
                data.damage(part, amount)
            }

            BodyPart.LEGS -> {
                if (data.legs <= 0f) {
                    data.damage(BodyPart.BODY, amount * 0.8f)
                } else {
                    data.damage(part, amount)
                }
            }

            else -> {}
        }

        player.setData(ModDataAttachments.PLAYER_HEALTH, data)
    }
}
