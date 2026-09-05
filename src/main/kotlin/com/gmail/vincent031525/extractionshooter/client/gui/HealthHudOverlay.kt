package com.gmail.vincent031525.extractionshooter.client.gui

import com.gmail.vincent031525.extractionshooter.dataattachment.PlayerHealth
import com.gmail.vincent031525.extractionshooter.health.BodyPart
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.registry.ModEffects
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import kotlin.math.roundToInt

object HealthHudOverlay {

    fun render(guiGraphics: GuiGraphics) {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return

        if (mc.options.hideGui) return
        if (player.isSpectator) return

        val health = player.getData(ModDataAttachments.PLAYER_HEALTH)
        renderHealthCard(guiGraphics, 10, 10, health, player)
    }

    fun renderHealthCard(
        guiGraphics: GuiGraphics,
        startX: Int,
        startY: Int,
        health: PlayerHealth,
        player: net.minecraft.world.entity.player.Player
    ) {
        val mc = Minecraft.getInstance()
        val font = mc.font

        val cardWidth = 126
        val hasBleed = player.hasEffect(ModEffects.BLEEDING)
        val hasFracture = player.hasEffect(ModEffects.FRACTURE)
        val hasPainkillers = player.hasEffect(ModEffects.ON_PAINKILLERS)
        val hasAnyEffect = hasBleed || hasFracture || hasPainkillers
        val cardHeight = if (hasAnyEffect) 70 else 56

        // 1. Semi-transparent military dark background
        guiGraphics.fill(startX, startY, startX + cardWidth, startY + cardHeight, 0xCC11141A.toInt())
        // Border outline
        guiGraphics.renderOutline(startX, startY, cardWidth, cardHeight, 0xFF2F3542.toInt())

        // 2. Paperdoll Silhouette (Left side)
        val dollX = startX + 7
        val dollY = startY + 8

        val headColor = getPartColor(health.head, BodyPart.HEAD.maxHealth)
        val bodyColor = getPartColor(health.body, BodyPart.BODY.maxHealth)
        val legsColor = getPartColor(health.legs, BodyPart.LEGS.maxHealth)

        // Head box (12x10)
        guiGraphics.fill(dollX + 4, dollY, dollX + 16, dollY + 10, headColor)
        guiGraphics.renderOutline(dollX + 4, dollY, 12, 10, 0xFF1E272E.toInt())

        // Body box (16x15)
        guiGraphics.fill(dollX + 2, dollY + 12, dollX + 18, dollY + 27, bodyColor)
        guiGraphics.renderOutline(dollX + 2, dollY + 12, 16, 15, 0xFF1E272E.toInt())

        // Left & Right Legs (6x14 each)
        guiGraphics.fill(dollX + 3, dollY + 29, dollX + 9, dollY + 43, legsColor)
        guiGraphics.renderOutline(dollX + 3, dollY + 29, 6, 14, 0xFF1E272E.toInt())
        guiGraphics.fill(dollX + 11, dollY + 29, dollX + 17, dollY + 43, legsColor)
        guiGraphics.renderOutline(dollX + 11, dollY + 29, 6, 14, 0xFF1E272E.toInt())

        // 3. Stats details (Right side)
        val statsX = startX + 30
        val totalHp = health.getTotalHealth().roundToInt()
        val maxTotalHp = health.getMaxTotalHealth().roundToInt()

        // Header: Total HP
        val hpText = "HP $totalHp/$maxTotalHp"
        val headerColor = when {
            totalHp <= 0 -> 0xFFFF3333.toInt()
            totalHp < maxTotalHp * 0.4f -> 0xFFFFAA00.toInt()
            else -> 0xFFFFFFFF.toInt()
        }
        guiGraphics.drawString(font, hpText, statsX, startY + 5, headerColor, true)

        // Limb Bars
        val barWidth = 48
        val barHeight = 4

        // Head
        renderLimbRow(
            guiGraphics, font, "HD",
            health.head.roundToInt(), BodyPart.HEAD.maxHealth.roundToInt(),
            headColor, statsX, startY + 18, barWidth, barHeight
        )

        // Thorax / Body
        renderLimbRow(
            guiGraphics, font, "TH",
            health.body.roundToInt(), BodyPart.BODY.maxHealth.roundToInt(),
            bodyColor, statsX, startY + 29, barWidth, barHeight
        )

        // Legs
        renderLimbRow(
            guiGraphics, font, "LG",
            health.legs.roundToInt(), BodyPart.LEGS.maxHealth.roundToInt(),
            legsColor, statsX, startY + 40, barWidth, barHeight
        )

        // 4. Status Badges Row
        if (hasAnyEffect) {
            var badgeX = startX + 6
            val badgeY = startY + 56

            if (hasBleed) {
                val bleedEffect = player.getEffect(ModEffects.BLEEDING)
                val level = (bleedEffect?.amplifier ?: 0) + 1
                val text = if (level > 1) "BLEED II" else "BLEED"
                renderBadge(guiGraphics, font, text, badgeX, badgeY, 0xFFC0392B.toInt(), 0xFFFFFFFF.toInt())
                badgeX += font.width(text) + 8
            }

            if (hasFracture) {
                renderBadge(guiGraphics, font, "FRACTURE", badgeX, badgeY, 0xFFD35400.toInt(), 0xFFFFFFFF.toInt())
                badgeX += font.width("FRACTURE") + 8
            }

            if (hasPainkillers) {
                renderBadge(guiGraphics, font, "PK", badgeX, badgeY, 0xFF2980B9.toInt(), 0xFFFFFFFF.toInt())
                badgeX += font.width("PK") + 8
            }
        }
    }

