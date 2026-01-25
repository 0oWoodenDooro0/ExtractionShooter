package com.gmail.vincent031525.extractionshooter.command

import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.network.payload.SyncEquipmentPayload
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.item.ItemArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor

object GiveCommand {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>, context: CommandBuildContext) {
        dispatcher.register(
            Commands.literal("es")
                .then(Commands.literal("give")
                    .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("item", ItemArgument.item(context))
                            .then(Commands.argument("grid", StringArgumentType.word())
                                .suggests { ctx, builder ->
                                    val player = try { EntityArgument.getPlayer(ctx, "target") } catch (e: Exception) { null }
                                    if (player != null) {
                                        val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
                                        equipment.persistentGrids.keys.forEach { builder.suggest(it) }
                                    }
                                    builder.buildFuture()
                                }
                                .executes { execute(it, 1) }
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                    .executes { execute(it, IntegerArgumentType.getInteger(it, "amount")) }
                                )
                            )
                        )
                    )
                )
        )
    }

    private fun execute(context: CommandContext<CommandSourceStack>, amount: Int): Int {
        val player = EntityArgument.getPlayer(context, "target")
        val itemInput = ItemArgument.getItem(context, "item")
        val gridId = StringArgumentType.getString(context, "grid")
        val stackPrototype = itemInput.createItemStack(1, false)
        
        val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
        val grid = equipment.persistentGrids[gridId]
        
        if (grid == null) {
            context.source.sendFailure(Component.literal("Grid '$gridId' not found"))
            return 0
        }
        
        var addedCount = 0
        var currentGrid: GridInventory = grid
        
        for (i in 0 until amount) {
            val space = currentGrid.findSpaceForItem(stackPrototype)
            if (space != null) {
                val (x, y) = space
                val nextGrid = currentGrid.addItem(stackPrototype.copy(), x, y, false)
                if (nextGrid != null) {
                    currentGrid = nextGrid
                    addedCount++
                }
            } else {
                break
            }
        }
        
        if (addedCount > 0) {
            equipment.updateGrid(gridId, currentGrid)
            player.setData(ModDataAttachments.PLAYER_EQUIPMENT, equipment)
            PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(equipment))
            context.source.sendSuccess({ Component.literal("Added $addedCount x ${stackPrototype.hoverName.string} to $gridId") }, true)
        } else {
            context.source.sendFailure(Component.literal("No space in grid $gridId"))
        }
        
        return addedCount
    }
}