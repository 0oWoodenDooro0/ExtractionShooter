package com.gmail.vincent031525.extractionshooter.command

import com.gmail.vincent031525.extractionshooter.dataattachment.PlayerHealth
import com.gmail.vincent031525.extractionshooter.health.BodyPart
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.registry.ModEffects
import com.gmail.vincent031525.extractionshooter.util.HealthUtils
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance

object HealthCommand {

    private val PART_SUGGESTIONS = listOf("head", "body", "legs", "all")
    private val DAMAGE_PART_SUGGESTIONS = listOf("head", "body", "legs")
    private val EFFECT_SUGGESTIONS = listOf("bleed", "fracture", "clear")

    fun register(root: LiteralArgumentBuilder<CommandSourceStack>) {
        root.then(
            Commands.literal("health")
                // /es health check [target]
                .then(
                    Commands.literal("check")
                        .executes { executeCheck(it, getTarget(it)) }
                        .then(
                            Commands.argument("target", EntityArgument.player())
                                .executes { executeCheck(it, EntityArgument.getPlayer(it, "target")) }
                        )
                )
                // /es health set <part> <amount> [target]
                .then(
                    Commands.literal("set")
                        .then(
                            Commands.argument("part", StringArgumentType.word())
                                .suggests { _, builder ->
                                    PART_SUGGESTIONS.forEach { builder.suggest(it) }
                                    builder.buildFuture()
                                }
                                .then(
                                    Commands.argument("amount", FloatArgumentType.floatArg(0f, 185f))
                                        .executes {
                                            executeSet(
                                                it,
                                                StringArgumentType.getString(it, "part"),
                                                FloatArgumentType.getFloat(it, "amount"),
                                                getTarget(it)
                                            )
                                        }
                                        .then(
                                            Commands.argument("target", EntityArgument.player())
                                                .executes {
                                                    executeSet(
                                                        it,
                                                        StringArgumentType.getString(it, "part"),
                                                        FloatArgumentType.getFloat(it, "amount"),
                                                        EntityArgument.getPlayer(it, "target")
                                                    )
                                                }
                                        )
                                )
                        )
                )
                // /es health damage <part> <amount> [target]
                .then(
                    Commands.literal("damage")
                        .then(
                            Commands.argument("part", StringArgumentType.word())
                                .suggests { _, builder ->
                                    DAMAGE_PART_SUGGESTIONS.forEach { builder.suggest(it) }
                                    builder.buildFuture()
                                }
                                .then(
                                    Commands.argument("amount", FloatArgumentType.floatArg(0f, 500f))
                                        .executes {
                                            executeDamage(
                                                it,
                                                StringArgumentType.getString(it, "part"),
                                                FloatArgumentType.getFloat(it, "amount"),
                                                getTarget(it)
                                            )
                                        }
                                        .then(
                                            Commands.argument("target", EntityArgument.player())
                                                .executes {
                                                    executeDamage(
                                                        it,
                                                        StringArgumentType.getString(it, "part"),
                                                        FloatArgumentType.getFloat(it, "amount"),
                                                        EntityArgument.getPlayer(it, "target")
                                                    )
                                                }
                                        )
                                )
                        )
                )
                // /es health heal <head|body|legs|all> [amount] [target]
                .then(
                    Commands.literal("heal")
                        .executes { executeHeal(it, "all", null, getTarget(it)) }
                        .then(
                            Commands.argument("part", StringArgumentType.word())
                                .suggests { _, builder ->
                                    PART_SUGGESTIONS.forEach { builder.suggest(it) }
                                    builder.buildFuture()
                                }
                                .executes {
                                    executeHeal(
                                        it,
                                        StringArgumentType.getString(it, "part"),
                                        null,
                                        getTarget(it)
                                    )
                                }
                                .then(
                                    Commands.argument("amount", FloatArgumentType.floatArg(0f, 185f))
                                        .executes {
                                            executeHeal(
                                                it,
                                                StringArgumentType.getString(it, "part"),
                                                FloatArgumentType.getFloat(it, "amount"),
                                                getTarget(it)
                                            )
                                        }
                                        .then(
                                            Commands.argument("target", EntityArgument.player())
                                                .executes {
                                                    executeHeal(
                                                        it,
                                                        StringArgumentType.getString(it, "part"),
                                                        FloatArgumentType.getFloat(it, "amount"),
                                                        EntityArgument.getPlayer(it, "target")
                                                    )
                                                }
                                        )
                                )
                        )
                )
                // /es health reset [target]
                .then(
                    Commands.literal("reset")
                        .executes { executeReset(it, getTarget(it)) }
                        .then(
                            Commands.argument("target", EntityArgument.player())
                                .executes { executeReset(it, EntityArgument.getPlayer(it, "target")) }
                        )
                )
                // /es health effect <bleed|fracture|clear> [level] [target]
                .then(
                    Commands.literal("effect")
                        .then(
                            Commands.argument("type", StringArgumentType.word())
                                .suggests { _, builder ->
                                    EFFECT_SUGGESTIONS.forEach { builder.suggest(it) }
                                    builder.buildFuture()
                                }
                                .executes {
                                    executeEffect(
                                        it,
                                        StringArgumentType.getString(it, "type"),
                                        0,
                                        getTarget(it)
                                    )
                                }
                                .then(
                                    Commands.argument("level", IntegerArgumentType.integer(0, 10))
                                        .executes {
                                            executeEffect(
                                                it,
                                                StringArgumentType.getString(it, "type"),
                                                IntegerArgumentType.getInteger(it, "level"),
                                                getTarget(it)
                                            )
                                        }
                                        .then(
                                            Commands.argument("target", EntityArgument.player())
                                                .executes {
                                                    executeEffect(
                                                        it,
                                                        StringArgumentType.getString(it, "type"),
                                                        IntegerArgumentType.getInteger(it, "level"),
                                                        EntityArgument.getPlayer(it, "target")
                                                    )
                                                }
                                        )
                                )
                        )
                )
        )
    }

