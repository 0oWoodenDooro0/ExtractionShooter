package com.gmail.vincent031525.extractionshooter.command

import com.gmail.vincent031525.extractionshooter.network.payload.SyncEquipmentPayload
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.PacketDistributor

object InvCommand {

    fun register(root: LiteralArgumentBuilder<CommandSourceStack>) {
        root.then(
            Commands.literal("inv")
                // /es inv clear <all|grid_id> [target]
                .then(
                    Commands.literal("clear")
                        .then(
                            Commands.argument("grid", StringArgumentType.word())
                                .suggests { ctx, builder ->
                                    builder.suggest("all")
                                    val player = try {
                                        EntityArgument.getPlayer(ctx, "target")
                                    } catch (e: Exception) {
                                        ctx.source.player
                                    }
                                    if (player != null) {
                                        val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
                                        equipment.getAllActiveGrids(player).keys.forEach { builder.suggest(it) }
                                    }
                                    builder.buildFuture()
                                }
                                .executes {
                                    executeClear(
                                        it,
                                        StringArgumentType.getString(it, "grid"),
                                        it.source.playerOrException
                                    )
                                }
                                .then(
                                    Commands.argument("target", EntityArgument.player())
                                        .executes {
                                            executeClear(
                                                it,
                                                StringArgumentType.getString(it, "grid"),
                                                EntityArgument.getPlayer(it, "target")
                                            )
                                        }
                                )
                        )
                )
                // /es inv dump [grid_id] [target]
                .then(
                    Commands.literal("dump")
                        .executes { executeDump(it, null, it.source.playerOrException) }
                        .then(
                            Commands.argument("grid", StringArgumentType.word())
                                .suggests { ctx, builder ->
                                    val player = try {
                                        EntityArgument.getPlayer(ctx, "target")
                                    } catch (e: Exception) {
                                        ctx.source.player
                                    }
                                    if (player != null) {
                                        val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
                                        equipment.getAllActiveGrids(player).keys.forEach { builder.suggest(it) }
                                    }
                                    builder.buildFuture()
                                }
                                .executes {
                                    executeDump(
                                        it,
                                        StringArgumentType.getString(it, "grid"),
                                        it.source.playerOrException
                                    )
                                }
                                .then(
                                    Commands.argument("target", EntityArgument.player())
                                        .executes {
                                            executeDump(
                                                it,
                                                StringArgumentType.getString(it, "grid"),
                                                EntityArgument.getPlayer(it, "target")
                                            )
                                        }
                                )
                        )
                )
        )
    }

    private fun executeClear(context: CommandContext<CommandSourceStack>, gridId: String, target: ServerPlayer): Int {
        val equipment = target.getData(ModDataAttachments.PLAYER_EQUIPMENT)

        if (gridId.equals("all", ignoreCase = true)) {
            equipment.persistentGrids.keys.forEach { key ->
                val g = equipment.persistentGrids[key]
                if (g != null) {
                    equipment.persistentGrids[key] = g.copy(items = emptyList())
                }
            }

            target.inventory.setItem(InventoryUtils.PRIMARY_1_SLOT, ItemStack.EMPTY)
            target.inventory.setItem(InventoryUtils.PRIMARY_2_SLOT, ItemStack.EMPTY)
            target.inventory.setItem(InventoryUtils.PISTOL_SLOT, ItemStack.EMPTY)
            InventoryUtils.syncHotbarSlot(target, InventoryUtils.PRIMARY_1_SLOT)
            InventoryUtils.syncHotbarSlot(target, InventoryUtils.PRIMARY_2_SLOT)
            InventoryUtils.syncHotbarSlot(target, InventoryUtils.PISTOL_SLOT)

            target.setData(ModDataAttachments.PLAYER_EQUIPMENT, equipment)
            PacketDistributor.sendToPlayer(target, SyncEquipmentPayload(equipment))
            target.containerMenu.broadcastChanges()

            context.source.sendSuccess({
                Component.literal("已清空 ${target.name.string} 的所有網格與裝備！").withStyle(ChatFormatting.GREEN)
            }, true)
            return 1
        }

        val allGrids = equipment.getAllActiveGrids(target)
        val grid = allGrids[gridId]
        if (grid == null) {
            context.source.sendFailure(Component.literal("找不到網格 '$gridId'"))
            return 0
        }

        val weaponSlot = InventoryUtils.getWeaponHotbarSlot(gridId)
        if (weaponSlot != null) {
            target.inventory.setItem(weaponSlot, ItemStack.EMPTY)
            InventoryUtils.syncHotbarSlot(target, weaponSlot)
            target.containerMenu.broadcastChanges()
        } else {
            val emptyGrid = grid.copy(items = emptyList())
            equipment.updateGrid(gridId, emptyGrid, target)
            target.setData(ModDataAttachments.PLAYER_EQUIPMENT, equipment)
            PacketDistributor.sendToPlayer(target, SyncEquipmentPayload(equipment))
        }

        context.source.sendSuccess({
            Component.literal("已清空 ${target.name.string} 的網格 [$gridId]").withStyle(ChatFormatting.GREEN)
        }, true)
        return 1
    }

    private fun executeDump(context: CommandContext<CommandSourceStack>, gridId: String?, target: ServerPlayer): Int {
        val equipment = target.getData(ModDataAttachments.PLAYER_EQUIPMENT)
        val allGrids = equipment.getAllActiveGrids(target)

        if (gridId == null) {
            val msg = Component.empty()
                .append(Component.literal("===== [${target.name.string}] 當前所有活躍網格 =====\n").withStyle(ChatFormatting.GOLD))
            for ((name, grid) in allGrids) {
                msg.append(
                    Component.literal(" - $name: ")
                        .withStyle(ChatFormatting.YELLOW)
                ).append(
                    Component.literal("${grid.items.size} 件物品 (尺寸: ${grid.columns}x${grid.rows})\n")
                        .withStyle(ChatFormatting.WHITE)
                )
            }
            context.source.sendSuccess({ msg }, false)
            return allGrids.size
        }

        val grid = allGrids[gridId]
        if (grid == null) {
            context.source.sendFailure(Component.literal("找不到網格 '$gridId'"))
            return 0
        }

        val filterName = grid.filter ?: "無"
        val msg = Component.empty()
            .append(Component.literal("===== 網格詳情: [$gridId] =====\n").withStyle(ChatFormatting.GOLD))
            .append(Component.literal("尺寸: ${grid.columns}x${grid.rows} | 過濾器: $filterName | 單格限制: ${grid.singleItem}\n").withStyle(ChatFormatting.GRAY))
            .append(Component.literal("物品清單 (${grid.items.size} 個):\n").withStyle(ChatFormatting.AQUA))

        for ((idx, item) in grid.items.withIndex()) {
            val rotatedStr = if (item.rotated) "[已旋轉 90°]" else ""
            msg.append(
                Component.literal("  [$idx] ${item.stack.hoverName.string} x${item.stack.count} ")
                    .withStyle(ChatFormatting.GREEN)
            ).append(
                Component.literal("@ (${item.x}, ${item.y}) $rotatedStr\n")
                    .withStyle(ChatFormatting.DARK_AQUA)
            )
        }

        context.source.sendSuccess({ msg }, false)
        return grid.items.size
    }
}
