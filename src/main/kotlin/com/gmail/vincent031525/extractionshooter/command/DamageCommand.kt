package com.gmail.vincent031525.extractionshooter.command

import com.gmail.vincent031525.extractionshooter.datamap.AmmoStats
import com.gmail.vincent031525.extractionshooter.event.DamageHandler
import com.gmail.vincent031525.extractionshooter.health.BodyPart
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.coordinates.Vec3Argument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3

object DamageCommand {

    private val PART_SUGGESTIONS = listOf("head", "body", "legs")

    fun register(root: LiteralArgumentBuilder<CommandSourceStack>) {
        root.then(
            Commands.literal("damage")
                // /es damage bullet <head|body|legs> <penetration> <damage> [target]
                .then(
                    Commands.literal("bullet")
                        .then(
                            Commands.argument("part", StringArgumentType.word())
                                .suggests { _, builder ->
                                    PART_SUGGESTIONS.forEach { builder.suggest(it) }
                                    builder.buildFuture()
                                }
                                .then(
                                    Commands.argument("penetration", FloatArgumentType.floatArg(0f, 100f))
                                        .then(
                                            Commands.argument("damage", FloatArgumentType.floatArg(0f, 200f))
                                                .executes {
                                                    executeBullet(
                                                        it,
                                                        StringArgumentType.getString(it, "part"),
                                                        FloatArgumentType.getFloat(it, "penetration"),
                                                        FloatArgumentType.getFloat(it, "damage"),
                                                        it.source.playerOrException
                                                    )
                                                }
                                                .then(
                                                    Commands.argument("target", EntityArgument.player())
                                                        .executes {
                                                            executeBullet(
                                                                it,
                                                                StringArgumentType.getString(it, "part"),
                                                                FloatArgumentType.getFloat(it, "penetration"),
                                                                FloatArgumentType.getFloat(it, "damage"),
                                                                EntityArgument.getPlayer(it, "target")
                                                            )
                                                        }
                                                )
                                        )
                                )
                        )
                )
                // /es damage pos <pos> <penetration> <damage> [target]
                .then(
                    Commands.literal("pos")
                        .then(
                            Commands.argument("pos", Vec3Argument.vec3())
                                .then(
                                    Commands.argument("penetration", FloatArgumentType.floatArg(0f, 100f))
                                        .then(
                                            Commands.argument("damage", FloatArgumentType.floatArg(0f, 200f))
                                                .executes {
                                                    executePos(
                                                        it,
                                                        Vec3Argument.getVec3(it, "pos"),
                                                        FloatArgumentType.getFloat(it, "penetration"),
                                                        FloatArgumentType.getFloat(it, "damage"),
                                                        it.source.playerOrException
                                                    )
                                                }
                                                .then(
                                                    Commands.argument("target", EntityArgument.player())
                                                        .executes {
                                                            executePos(
                                                                it,
                                                                Vec3Argument.getVec3(it, "pos"),
                                                                FloatArgumentType.getFloat(it, "penetration"),
                                                                FloatArgumentType.getFloat(it, "damage"),
                                                                EntityArgument.getPlayer(it, "target")
                                                            )
                                                        }
                                                )
                                        )
                                )
                        )
                )
        )
    }

    private fun executeBullet(
        context: CommandContext<CommandSourceStack>,
        partStr: String,
        penetration: Float,
        rawDamage: Float,
        target: ServerPlayer
    ): Int {
        val part = when (partStr.lowercase()) {
            "head" -> BodyPart.HEAD
            "body" -> BodyPart.BODY
            "legs" -> BodyPart.LEGS
            else -> {
                context.source.sendFailure(Component.literal("未知部位 '$partStr' (可選: head, body, legs)"))
                return 0
            }
        }

        val report = DamageHandler.handleArmorAndDamage(
            target,
            part,
            AmmoStats(rawDamage, penetration),
            rawDamage
        )

        val penChancePercent = (report.penetrationChance * 100).toInt()
        val armorDesc = if (report.armorStack.isEmpty) "無護甲" else "${report.armorStack.hoverName.string} (損耗: -${report.armorDurabilityDamage})"
        val penResult = if (report.isPenetrated) "§a穿透成功" else "§c護甲阻擋 (鈍擊)"

        val msg = Component.empty()
            .append(Component.literal("===== 子彈模擬結算 [${target.name.string}] =====\n").withStyle(ChatFormatting.GOLD))
            .append(Component.literal("命中部位: ").withStyle(ChatFormatting.GRAY)).append(Component.literal("${part.name}\n").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal("護甲防護: ").withStyle(ChatFormatting.GRAY)).append(Component.literal("$armorDesc\n").withStyle(ChatFormatting.AQUA))
            .append(Component.literal("穿透判定: ").withStyle(ChatFormatting.GRAY)).append(Component.literal("$penResult §7(機率: $penChancePercent%)\n"))
            .append(Component.literal("承受傷害: ").withStyle(ChatFormatting.GRAY)).append(Component.literal("${"%.1f".format(report.finalDamage)} §7(原始: ${"%.1f".format(report.rawDamage)})\n").withStyle(ChatFormatting.RED))
            .append(Component.literal("剩餘部位血量: ").withStyle(ChatFormatting.GRAY)).append(Component.literal("${"%.1f".format(report.remainingHealth)}\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("流血狀態: ").withStyle(ChatFormatting.GRAY)).append(Component.literal("${report.bleedingLevel?.let { "Lv.$it" } ?: "無"}\n").withStyle(ChatFormatting.LIGHT_PURPLE))

        context.source.sendSuccess({ msg }, false)
        return 1
    }

    private fun executePos(
        context: CommandContext<CommandSourceStack>,
        pos: Vec3,
        penetration: Float,
        damage: Float,
        target: ServerPlayer
    ): Int {
        DamageHandler.executeBulletDamage(target, pos, penetration, damage)
        context.source.sendSuccess({
            Component.literal("已在座標 ($pos) 對 ${target.name.string} 模擬子彈射擊 (穿透: $penetration, 傷害: $damage)")
                .withStyle(ChatFormatting.GREEN)
        }, true)
        return 1
    }
}
