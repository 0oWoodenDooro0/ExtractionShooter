package com.gmail.vincent031525.extractionshooter.command

import com.gmail.vincent031525.extractionshooter.block.entity.LootCrateBlockEntity
import com.gmail.vincent031525.extractionshooter.datacomponent.GunData
import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import com.gmail.vincent031525.extractionshooter.registry.ModItems
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Container
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

object LootCommand {

    fun register(root: LiteralArgumentBuilder<CommandSourceStack>) {
        root.then(
            Commands.literal("loot")
                // /es loot fill [target]
                .then(
                    Commands.literal("fill")
                        .executes { executeFill(it, it.source.playerOrException) }
                        .then(
                            Commands.argument("target", EntityArgument.player())
                                .executes { executeFill(it, EntityArgument.getPlayer(it, "target")) }
                        )
                )
                // /es loot clear [target]
                .then(
                    Commands.literal("clear")
                        .executes { executeClear(it, it.source.playerOrException) }
                        .then(
                            Commands.argument("target", EntityArgument.player())
                                .executes { executeClear(it, EntityArgument.getPlayer(it, "target")) }
                        )
                )
        )
    }

    private fun executeFill(context: CommandContext<CommandSourceStack>, target: ServerPlayer): Int {
        val hit = target.pick(5.0, 0f, false)
        if (hit.type != HitResult.Type.BLOCK || hit !is BlockHitResult) {
            context.source.sendFailure(Component.literal("請將準心對準 5 格內的物資箱 (Loot Crate) 或容器方塊"))
            return 0
        }

        val level = target.level()
        val pos = hit.blockPos
        val blockEntity = level.getBlockEntity(pos)

        if (blockEntity is LootCrateBlockEntity) {
            var grid = GridInventory(9, 6)
            val itemsToFill = listOf(
                createM4A1(),
                createMagazine(ModItems.MAG_30_ITEM.get(), 30),
                createMagazine(ModItems.MAG_30_ITEM.get(), 30),
                createMagazine(ModItems.MAG_60_ITEM.get(), 60),
                ItemStack(ModItems.AMMO_556_ITEM.get(), 60),
                ItemStack(ModItems.AMMO_556_ITEM.get(), 60),
                ItemStack(ModItems.AMMO_556_ITEM.get(), 60),
                ItemStack(ModItems.MEDKIT_LARGE_ITEM.get()),
                ItemStack(ModItems.MEDKIT_SMALL_ITEM.get()),
                ItemStack(ModItems.SURGERY_KIT_ITEM.get()),
                ItemStack(ModItems.SPLINT_ITEM.get()),
                ItemStack(ModItems.BANDAGE_ITEM.get()),
                ItemStack(ModItems.PAINKILLERS_ITEM.get()),
                ItemStack(ModItems.RIG_ITEM.get()),
                ItemStack(ModItems.BACKPACK_ITEM.get())
            )

            for (stack in itemsToFill) {
                val space = grid.findSpaceForItem(stack)
                if (space != null) {
                    grid = grid.addItem(stack, space.first, space.second, false) ?: grid
                }
            }

            blockEntity.grid = grid
            blockEntity.setChanged()
            level.sendBlockUpdated(pos, blockEntity.blockState, blockEntity.blockState, 3)

            context.source.sendSuccess({
                Component.literal("已成功向戰術物資箱填入 ${grid.items.size} 件測試物品！")
                    .withStyle(ChatFormatting.GREEN)
            }, true)
            return 1
        }

        if (blockEntity is Container) {
            blockEntity.clearContent()
            val sampleItems = listOf(
                createM4A1(),
                createMagazine(ModItems.MAG_30_ITEM.get(), 30),
                ItemStack(ModItems.AMMO_556_ITEM.get(), 60),
                ItemStack(ModItems.MEDKIT_LARGE_ITEM.get()),
                ItemStack(ModItems.BANDAGE_ITEM.get()),
                ItemStack(ModItems.PAINKILLERS_ITEM.get())
            )
            for ((idx, stack) in sampleItems.withIndex()) {
                if (idx < blockEntity.containerSize) {
                    blockEntity.setItem(idx, stack)
                }
            }
            blockEntity.setChanged()
            level.sendBlockUpdated(pos, blockEntity.blockState, blockEntity.blockState, 3)

            context.source.sendSuccess({
                Component.literal("已成功向容器填入測試物品！").withStyle(ChatFormatting.GREEN)
            }, true)
            return 1
        }

        context.source.sendFailure(Component.literal("目標方塊不是物資箱或容器"))
        return 0
    }

    private fun executeClear(context: CommandContext<CommandSourceStack>, target: ServerPlayer): Int {
        val hit = target.pick(5.0, 0f, false)
        if (hit.type != HitResult.Type.BLOCK || hit !is BlockHitResult) {
            context.source.sendFailure(Component.literal("請將準心對準 5 格內的物資箱 (Loot Crate) 或容器方塊"))
            return 0
        }

        val level = target.level()
        val pos = hit.blockPos
        val blockEntity = level.getBlockEntity(pos)

        if (blockEntity is LootCrateBlockEntity) {
            blockEntity.grid = GridInventory(9, 6)
            blockEntity.setChanged()
            level.sendBlockUpdated(pos, blockEntity.blockState, blockEntity.blockState, 3)

            context.source.sendSuccess({
                Component.literal("已清空戰術物資箱！").withStyle(ChatFormatting.YELLOW)
            }, true)
            return 1
        }

        if (blockEntity is Container) {
            blockEntity.clearContent()
            blockEntity.setChanged()
            level.sendBlockUpdated(pos, blockEntity.blockState, blockEntity.blockState, 3)

            context.source.sendSuccess({
                Component.literal("已清空容器內容！").withStyle(ChatFormatting.YELLOW)
            }, true)
            return 1
        }

        context.source.sendFailure(Component.literal("目標方塊不是物資箱或容器"))
        return 0
    }

    private fun createMagazine(magItem: Item, count: Int): ItemStack {
        val stack = ItemStack(magItem)
        stack.set(ModDataComponents.MAGAZINE_DATA, MagazineData(count, ModItems.AMMO_556_ITEM.get()))
        return stack
    }

    private fun createM4A1(): ItemStack {
        val stack = ItemStack(ModItems.M4A1_ITEM.get())
        val mag = createMagazine(ModItems.MAG_30_ITEM.get(), 30)
        stack.set(ModDataComponents.GUN_DATA, GunData(magazineStack = mag))
        return stack
    }
}