    private fun renderLimbRow(
        guiGraphics: GuiGraphics,
        font: net.minecraft.client.gui.Font,
        name: String,
        current: Int,
        max: Int,
        color: Int,
        x: Int,
        y: Int,
        barW: Int,
        barH: Int
    ) {
        // Name
        guiGraphics.drawString(font, name, x, y, 0xFF888888.toInt(), false)

        // Mini bar
        val barX = x + 16
        val barY = y + 2
        guiGraphics.fill(barX, barY, barX + barW, barY + barH, 0xFF222222.toInt())
        val fillW = if (max > 0) ((current.toFloat() / max.toFloat()) * barW).roundToInt().coerceIn(0, barW) else 0
        if (fillW > 0) {
            guiGraphics.fill(barX, barY, barX + fillW, barY + barH, color)
        }

        // Value
        val valText = "$current"
        guiGraphics.drawString(font, valText, barX + barW + 4, y, color, false)
    }

    private fun renderBadge(
        guiGraphics: GuiGraphics,
        font: net.minecraft.client.gui.Font,
        text: String,
        x: Int,
        y: Int,
        bgColor: Int,
        textColor: Int
    ) {
        val textWidth = font.width(text)
        val pad = 3
        val badgeW = textWidth + pad * 2
        val badgeH = 10

        guiGraphics.fill(x, y, x + badgeW, y + badgeH, bgColor)
        guiGraphics.renderOutline(x, y, badgeW, badgeH, 0xFF000000.toInt())
        guiGraphics.drawString(font, text, x + pad, y + 1, textColor, false)
    }

    fun getPartColor(current: Float, max: Float): Int {
        if (current <= 0f) return 0xFF1E272E.toInt() // Blacked out / Destroyed
        val ratio = (current / max).coerceIn(0f, 1f)
        return when {
            ratio > 0.75f -> 0xFF2ECC71.toInt() // Healthy Green
            ratio > 0.40f -> 0xFFF1C40F.toInt() // Injured Yellow
            ratio > 0.15f -> 0xFFE67E22.toInt() // Critical Orange
            else -> 0xFFE74C3C.toInt()          // Heavy Damage Red
        }
    }
}