    private fun getTarget(context: CommandContext<CommandSourceStack>): ServerPlayer {
        return context.source.playerOrException
    }

    private fun executeCheck(context: CommandContext<CommandSourceStack>, target: ServerPlayer): Int {
        val health = target.getData(ModDataAttachments.PLAYER_HEALTH)
        val bleedEffect = target.getEffect(ModEffects.BLEEDING)
        val hasFracture = target.hasEffect(ModEffects.FRACTURE)
        val hasPainkillers = target.hasEffect(ModEffects.ON_PAINKILLERS)

        val bleedStr = if (bleedEffect != null) "流血 (Lv.${bleedEffect.amplifier})" else "無"
        val fractureStr = if (hasFracture) "骨折" else "無"
        val painStr = if (hasPainkillers) "止痛效果中" else "無"

        val msg = Component.empty()
            .append(Component.literal("===== [${target.name.string}] 健康報告 =====\n").withStyle(ChatFormatting.GOLD))
            .append(Component.literal("頭部 (Head): ").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("${"%.1f".format(health.head)} / ${BodyPart.HEAD.maxHealth}\n").withStyle(ChatFormatting.RED))
            .append(Component.literal("胸部 (Body): ").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("${"%.1f".format(health.body)} / ${BodyPart.BODY.maxHealth}\n").withStyle(ChatFormatting.GREEN))
            .append(Component.literal("腿部 (Legs): ").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("${"%.1f".format(health.legs)} / ${BodyPart.LEGS.maxHealth}\n").withStyle(ChatFormatting.AQUA))
            .append(Component.literal("總血量: ").withStyle(ChatFormatting.YELLOW))
            .append(Component.literal("${"%.1f".format(health.getTotalHealth())} / ${health.getMaxTotalHealth()}\n").withStyle(ChatFormatting.WHITE))
            .append(Component.literal("異常狀態: 流血: $bleedStr | 骨折: $fractureStr | 止痛: $painStr").withStyle(ChatFormatting.LIGHT_PURPLE))

        context.source.sendSuccess({ msg }, false)
        return 1
    }

    private fun executeSet(
        context: CommandContext<CommandSourceStack>,
        partStr: String,
        amount: Float,
        target: ServerPlayer
    ): Int {
        val health = target.getData(ModDataAttachments.PLAYER_HEALTH)
        when (partStr.lowercase()) {
            "head" -> health.head = amount.coerceIn(0f, BodyPart.HEAD.maxHealth)
            "body" -> health.body = amount.coerceIn(0f, BodyPart.BODY.maxHealth)
            "legs" -> health.legs = amount.coerceIn(0f, BodyPart.LEGS.maxHealth)
            "all" -> {
                health.head = amount.coerceIn(0f, BodyPart.HEAD.maxHealth)
                health.body = amount.coerceIn(0f, BodyPart.BODY.maxHealth)
                health.legs = amount.coerceIn(0f, BodyPart.LEGS.maxHealth)
            }
            else -> {
                context.source.sendFailure(Component.literal("未知部位 '$partStr' (可選: head, body, legs, all)"))
                return 0
            }
        }

        target.setData(ModDataAttachments.PLAYER_HEALTH, health)
        HealthUtils.syncHealth(target)

        context.source.sendSuccess({
            Component.literal("已將 ${target.name.string} 的 $partStr 血量設為 $amount")
                .withStyle(ChatFormatting.GREEN)
        }, true)
        return 1
    }

