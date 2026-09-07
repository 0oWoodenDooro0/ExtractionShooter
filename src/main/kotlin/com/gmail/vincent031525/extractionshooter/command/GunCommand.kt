package com.gmail.vincent031525.extractionshooter.command

import com.gmail.vincent031525.extractionshooter.datacomponent.GunData
import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.item.GunItem
import com.gmail.vincent031525.extractionshooter.item.MagazineItem
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
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

object GunCommand {

    fun register(root: LiteralArgumentBuilder<CommandSourceStack>) {
        root.then(
            Commands.literal("gun")
                // /es gun refill [target]
                .then(
                    Commands.literal("refill")
                        .executes { executeRefill(it, it.source.playerOrException) }
                        .then(
                            Commands.argument("target", EntityArgument.player())
                                .executes { executeRefill(it, EntityArgument.getPlayer(it, "target")) }
                        )
                )
                // /es gun unload [target]
                .then(
                    Commands.literal("unload")
                        .executes { executeUnload(it, it.source.playerOrException) }
                        .then(
                            Commands.argument("target", EntityArgument.player())
                                .executes { executeUnload(it, EntityArgument.getPlayer(it, "target")) }
                        )
                )
                // /es gun info [target]
                .then(
                    Commands.literal("info")
                        .executes { executeInfo(it, it.source.playerOrException) }
                        .then(
                            Commands.argument("target", EntityArgument.player())
                                .executes { executeInfo(it, EntityArgument.getPlayer(it, "target")) }
                        )
                )
        )
    }

    private fun executeRefill(context: CommandContext<CommandSourceStack>, target: ServerPlayer): Int {
        val stack = target.mainHandItem
        if (stack.isEmpty) {
            context.source.sendFailure(Component.literal("主手必須手持槍械或彈匣"))
            return 0
        }

        if (stack.item is GunItem<*>) {
            val gunData = GunItem.getGunData(stack) ?: GunData()
            val mag = gunData.magazineStack

            val updatedMag = if (mag.isEmpty) {
                val newMag = ItemStack(ModItems.MAG_30_ITEM.get())
                newMag.set(ModDataComponents.MAGAZINE_DATA, MagazineData(30, ModItems.AMMO_556_ITEM.get()))
                newMag
            } else if (mag.item is MagazineItem) {
                val magItem = mag.item as MagazineItem
                val stats = magItem.getMagazineStats()
                val copyMag = mag.copy()
                copyMag.set(ModDataComponents.MAGAZINE_DATA, MagazineData(stats.maxAmmo, ModItems.AMMO_556_ITEM.get()))
                copyMag
            } else {
                mag
            }

            stack.set(ModDataComponents.GUN_DATA, gunData.copy(magazineStack = updatedMag))

            target.level().playSound(
                null,
                target.x, target.y, target.z,
                SoundEvents.ARMOR_EQUIP_GENERIC.value(),
                SoundSource.PLAYERS,
                1.0f,
                1.5f
            )

            context.source.sendSuccess({
                Component.literal("已為 ${target.name.string} 的主手槍械裝填滿彈彈匣！").withStyle(ChatFormatting.GREEN)
            }, true)
            return 1
        }

        if (stack.item is MagazineItem) {
            val stats = (stack.item as MagazineItem).getMagazineStats()
            stack.set(ModDataComponents.MAGAZINE_DATA, MagazineData(stats.maxAmmo, ModItems.AMMO_556_ITEM.get()))

            target.level().playSound(
                null,
                target.x, target.y, target.z,
                SoundEvents.ARMOR_EQUIP_GENERIC.value(),
                SoundSource.PLAYERS,
                1.0f,
                1.5f
            )

            context.source.sendSuccess({
                Component.literal("已將 ${target.name.string} 主手彈匣補滿 (${stats.maxAmmo} 發 5.56 彈藥)！")
                    .withStyle(ChatFormatting.GREEN)
            }, true)
            return 1
        }

        context.source.sendFailure(Component.literal("主手物品不是槍械或彈匣 (當前: ${stack.hoverName.string})"))
        return 0
    }

