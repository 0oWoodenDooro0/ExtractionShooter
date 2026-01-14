package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.datacomponent.GunData
import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUseAnimation
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.AttackRange
import net.minecraft.world.item.component.PiercingWeapon
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import software.bernie.geckolib.animatable.GeoItem
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.util.GeckoLibUtil
import java.util.*
import java.util.function.Consumer

abstract class GunItem(properties: Properties) : Item(
    properties.stacksTo(1).component(
        DataComponents.PIERCING_WEAPON,
        PiercingWeapon(false, false, Optional.empty(), Optional.empty())
    ).component(DataComponents.ATTACK_RANGE, AttackRange(0f, 0f, 0f, 0f, 0f, 0f))
),
    GeoItem {

    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)
    abstract val gunStats: GunStats

    init {
        GeoItem.registerSyncedAnimatable(this)
    }

    data class GunStats(
        val damage: Float,
        val rps: Int,
        val range: Double,
        val verticalRecoil: Float,
        val horizontalRecoil: Float
    ) {
        val shootTickDelay: Int
            get() = if (rps > 0) (20 / rps).coerceAtLeast(1) else 20
        val animationSpeed: Double
            get() = 20.0 / shootTickDelay.toDouble()
    }

    override fun use(level: Level, player: Player, hand: InteractionHand): InteractionResult {
        return InteractionResult.PASS
    }

    override fun canDestroyBlock(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        entity: LivingEntity
    ): Boolean {
        return false
    }

    override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack, slotChanged: Boolean): Boolean {
        return slotChanged
    }

    override fun getUseAnimation(stack: ItemStack): ItemUseAnimation {
        return ItemUseAnimation.NONE
    }

    override fun mineBlock(
        stack: ItemStack,
        level: Level,
        state: BlockState,
        pos: BlockPos,
        miningEntity: LivingEntity
    ): Boolean {
        return false
    }

    override fun onEntitySwing(stack: ItemStack, entity: LivingEntity, hand: InteractionHand): Boolean {
        return true
    }

    override fun onLeftClickEntity(stack: ItemStack, player: Player, entity: Entity): Boolean {
        return true
    }

    override fun inventoryTick(stack: ItemStack, level: ServerLevel, entity: Entity, slot: EquipmentSlot?) {
        if (entity !is Player) return
        val data = stack.get(ModDataComponents.GUN_DATA) ?: return

        if (data.burstRemaining > 0) {
            if (data.burstTickDelay <= 0) {
                val magazineData = stack.getMagazineData() ?: return
                val magazineStack = stack.getMagazineStack()
                if (magazineData.ammoCount > 0) {
                    performShoot(level, entity, stack)
                    magazineStack.set(
                        ModDataComponents.MAGAZINE_DATA,
                        magazineData.copy(ammoCount = magazineData.ammoCount - 1)
                    )
                    stack.set(
                        ModDataComponents.GUN_DATA, data.copy(
                            burstRemaining = data.burstRemaining - 1,
                            burstTickDelay = gunStats.shootTickDelay
                        )
                    )
                } else {
                    stack.set(ModDataComponents.GUN_DATA, data.copy(burstRemaining = 0))
                }
            } else {
                stack.set(ModDataComponents.GUN_DATA, data.copy(burstTickDelay = data.burstTickDelay - 1))
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipDisplay: TooltipDisplay,
        tooltipAdder: Consumer<Component>,
        flag: TooltipFlag
    ) {
        val data = stack.getGunData() ?: GunData()
        val magazineData = stack.getMagazineData() ?: MagazineData()
        tooltipAdder.accept(
            Component.literal("ammoCount ${magazineData.ammoCount}").withStyle(ChatFormatting.GRAY)
        )
        tooltipAdder.accept(
            Component.literal("FireMode ${data.fireMode.name}")
                .withStyle(ChatFormatting.YELLOW)
        )
        tooltipAdder.accept(
            Component.literal("item ${data.magazineStack.item.name}").withStyle(ChatFormatting.YELLOW)
        )
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache {
        return cache
    }

    fun canShoot(level: Level, stack: ItemStack): Boolean {
        val gunData = stack.getGunData() ?: return false
        val magazineData = stack.getMagazineData() ?: return false
        if (level.gameTime < gunData.nextAttackTick) return false
        if (magazineData.ammoCount <= 0) return false
        return true
    }

    private fun performShoot(level: ServerLevel, player: Player, stack: ItemStack) {
        triggerAnim(player, GeoItem.getOrAssignId(stack, level), "shoot_controller", "fire")

        val pitch = 1.9f + level.random.nextFloat() * 0.2f
        level.playSound(
            null,
            player.blockPosition(),
            SoundEvents.GENERIC_EXPLODE.value(),
            SoundSource.PLAYERS,
            1.0f,
            pitch
        )

        val eyePos = player.eyePosition
        val lookVec = player.lookAngle
        val endPos = eyePos.add(lookVec.scale(gunStats.range))
        val traceBox = AABB(eyePos, endPos)

        val entities = level.getEntities(player, traceBox) { entity ->
            entity is LivingEntity && entity != player
        }

        for (entity in entities) {
            val entityBox = entity.boundingBox
            if (entityBox.clip(eyePos, endPos).isPresent) {
                entity.invulnerableTime = 0
                entity.hurtServer(level, player.damageSources().playerAttack(player), gunStats.damage)

                level.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.CRIT,
                    entity.x, entity.y + entity.eyeHeight, entity.z, 5, 0.2, 0.2, 0.2, 0.0
                )
                break
            }
        }
    }

    fun tryShoot(level: ServerLevel, player: Player, stack: ItemStack) {

        val gunData = stack.getGunData() ?: return
        val magazineStack = stack.getMagazineStack()
        val magazineData = stack.getMagazineData() ?: return

        if (level.gameTime < gunData.nextAttackTick) return

        if (magazineData.ammoCount <= 0) {
            level.playSound(
                null,
                player.blockPosition(),
                SoundEvents.DISPENSER_FAIL,
                SoundSource.PLAYERS,
                1.0f,
                1.5f
            )

            val cooldownData = gunData.copy(nextAttackTick = level.gameTime + 10)
            stack.set(ModDataComponents.GUN_DATA, cooldownData)
            return
        }

        performShoot(level, player, stack)

        val newData = if (gunData.fireMode == GunData.FireMode.BURST) {
            gunData.copy(
                burstRemaining = 2,
                burstTickDelay = gunStats.shootTickDelay,
                nextAttackTick = level.gameTime + (gunStats.shootTickDelay * 3L) + 5L
            )
        } else {
            gunData.copy(
                nextAttackTick = level.gameTime + gunStats.shootTickDelay
            )
        }

        magazineStack.set(ModDataComponents.MAGAZINE_DATA, magazineData.copy(ammoCount = magazineData.ammoCount - 1))
        stack.set(ModDataComponents.GUN_DATA, newData)
    }

    fun unload(player: Player, stack: ItemStack) {
        val currentData = stack.getGunData() ?: return
        val magazineStack = stack.getMagazineStack()
        val magazineData = stack.getMagazineData() ?: return
        if (magazineStack.isEmpty) return

        magazineStack[ModDataComponents.MAGAZINE_DATA] = MagazineData(ammoCount = magazineData.ammoCount)

        stack.set(ModDataComponents.GUN_DATA, currentData.copy(magazineStack = ItemStack.EMPTY))

        if (!player.inventory.add(magazineStack)) {
            player.drop(magazineStack, false)
        }
    }

    fun reload(player: Player, gunStack: ItemStack, magSlot: Int) {
        val gunData = gunStack.getGunData() ?: return
        val magazineStack = player.inventory.getItem(magSlot)
        val magazineStats = (magazineStack.item as MagazineItem).magazineStats

        unload(player, gunStack)

        gunStack.set(
            ModDataComponents.GUN_DATA, gunData.copy(
                nextAttackTick = player.level().gameTime + magazineStats.reloadTick,
                magazineStack = magazineStack.copy()
            )
        )

        magazineStack.shrink(1)

        player.level().playSound(
            null,
            player.blockPosition(),
            SoundEvents.ARMOR_EQUIP_GENERIC.value(),
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        )
    }
}