    private fun executeDamage(
        context: CommandContext<CommandSourceStack>,
        partStr: String,
        amount: Float,
        target: ServerPlayer
    ): Int {
        val health = target.getData(ModDataAttachments.PLAYER_HEALTH)
        val part = when (partStr.lowercase()) {
            "head" -> BodyPart.HEAD
            "body" -> BodyPart.BODY
            "legs" -> BodyPart.LEGS
            else -> {
                context.source.sendFailure(Component.literal("未知部位 '$partStr' (可選: head, body, legs)"))
                return 0
            }
        }

        health.damage(part, amount)
        target.setData(ModDataAttachments.PLAYER_HEALTH, health)
        HealthUtils.syncHealth(target)

        target.connection.send(ClientboundHurtAnimationPacket(target))
        target.level().playSound(
            null,
            target.x, target.y, target.z,
            SoundEvents.PLAYER_HURT,
            SoundSource.PLAYERS,
            1.0f,
            1.0f
        )

        context.source.sendSuccess({
            Component.literal("對 ${target.name.string} 的 $partStr 造成 $amount 傷害 (剩餘: ${"%.1f".format(health.getHealth(part))})")
                .withStyle(ChatFormatting.RED)
        }, true)
        return 1
    }

    private fun executeHeal(
        context: CommandContext<CommandSourceStack>,
        partStr: String,
        amount: Float?,
        target: ServerPlayer
    ): Int {
        val health = target.getData(ModDataAttachments.PLAYER_HEALTH)
        when (partStr.lowercase()) {
            "head" -> health.heal(BodyPart.HEAD, amount ?: BodyPart.HEAD.maxHealth)
            "body" -> health.heal(BodyPart.BODY, amount ?: BodyPart.BODY.maxHealth)
            "legs" -> health.heal(BodyPart.LEGS, amount ?: BodyPart.LEGS.maxHealth)
            "all" -> {
                health.heal(BodyPart.HEAD, amount ?: BodyPart.HEAD.maxHealth)
                health.heal(BodyPart.BODY, amount ?: BodyPart.BODY.maxHealth)
                health.heal(BodyPart.LEGS, amount ?: BodyPart.LEGS.maxHealth)
            }
            else -> {
                context.source.sendFailure(Component.literal("未知部位 '$partStr' (可選: head, body, legs, all)"))
                return 0
            }
        }

        target.setData(ModDataAttachments.PLAYER_HEALTH, health)
        HealthUtils.syncHealth(target)

        context.source.sendSuccess({
            Component.literal("已治療 ${target.name.string} 的 $partStr (當前總血量: ${"%.1f".format(health.getTotalHealth())})")
                .withStyle(ChatFormatting.GREEN)
        }, true)
        return 1
    }

    private fun executeReset(context: CommandContext<CommandSourceStack>, target: ServerPlayer): Int {
        target.setData(ModDataAttachments.PLAYER_HEALTH, PlayerHealth())
        target.removeEffect(ModEffects.BLEEDING)
        target.removeEffect(ModEffects.FRACTURE)
        target.removeEffect(ModEffects.ON_PAINKILLERS)
        HealthUtils.syncHealth(target)

        context.source.sendSuccess({
            Component.literal("已完全重置 ${target.name.string} 的肢體血量與異常狀態！")
                .withStyle(ChatFormatting.GREEN)
        }, true)
        return 1
    }

    private fun executeEffect(
        context: CommandContext<CommandSourceStack>,
        type: String,
        level: Int,
        target: ServerPlayer
    ): Int {
        when (type.lowercase()) {
            "bleed" -> {
                target.addEffect(MobEffectInstance(ModEffects.BLEEDING, Int.MAX_VALUE, level.coerceIn(0, 1), false, true, true))
                context.source.sendSuccess({
                    Component.literal("已賦予 ${target.name.string} 流血效果 (等級: $level)")
                        .withStyle(ChatFormatting.RED)
                }, true)
            }
            "fracture" -> {
                target.addEffect(MobEffectInstance(ModEffects.FRACTURE, Int.MAX_VALUE, 0, false, true, true))
                context.source.sendSuccess({
                    Component.literal("已賦予 ${target.name.string} 骨折效果")
                        .withStyle(ChatFormatting.DARK_RED)
                }, true)
            }
            "clear" -> {
                target.removeEffect(ModEffects.BLEEDING)
                target.removeEffect(ModEffects.FRACTURE)
                target.removeEffect(ModEffects.ON_PAINKILLERS)
                context.source.sendSuccess({
                    Component.literal("已清除 ${target.name.string} 的所有自訂狀態效果")
                        .withStyle(ChatFormatting.GREEN)
                }, true)
            }
            else -> {
                context.source.sendFailure(Component.literal("未知效果類型 '$type' (可選: bleed, fracture, clear)"))
                return 0
            }
        }
        return 1
    }
}
