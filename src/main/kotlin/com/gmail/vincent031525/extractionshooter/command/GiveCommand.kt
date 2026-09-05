package com.gmail.vincent031525.extractionshooter.command

import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import com.gmail.vincent031525.extractionshooter.network.payload.SyncEquipmentPayload
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
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
import net.minecraft.world.item.ItemStack
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
                                        equipment.getAllActiveGrids().keys.forEach { builder.suggest(it) }
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
        val allGrids = equipment.getAllActiveGrids()
        val grid = allGrids[gridId]

        if (grid == null) {
            context.source.sendFailure(Component.literal("Grid '$gridId' not found"))
            return 0
        }

        var addedCount = 0
        var totalRemaining = amount
        var currentGrid: GridInventory = grid

        // 1. If item is stackable and not a singleItem slot, attempt to merge into existing matching stacks first
        if (stackPrototype.isStackable && !currentGrid.singleItem) {
            val updatedItems = currentGrid.items.toMutableList()
            for (i in updatedItems.indices) {
                if (totalRemaining <= 0) break
                val inst = updatedItems[i]
                if (ItemStack.isSameItemSameComponents(inst.stack, stackPrototype)) {
                    val maxStack = inst.stack.maxStackSize
                    val currentCount = inst.stack.count
                    val space = maxStack - currentCount
                    if (space > 0) {
                        val toAdd = minOf(space, totalRemaining)
                        val newStack = inst.stack.copy()
                        newStack.grow(toAdd)
                        updatedItems[i] = inst.copy(stack = newStack)
                        addedCount += toAdd
                        totalRemaining -= toAdd
                    }
                }
            }
            if (addedCount > 0) {
                currentGrid = currentGrid.copy(items = updatedItems)
            }
        }

        // 2. Place remaining amount into empty spaces in stacks up to maxStackSize
        while (totalRemaining > 0) {
            val stackSize = if (stackPrototype.isStackable && !currentGrid.singleItem) {
                minOf(totalRemaining, stackPrototype.maxStackSize)
            } else {
                1
            }

            val newStack = itemInput.createItemStack(stackSize, false)
            val space = currentGrid.findSpaceForItem(newStack) ?: break
            val (x, y) = space
            val nextGrid = currentGrid.addItem(newStack, x, y, false) ?: break

            currentGrid = nextGrid
            addedCount += stackSize
            totalRemaining -= stackSize
        }

        if (addedCount > 0) {
            equipment.updateGrid(gridId, currentGrid)
            player.setData(ModDataAttachments.PLAYER_EQUIPMENT, equipment)
            InventoryUtils.syncHotbarWithEquipment(player, equipment)
            PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(equipment))

            context.source.sendSuccess({
                Component.literal("Added $addedCount x ${stackPrototype.hoverName.string} to $gridId")
            }, true)
        } else {
            context.source.sendFailure(Component.literal("No space in grid $gridId"))
        }

        return addedCount
    }
}