    private fun executeUnload(context: CommandContext<CommandSourceStack>, target: ServerPlayer): Int {
        val stack = target.mainHandItem
        if (stack.isEmpty) {
            context.source.sendFailure(Component.literal("主手必須手持槍械或彈匣"))
            return 0
        }

        if (stack.item is GunItem<*>) {
            val gunData = GunItem.getGunData(stack) ?: GunData()
            val mag = gunData.magazineStack
            if (mag.isEmpty) {
                context.source.sendFailure(Component.literal("這把槍目前沒有裝入彈匣"))
                return 0
            }

            stack.set(ModDataComponents.GUN_DATA, gunData.copy(magazineStack = ItemStack.EMPTY))

            if (!target.inventory.add(mag)) {
                target.drop(mag, false)
            }

            target.level().playSound(
                null,
                target.x, target.y, target.z,
                SoundEvents.ITEM_PICKUP,
                SoundSource.PLAYERS,
                1.0f,
                1.0f
            )

            context.source.sendSuccess({
                Component.literal("已卸下 ${target.name.string} 槍械中的彈匣 (${mag.hoverName.string})！")
                    .withStyle(ChatFormatting.YELLOW)
            }, true)
            return 1
        }

        if (stack.item is MagazineItem) {
            stack.set(ModDataComponents.MAGAZINE_DATA, MagazineData(0, Items.AIR))
            context.source.sendSuccess({
                Component.literal("已清空 ${target.name.string} 主手彈匣內的所有彈藥！").withStyle(ChatFormatting.YELLOW)
            }, true)
            return 1
        }

        context.source.sendFailure(Component.literal("主手物品不是槍械或彈匣"))
        return 0
    }

    private fun executeInfo(context: CommandContext<CommandSourceStack>, target: ServerPlayer): Int {
        val stack = target.mainHandItem
        if (stack.isEmpty) {
            context.source.sendFailure(Component.literal("主手必須手持槍械或彈匣"))
            return 0
        }

        if (stack.item is GunItem<*>) {
            val fireMode = GunItem.getFireMode(stack)
            val mag = GunItem.getMagazineStack(stack)
            val modeName = fireMode?.name ?: "未知"

            val msg = Component.empty()
                .append(Component.literal("===== 槍械狀態: [${stack.hoverName.string}] =====\n").withStyle(ChatFormatting.GOLD))
                .append(Component.literal("開火模式: $modeName\n").withStyle(ChatFormatting.AQUA))

            if (mag.isEmpty) {
                msg.append(Component.literal("彈匣: [未裝入彈匣]\n").withStyle(ChatFormatting.RED))
            } else {
                val magData = MagazineItem.getMagazineData(mag)
                val magStats = (mag.item as? MagazineItem)?.getMagazineStats()
                val count = magData?.ammoCount ?: 0
                val max = magStats?.maxAmmo ?: 0
                val ammoName = magData?.ammoItem?.name?.string ?: "無"

                msg.append(
                    Component.literal("彈匣: ${mag.hoverName.string} (子彈: $count / $max, 彈種: $ammoName)\n")
                        .withStyle(ChatFormatting.GREEN)
                )
            }

            context.source.sendSuccess({ msg }, false)
            return 1
        }

        if (stack.item is MagazineItem) {
            val magData = MagazineItem.getMagazineData(stack)
            val stats = (stack.item as MagazineItem).getMagazineStats()
            val count = magData?.ammoCount ?: 0
            val ammoName = magData?.ammoItem?.name?.string ?: "無"

            val msg = Component.empty()
                .append(Component.literal("===== 彈匣狀態: [${stack.hoverName.string}] =====\n").withStyle(ChatFormatting.GOLD))
                .append(Component.literal("容量: $count / ${stats.maxAmmo}\n").withStyle(ChatFormatting.GREEN))
                .append(Component.literal("裝填彈種: $ammoName\n").withStyle(ChatFormatting.WHITE))

            context.source.sendSuccess({ msg }, false)
            return 1
        }

        context.source.sendFailure(Component.literal("主手物品不是槍械或彈匣"))
        return 0
    }
}
