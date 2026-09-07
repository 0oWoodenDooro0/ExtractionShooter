package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.damagesource.BulletDamageSource
import com.gmail.vincent031525.extractionshooter.datacomponent.GunData
import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.datamap.GunStats
import com.gmail.vincent031525.extractionshooter.datamap.MagazineStats
import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.*
import net.minecraft.world.item.component.AttackRange
import net.minecraft.world.item.component.PiercingWeapon
import net.minecraft.world.item.component.TooltipDisplay
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import software.bernie.geckolib.animatable.GeoAnimatable
import software.bernie.geckolib.animatable.GeoItem
import software.bernie.geckolib.animatable.client.GeoRenderProvider
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache
import software.bernie.geckolib.animatable.manager.AnimatableManager
import software.bernie.geckolib.animation.AnimationController
import software.bernie.geckolib.animation.RawAnimation
import software.bernie.geckolib.animation.`object`.PlayState
import software.bernie.geckolib.animation.state.AnimationTest
import software.bernie.geckolib.renderer.GeoItemRenderer
import software.bernie.geckolib.util.GeckoLibUtil
import java.util.*
import java.util.function.Consumer

class GunItem<T : GeoItemRenderer<*>>(
    properties: Properties,
    private val rendererFactory: () -> T
) : Item(
    properties.stacksTo(1).component(
        DataComponents.PIERCING_WEAPON,
        PiercingWeapon(
            false,
            false,
            Optional.empty(),
            Optional.empty()
        )
    ).component(
        DataComponents.ATTACK_RANGE, AttackRange(
            0f,
            0f,
            0f,
            0f,
            0f,
            0f
        )
    )
),
    GeoItem {

    private val cache: AnimatableInstanceCache = GeckoLibUtil.createInstanceCache(this)

    companion object {

        fun getGunData(stack: ItemStack): GunData? {
            return stack.get(ModDataComponents.GUN_DATA)
        }

        fun getMagazineStack(stack: ItemStack): ItemStack {
            return getGunData(stack)?.magazineStack ?: ItemStack.EMPTY
        }

        fun getFireMode(stack: ItemStack): GunStats.FireMode? {
            val data = getGunData(stack) ?: return null
            val stats = (stack.item as GunItem<*>).getGunStats()
            return stats.fireModeCycle[data.fireModeIndex]
        }

        fun nextFireMode(stack: ItemStack): Int {
            val data = getGunData(stack) ?: return -1
            val stats = (stack.item as GunItem<*>).getGunStats()
            val nextFireModeIndex = (data.fireModeIndex + 1) % stats.fireModeCycle.size
            stack.set(ModDataComponents.GUN_DATA, data.copy(fireModeIndex = nextFireModeIndex))
            return nextFireModeIndex
        }
    }

    init {
        GeoItem.registerSyncedAnimatable(this)
    }

    override fun getAnimatableInstanceCache(): AnimatableInstanceCache {
        return cache
    }

    override fun createGeoRenderer(consumer: Consumer<GeoRenderProvider>) {
        consumer.accept(object : GeoRenderProvider {
            private var renderer: T? = null

            override fun getGeoItemRenderer(): GeoItemRenderer<*> {
                return renderer ?: rendererFactory()
            }
        })
    }

    override fun registerControllers(controllers: AnimatableManager.ControllerRegistrar) {
        val controller =
            AnimationController("shoot_controller", 0) { state: AnimationTest<GeoAnimatable> -> PlayState.STOP }

        controller.animationSpeed = getGunStats().animationSpeed
        controller.triggerableAnim("fire", RawAnimation.begin().thenPlay("fire"))

        controllers.add(controller)
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
                val magazineStack = getMagazineStack(stack)
                val magazineData = MagazineItem.getMagazineData(magazineStack) ?: return
                val isCreative = entity.abilities.instabuild

                if (magazineData.ammoCount > 0 || isCreative) {
                    performShoot(level, entity, stack)
                    val newMagStack = if (!isCreative) {
                        val mag = magazineStack.copy()
                        val newAmmoCount = (magazineData.ammoCount - 1).coerceAtLeast(0)
                        mag.set(
                            ModDataComponents.MAGAZINE_DATA,
                            magazineData.copy(
                                ammoCount = newAmmoCount,
                                ammoItem = if (newAmmoCount == 0) Items.AIR else magazineData.ammoItem
                            )
                        )
                        mag
                    } else {
                        magazineStack
                    }
                    stack.set(
                        ModDataComponents.GUN_DATA, data.copy(
                            burstRemaining = data.burstRemaining - 1,
                            burstTickDelay = getGunStats().shootTickDelay,
                            magazineStack = newMagStack
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
        val data = getGunData(stack) ?: GunData()
        val magazineData = MagazineItem.getMagazineData(getMagazineStack(stack)) ?: MagazineData()
        tooltipAdder.accept(
            Component.literal("ammoCount ${magazineData.ammoCount}").withStyle(ChatFormatting.GRAY)
        )
        tooltipAdder.accept(
            Component.literal("FireMode ${getGunStats().fireModeCycle[data.fireModeIndex].name}")
                .withStyle(ChatFormatting.YELLOW)
        )
        tooltipAdder.accept(
            Component.literal("item ${data.magazineStack.item.name}").withStyle(ChatFormatting.YELLOW)
        )
    }

    override fun overrideOtherStackedOnMe(
        stack: ItemStack,
        other: ItemStack,
        slot: Slot,
        action: ClickAction,
        player: Player,
        access: SlotAccess
    ): Boolean {
        if (action == ClickAction.SECONDARY && !other.isEmpty) {
            val magazineStack = getMagazineStack(stack)
            if (!magazineStack.isEmpty) return false

            val loadedMag = loadMagazine(player.level(), stack, other)
            if (!loadedMag.isEmpty) {
                player.level().playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.ARMOR_EQUIP_GENERIC.value(),
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
                )
                return true
            }
            return false
        }

        if (action == ClickAction.SECONDARY && other.isEmpty) {
            val magazineStack = unloadMagazine(stack)
            if (magazineStack.isEmpty) return false

            access.set(magazineStack.copy())

            player.level()
                .playSound(null, player.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0f, 1.2f)
            return true
        }

        return false
    }

    fun getGunStats(): GunStats = BuiltInRegistries.ITEM.wrapAsHolder(this).getData(ModDataMaps.GUN_STATS)!!

    fun canShoot(level: Level, stack: ItemStack): Boolean {
        val gunData = getGunData(stack) ?: return false
        val magazineData = MagazineItem.getMagazineData(getMagazineStack(stack)) ?: return false
        if (level.gameTime < gunData.nextAttackTick) return false
        if (magazineData.ammoCount <= 0) return false
        return true
    }

    private fun performShoot(
        level: ServerLevel,
        player: Player,
        stack: ItemStack,
        clientOrigin: Vec3? = null,
        clientDir: Vec3? = null
    ) {
        val magazineStack = getMagazineStack(stack)
        val ammoItem = MagazineItem.getAmmoItem(magazineStack)
        if (ammoItem !is AmmoItem) return
        val ammoStats = ammoItem.getAmmoStats()

        triggerAnim(player, GeoItem.getOrAssignId(stack, level), "shoot_controller", "fire")

        val pitch = 1.9f + level.random.nextFloat() * 0.2f
        // Pass player to only play sound to surrounding players (shooter already played it locally)
        level.playSound(
            player,
            player.blockPosition(),
            SoundEvents.GENERIC_EXPLODE.value(),
            SoundSource.PLAYERS,
            1.0f,
            pitch
        )

        val eyePos = clientOrigin?.let {
            if (it.distanceToSqr(player.eyePosition) < 9.0) it else player.eyePosition
        } ?: player.eyePosition

        val lookVec = clientDir?.let {
            if (it.lengthSqr() > 0.001 && it.normalize().dot(player.lookAngle.normalize()) > 0.2) it.normalize() else player.lookAngle
        } ?: player.lookAngle
        val endPos = eyePos.add(lookVec.scale(getGunStats().range))
        val traceBox = AABB(eyePos, endPos)

        val entities = level.getEntities(player, traceBox) { entity ->
            entity is LivingEntity && entity != player
        }

        for (entity in entities) {
            val entityBox = entity.boundingBox
            val hitResult = entityBox.clip(eyePos, endPos)
            if (hitResult.isPresent) {
                val hitPos = hitResult.get()
                entity.invulnerableTime = 0
                entity.hurtServer(
                    level,
                    BulletDamageSource(
                        entity.damageSources().damageTypes.getOrThrow(DamageTypes.PLAYER_ATTACK),
                        ammoStats,
                        player,
                        hitPos
                    ),
                    ammoStats.damage
                )

                level.sendParticles(
                    net.minecraft.core.particles.ParticleTypes.CRIT,
                    hitPos.x, hitPos.y, hitPos.z, 5, 0.2, 0.2, 0.2, 0.0
                )
                break
            }
        }
    }

    fun tryShoot(
        level: ServerLevel,
        player: Player,
        stack: ItemStack,
        clientOrigin: Vec3? = null,
        clientDir: Vec3? = null
    ): Boolean {
        val gunData = getGunData(stack) ?: return false
        val magazineStack = getMagazineStack(stack)
        val magazineData = MagazineItem.getMagazineData(magazineStack) ?: return false

        // 1 tick tolerance for network latency between client and server
        if (level.gameTime < gunData.nextAttackTick - 1L) return false

        val isCreative = player.abilities.instabuild

        if (magazineData.ammoCount <= 0 && !isCreative) {
            level.playSound(
                player,
                player.blockPosition(),
                SoundEvents.DISPENSER_FAIL,
                SoundSource.PLAYERS,
                1.0f,
                1.5f
            )

            val cooldownData = gunData.copy(nextAttackTick = level.gameTime + 10)
            stack.set(ModDataComponents.GUN_DATA, cooldownData)
            return false
        }

        performShoot(level, player, stack, clientOrigin, clientDir)

        val gunStats = getGunStats()
        val nextTick = level.gameTime + gunStats.shootTickDelay
        val newData = if (gunStats.fireModeCycle[gunData.fireModeIndex] == GunStats.FireMode.BURST) {
            gunData.copy(
                burstRemaining = 2,
                burstTickDelay = gunStats.shootTickDelay,
                nextAttackTick = level.gameTime + (gunStats.shootTickDelay * 3L) + 5L
            )
        } else {
            gunData.copy(
                nextAttackTick = nextTick
            )
        }

        if (isCreative) {
            // Keep infinite ammo in creative mode while properly updating attack cooldown
            stack.set(ModDataComponents.GUN_DATA, newData)
            return true
        }

        val newAmmoCount = (magazineData.ammoCount - 1).coerceAtLeast(0)
        val updatedMagStack = magazineStack.copy()
        val updatedMagData = magazineData.copy(
            ammoCount = newAmmoCount,
            ammoItem = if (newAmmoCount == 0) Items.AIR else magazineData.ammoItem
        )
        updatedMagStack.set(ModDataComponents.MAGAZINE_DATA, updatedMagData)

        val finalGunData = newData.copy(magazineStack = updatedMagStack)
        stack.set(ModDataComponents.GUN_DATA, finalGunData)
        return true
    }

    fun loadMagazine(level: Level, stack: ItemStack, magazineStack: ItemStack): ItemStack {
        if (stack.isEmpty) return ItemStack.EMPTY
        if (magazineStack.isEmpty) return ItemStack.EMPTY
        val data = getGunData(stack) ?: GunData()
        val magazineStats = (magazineStack.item as? MagazineItem)?.getMagazineStats() ?: MagazineStats()
        val newMagazineStack = magazineStack.split(1)
        stack.set(
            ModDataComponents.GUN_DATA, data.copy(
                nextAttackTick = level.gameTime + magazineStats.reloadTick,
                magazineStack = newMagazineStack
            )
        )

        return newMagazineStack
    }

    fun unloadMagazine(stack: ItemStack): ItemStack {
        if (stack.isEmpty) return ItemStack.EMPTY
        val currentData = getGunData(stack) ?: return ItemStack.EMPTY
        val magazineStack = getMagazineStack(stack)
        if (magazineStack.isEmpty) return ItemStack.EMPTY

        stack.set(ModDataComponents.GUN_DATA, currentData.copy(magazineStack = ItemStack.EMPTY))

        return magazineStack
    }

    fun reload(player: Player, stack: ItemStack, magSlot: Int) {
        val magazineStack = player.inventory.getItem(magSlot)

        unloadMagazine(stack).let {
            if (it.isEmpty) return@let
            if (!player.inventory.add(it)) {
                player.drop(it, false)
            }
        }

        loadMagazine(player.level(), stack, magazineStack)

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
