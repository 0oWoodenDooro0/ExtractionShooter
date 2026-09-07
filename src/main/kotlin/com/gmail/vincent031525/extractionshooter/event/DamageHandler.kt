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
import com.gmail.vincent031525.extractionshooter.util.HealthUtils
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent

@EventBusSubscriber(modid = Extractionshooter.ID)
object DamageHandler {

    data class BulletDamageReport(
        val part: BodyPart,
        val armorStack: ItemStack,
        val penetrationChance: Float,
        val isPenetrated: Boolean,
        val rawDamage: Float,
        val finalDamage: Float,
        val armorDurabilityDamage: Int,
        val remainingHealth: Float,
        val bleedingLevel: Int?
    )

    @SubscribeEvent
    fun onLivingDamage(event: LivingIncomingDamageEvent) {
        val player = event.entity
        if (player !is Player || player.level().isClientSide) return

        val source = event.source
        val damage = event.amount

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
            applyFinalDamage(player, part, damage)
        }

        val updatedHealth = player.getData(ModDataAttachments.PLAYER_HEALTH)
        if (!player.isCreative && (updatedHealth.head <= 0f || updatedHealth.body <= 0f)) {
            event.amount = Float.MAX_VALUE
            return
        }
    }

    fun handleArmorAndDamage(
        player: Player,
        part: BodyPart,
        ammo: AmmoStats,
        rawDamage: Float
    ): BulletDamageReport {
        val slot = when (part) {
            BodyPart.HEAD -> EquipmentSlot.HEAD
            BodyPart.BODY -> EquipmentSlot.CHEST
            else -> null
        }

        val armorStack = slot?.let { player.getItemBySlot(it) } ?: ItemStack.EMPTY
        val armorStats = if (armorStack.isEmpty) null else armorStack.itemHolder.getData(ModDataMaps.ARMOR_STATS)

        val durabilityPercent = if (armorStack.isEmpty || armorStack.maxDamage == 0) 1.0f
        else 1.0f - (armorStack.damageValue.toFloat() / armorStack.maxDamage.toFloat())

        val penPower = ammo.penetration

        if (armorStats == null || slot == null) {
            val bleeding = applyFinalDamage(player, part, rawDamage)
            val remHealth = player.getData(ModDataAttachments.PLAYER_HEALTH).getHealth(part)
            return BulletDamageReport(
                part = part,
                armorStack = ItemStack.EMPTY,
                penetrationChance = 1.0f,
                isPenetrated = true,
                rawDamage = rawDamage,
                finalDamage = rawDamage,
                armorDurabilityDamage = 0,
                remainingHealth = remHealth,
                bleedingLevel = bleeding
            )
        }

        val armorPotential = armorStats.armorClass * 10f
        val chance = calculatePenetrationChance(penPower, armorPotential, durabilityPercent)
        val isPenetrated = player.random.nextFloat() < chance

        val finalDamage: Float
        val armorDmg: Int

        if (isPenetrated) {
            val reduction = 0.8f
            finalDamage = rawDamage * reduction
            armorDmg = 1
            damageArmor(armorStack, player, slot, armorDmg)
            applyFinalDamage(player, part, finalDamage)
        } else {
            val bluntDamage = rawDamage * armorStats.bluntThroughput
            finalDamage = bluntDamage
            armorDmg = 2
            damageArmor(armorStack, player, slot, armorDmg)
            applyFinalDamage(player, part, finalDamage)
        }

        val bleeding = player.getEffect(ModEffects.BLEEDING)?.amplifier
        val remHealth = player.getData(ModDataAttachments.PLAYER_HEALTH).getHealth(part)

        return BulletDamageReport(
            part = part,
            armorStack = armorStack.copy(),
            penetrationChance = chance,
            isPenetrated = isPenetrated,
            rawDamage = rawDamage,
            finalDamage = finalDamage,
            armorDurabilityDamage = armorDmg,
            remainingHealth = remHealth,
            bleedingLevel = bleeding
        )
    }

    private fun calculatePenetrationChance(pen: Float, armor: Float, durability: Float): Float {
        val diff = pen - (armor * durability)
        return when {
            diff >= 10f -> 1.0f
            diff <= -10f -> 0.02f
            else -> (diff + 10f) / 20f
        }
    }

    fun applyFinalDamage(player: Player, part: BodyPart, damage: Float): Int? {
        val data = player.getData(ModDataAttachments.PLAYER_HEALTH)
        data.damage(part, damage)
        player.setData(ModDataAttachments.PLAYER_HEALTH, data)
        HealthUtils.syncHealth(player)

        // Visual & audio hurt feedback
        if (player is ServerPlayer) {
            player.connection.send(ClientboundHurtAnimationPacket(player))
        }
        player.level().playSound(
            null,
            player.x, player.y, player.z,
            SoundEvents.PLAYER_HURT,
            SoundSource.PLAYERS,
            1.0f,
            (player.random.nextFloat() - player.random.nextFloat()) * 0.2f + 1.0f
        )

        val bleedingLevel = tryApplyBleeding(player, damage)

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

        return bleedingLevel
    }

    private fun damageArmor(stack: ItemStack, player: Player, slot: EquipmentSlot, amount: Int) {
        if (!player.abilities.instabuild) {
            stack.hurtAndBreak(amount, player, slot)
        }
    }

    private fun tryApplyBleeding(player: Player, damage: Float): Int? {
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
                return level
            }
            return currentLevel
        }
        return null
    }

    fun executeBulletDamage(
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
