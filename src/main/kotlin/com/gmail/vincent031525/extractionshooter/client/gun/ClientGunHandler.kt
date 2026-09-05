package com.gmail.vincent031525.extractionshooter.client.gun

import com.gmail.vincent031525.extractionshooter.datamap.GunStats
import com.gmail.vincent031525.extractionshooter.item.GunItem
import com.gmail.vincent031525.extractionshooter.item.MagazineItem
import com.gmail.vincent031525.extractionshooter.network.payload.ShootPayload
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import software.bernie.geckolib.animatable.GeoItem

object ClientGunHandler {

    private var wasAttackKeyDown = false
    private var nextShootTick = 0L
    private var burstRemaining = 0
    private var burstCooldown = 0

    private var recoilPitchVelocity = 0f
    private var recoilYawVelocity = 0f

    fun onClientTick() {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return

        // Update smooth recoil recovery
        updateRecoil(player)

        val stack = player.mainHandItem
        val gunItem = stack.item as? GunItem<*>
        if (gunItem == null) {
            wasAttackKeyDown = false
            burstRemaining = 0
            return
        }

        val level = player.level()
        val currentTick = level.gameTime
        val stats = gunItem.getGunStats()
        val gunData = GunItem.getGunData(stack)
        val fireMode = if (gunData != null && gunData.fireModeIndex in stats.fireModeCycle.indices) {
            stats.fireModeCycle[gunData.fireModeIndex]
        } else {
            GunStats.FireMode.SEMI
        }

        // Handle burst-fire continuation ticks
        if (burstRemaining > 0) {
            burstCooldown--
            if (burstCooldown <= 0) {
                shootOneBullet(player, gunItem, stack, stats)
                burstRemaining--
                burstCooldown = stats.shootTickDelay
            }
            return
        }

        // Only handle user input if not in a GUI
        if (minecraft.screen != null) {
            wasAttackKeyDown = false
            return
        }

        val isAttackDown = minecraft.options.keyAttack.isDown

        if (isAttackDown) {
            when (fireMode) {
                GunStats.FireMode.AUTO -> {
                    if (currentTick >= nextShootTick) {
                        shootOneBullet(player, gunItem, stack, stats)
                        nextShootTick = currentTick + stats.shootTickDelay
                    }
                }
                GunStats.FireMode.BURST -> {
                    if (!wasAttackKeyDown && currentTick >= nextShootTick) {
                        shootOneBullet(player, gunItem, stack, stats)
                        burstRemaining = 2
                        burstCooldown = stats.shootTickDelay
                        nextShootTick = currentTick + (stats.shootTickDelay * 3L) + 5L
                    }
                }
                GunStats.FireMode.SEMI -> {
                    if (!wasAttackKeyDown && currentTick >= nextShootTick) {
                        shootOneBullet(player, gunItem, stack, stats)
                        nextShootTick = currentTick + stats.shootTickDelay
                    }
                }
            }
        }

        wasAttackKeyDown = isAttackDown
    }

    private fun shootOneBullet(
        player: Player,
        gunItem: GunItem<*>,
        stack: ItemStack,
        stats: GunStats
    ) {
        val magazineStack = GunItem.getMagazineStack(stack)
        val magazineData = MagazineItem.getMagazineData(magazineStack)

        if (magazineData != null && magazineData.ammoCount > 0) {
            // 1. Instant local audio feedback
            val pitch = 1.9f + (Math.random().toFloat() * 0.2f)
            player.level().playLocalSound(
                player.x, player.y, player.z,
                SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.PLAYERS,
                1.0f,
                pitch,
                false
            )

            // 2. Instant local GeckoLib animation trigger
            val animId = GeoItem.getId(stack)
            if (animId != -1L) {
                gunItem.triggerAnim(player, animId, "shoot_controller", "fire")
            }

            // 3. Instant smooth recoil
            applyRecoil(player, stats)

            // 4. Send exact origin and look vector to server
            val origin = player.eyePosition
            val direction = player.lookAngle
            ClientPacketDistributor.sendToServer(ShootPayload(origin, direction))
        } else {
            // Instant dry-fire click sound
            player.level().playLocalSound(
                player.x, player.y, player.z,
                SoundEvents.DISPENSER_FAIL,
                SoundSource.PLAYERS,
                1.0f,
                1.5f,
                false
            )
            nextShootTick = player.level().gameTime + 10L
        }
    }

    private fun applyRecoil(player: Player, stats: GunStats) {
        val directPitch = stats.verticalRecoil * 0.65f
        player.xRot -= directPitch
        recoilPitchVelocity += (stats.verticalRecoil * 0.35f)

        val randomHorizontal = (Math.random().toFloat() * 2 - 1) * stats.horizontalRecoil
        player.yRot += randomHorizontal * 0.65f
        recoilYawVelocity += (randomHorizontal * 0.35f)
    }

    private fun updateRecoil(player: Player) {
        if (Math.abs(recoilPitchVelocity) > 0.005f) {
            player.xRot -= recoilPitchVelocity
            recoilPitchVelocity *= 0.6f
        } else {
            recoilPitchVelocity = 0f
        }

        if (Math.abs(recoilYawVelocity) > 0.005f) {
            player.yRot += recoilYawVelocity
            recoilYawVelocity *= 0.6f
        } else {
            recoilYawVelocity = 0f
        }
    }
}
