package com.gmail.vincent031525.extractionshooter.event

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.damagesource.BulletDamageSource
import com.gmail.vincent031525.extractionshooter.datamap.AmmoStats
import com.gmail.vincent031525.extractionshooter.health.BodyPart
import com.gmail.vincent031525.extractionshooter.health.LimbAABBHelper
import com.gmail.vincent031525.extractionshooter.registry.ModDamageTypes
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import com.gmail.vincent031525.extractionshooter.registry.ModEffects
import com.mojang.brigadier.arguments.FloatArgumentType
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent

@EventBusSubscriber(modid = Extractionshooter.ID)
object DamageHandler {

    @SubscribeEvent
    fun onLivingDamage(event: LivingIncomingDamageEvent) {
        val player = event.entity
        if (player !is Player || player.level().isClientSide) return

        val source = event.source
        val damage = event.amount
        val healthData = player.getData(ModDataAttachments.PLAYER_HEALTH)

        val hitVec = if (source is BulletDamageSource) source.hitPos else source.sourcePosition
        val part = when {
            source.`is`(DamageTypes.FALL) -> BodyPart.LEGS
            source.`is`(ModDamageTypes.BLEEDING) -> BodyPart.BODY
            else -> LimbAABBHelper.getTargetPart(player, hitVec)
        }

        event.amount = 0f

        if (part == null) return

        if (source is BulletDamageSource) {
            handleArmorAndDamage(player, part, source.stats, damage)
        } else {
            applyFinalDamage(player, BodyPart.BODY, damage)
        }

        if (healthData.head <= 0f || healthData.body <= 0f) {
            event.amount = Float.MAX_VALUE
            return
        }

    }

    private fun handleArmorAndDamage(player: Player, part: BodyPart, ammo: AmmoStats, rawDamage: Float) {
        val slot = when (part) {
            BodyPart.HEAD -> EquipmentSlot.HEAD
            BodyPart.BODY -> EquipmentSlot.CHEST
            else -> null
        }

        val armorStack = slot?.let { player.getItemBySlot(it) } ?: ItemStack.EMPTY
        val armorStats = if (armorStack.isEmpty) null else armorStack.itemHolder.getData(ModDataMaps.ARMOR_STATS)

        if (armorStats == null || slot == null) {
            applyFinalDamage(player, part, rawDamage)
            return
        }

        val durabilityPercent = 1.0f - (armorStack.damageValue.toFloat() / armorStack.maxDamage.toFloat())

        val armorPotential = armorStats.armorClass * 10f
        val penPower = ammo.penetration

        val chance = calculatePenetrationChance(penPower, armorPotential, durabilityPercent)
        val isPenetrated = player.random.nextFloat() < chance

        if (isPenetrated) {
            val reduction = 0.8f
            val finalDamage = rawDamage * reduction

            damageArmor(armorStack, player, slot, 1)

            applyFinalDamage(player, part, finalDamage)
        } else {
            val bluntDamage = rawDamage * armorStats.bluntThroughput

            damageArmor(armorStack, player, slot, 2)

            applyFinalDamage(player, part, bluntDamage)
        }
    }

    private fun calculatePenetrationChance(pen: Float, armor: Float, durability: Float): Float {
        val diff = pen - (armor * durability)
        return when {
            diff >= 10f -> 1.0f
            diff <= -10f -> 0.02f
            else -> (diff + 10f) / 20f
        }
    }

    private fun applyFinalDamage(player: Player, part: BodyPart, damage: Float) {
        val data = player.getData(ModDataAttachments.PLAYER_HEALTH)
        data.damage(part, damage)
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

    private fun damageArmor(stack: ItemStack, player: Player, slot: EquipmentSlot, amount: Int) {
        if (player !is Player || !player.abilities.instabuild) {
            stack.hurtAndBreak(amount, player, slot)
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
        event.dispatcher.register(
            Commands.literal("testdamage")
                .then(
                    Commands.argument("pos", Vec3Argument.vec3())
                        .then(
                            Commands.argument("penetration", FloatArgumentType.floatArg(0f, 100f))
                                .then(
                                    Commands.argument("damage", FloatArgumentType.floatArg(0f, 100f))
                                        .executes { context ->
                                            val pos = Vec3Argument.getVec3(context, "pos")
                                            val pen = FloatArgumentType.getFloat(context, "penetration")
                                            val dmg = FloatArgumentType.getFloat(context, "damage")

                                            val player = context.source.player ?: return@executes 0

                                            executeBulletDamage(player, pos, pen, dmg)
                                            1
                                        }
                                )
                        )
                )
        )
    }

    private fun executeBulletDamage(
        player: Player,
        hitPos: Vec3,
        pen: Float,
        dmg: Float
    ) {
        val level = player.level()
        val ammoStats = AmmoStats(dmg, pen)

        player.invulnerableTime = 0
        player.hurtServer(
            level as ServerLevel,
            BulletDamageSource(
                player.damageSources().damageTypes.getOrThrow(DamageTypes.PLAYER_ATTACK),
                ammoStats,
                player,
                hitPos
            ),
            ammoStats.damage
        )
    }
}
