package com.gmail.vincent031525.extractionshooter.event

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.dataattachment.PlayerHealth
import com.gmail.vincent031525.extractionshooter.health.BodyPart
import com.gmail.vincent031525.extractionshooter.health.LimbAABBHelper
import com.gmail.vincent031525.extractionshooter.registry.ModDamageTypes
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.registry.ModEffects
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
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
            source.`is`(ModDamageTypes.BLEEDING) -> BodyPart.BODY
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
        if (part == null) return
        val damage = if (part == BodyPart.LEGS && data.legs < 20f) {
            data.damage(BodyPart.BODY, amount * 0.8f)
            amount * 0.8f
        } else {
            data.damage(part, amount)
            amount
        }
        player.setData(ModDataAttachments.PLAYER_HEALTH, data)
        tryApplyBleeding(player, damage)

        if (part == BodyPart.LEGS && data.legs < 20f) {
            if (!player.hasEffect(ModEffects.FRACTURE)) {
                player.addEffect(
                    MobEffectInstance(
                        ModEffects.FRACTURE,
                        Int.MAX_VALUE,
                        0,
                        false,
                        true,
                        true
                    )
                )
            }
        }
    }

    private fun tryApplyBleeding(player: Player, damage: Float) {
        val random = player.random.nextFloat()

        val (chance, level) = when {
            damage > 12f -> 0.8f to 1
            damage > 6f -> 0.4f to 0
            damage > 2f -> 0.1f to 0
            else -> 0f to 0
        }

        if (random < chance) {
            val currentEffect = player.getEffect(ModEffects.BLEEDING)
            val currentLevel = currentEffect?.amplifier ?: -1

            if (level > currentLevel) {
                player.addEffect(
                    MobEffectInstance(
                        ModEffects.BLEEDING,
                        Int.MAX_VALUE,
                        level,
                        false,
                        true,
                        true
                    )
                )
            }
        }
    }

    @SubscribeEvent
    fun registerCommands(event: RegisterCommandsEvent) {
        event.dispatcher.register(
            Commands.literal("extdebug")
                .then(Commands.literal("check").executes { context ->
                    val player = context.source.playerOrException
                    val data = player.getData(ModDataAttachments.PLAYER_HEALTH)
                    context.source.sendSuccess({
                        Component.literal("當前部位血量 -> 頭: ${data.head}, 身: ${data.body}, 腳: ${data.legs}")
                    }, false)
                    1
                })
        )
    }
}
