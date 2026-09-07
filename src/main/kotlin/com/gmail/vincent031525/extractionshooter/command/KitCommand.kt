package com.gmail.vincent031525.extractionshooter.command

import com.gmail.vincent031525.extractionshooter.datacomponent.GunData
import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import com.gmail.vincent031525.extractionshooter.inventory.GridItemInstance
import com.gmail.vincent031525.extractionshooter.inventory.PlayerEquipment
import com.gmail.vincent031525.extractionshooter.network.payload.SyncEquipmentPayload
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import com.gmail.vincent031525.extractionshooter.registry.ModItems
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
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.PacketDistributor

object KitCommand {

    private val KIT_SUGGESTIONS = listOf("combat", "medic", "clear")

    fun register(root: LiteralArgumentBuilder<CommandSourceStack>) {
        root.then(
            Commands.literal("kit")
                .then(
                    Commands.argument("type", StringArgumentType.word())
                        .suggests { _, builder ->
                            KIT_SUGGESTIONS.forEach { builder.suggest(it) }
                            builder.buildFuture()
                        }
                        .executes { executeKit(it, StringArgumentType.getString(it, "type"), it.source.playerOrException) }
                        .then(
                            Commands.argument("target", EntityArgument.player())
                                .executes {
                                    executeKit(
                                        it,
                                        StringArgumentType.getString(it, "type"),
                                        EntityArgument.getPlayer(it, "target")
                                    )
                                }
                        )
                )
        )
    }

    private fun executeKit(context: CommandContext<CommandSourceStack>, kitType: String, target: ServerPlayer): Int {
        when (kitType.lowercase()) {
            "combat" -> applyCombatKit(target)
            "medic" -> applyMedicKit(target)
            "clear" -> applyClearKit(target)
            else -> {
                context.source.sendFailure(Component.literal("未知套裝類型 '$kitType' (可選: combat, medic, clear)"))
                return 0
            }
        }

        context.source.sendSuccess({
            Component.literal("已為 ${target.name.string} 裝備 '$kitType' 測試套裝！")
                .withStyle(ChatFormatting.GREEN)
        }, true)
        return 1
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

    private fun addItemsToGrid(initialGrid: GridInventory, items: List<ItemStack>): GridInventory {
        var grid = initialGrid
        for (stack in items) {
            val space = grid.findSpaceForItem(stack)
            if (space != null) {
                grid = grid.addItem(stack, space.first, space.second, false) ?: grid
            }
        }
        return grid
    }

    private fun applyCombatKit(player: ServerPlayer) {
        val equipment = PlayerEquipment()

        // 1. Tactical Rig (3x3)
        val rigStack = ItemStack(ModItems.RIG_ITEM.get())
        var rigGrid = GridInventory(3, 3)
        rigGrid = addItemsToGrid(
            rigGrid,
            listOf(
                createMagazine(ModItems.MAG_30_ITEM.get(), 30),
                createMagazine(ModItems.MAG_30_ITEM.get(), 30),
                ItemStack(ModItems.AMMO_556_ITEM.get(), 60),
                ItemStack(ModItems.BANDAGE_ITEM.get()),
                ItemStack(ModItems.PAINKILLERS_ITEM.get())
            )
        )
        rigStack.set(ModDataComponents.GRID_INVENTORY, rigGrid)
        equipment.persistentGrids["tactical_rig"] =
            GridInventory(2, 2, listOf(GridItemInstance(rigStack, 0, 0, false)), filter = "tactical_rig", singleItem = true)

        // 2. Backpack (5x5)
        val backpackStack = ItemStack(ModItems.BACKPACK_ITEM.get())
        var backpackGrid = GridInventory(5, 5)
        backpackGrid = addItemsToGrid(
            backpackGrid,
            listOf(
                ItemStack(ModItems.MEDKIT_LARGE_ITEM.get()),
                ItemStack(ModItems.SURGERY_KIT_ITEM.get()),
                ItemStack(ModItems.SPLINT_ITEM.get()),
                createMagazine(ModItems.MAG_60_ITEM.get(), 60),
                ItemStack(ModItems.AMMO_556_ITEM.get(), 60),
                ItemStack(ModItems.AMMO_556_ITEM.get(), 60)
            )
        )
        backpackStack.set(ModDataComponents.GRID_INVENTORY, backpackGrid)
        equipment.persistentGrids["backpack"] =
            GridInventory(2, 2, listOf(GridItemInstance(backpackStack, 0, 0, false)), filter = "backpack", singleItem = true)

        // 3. Secure Container (3x3)
        var secureGrid = GridInventory(3, 3)
        secureGrid = addItemsToGrid(
            secureGrid,
            listOf(
                ItemStack(ModItems.SURGERY_KIT_ITEM.get()),
                ItemStack(ModItems.AMMO_556_ITEM.get(), 60)
            )
        )
        equipment.persistentGrids["secure_container"] = secureGrid

        // 4. Primary weapon (M4A1 in hotbar slot 0)
        val m4a1 = createM4A1()
        player.inventory.setItem(InventoryUtils.PRIMARY_1_SLOT, m4a1)
        player.inventory.setItem(InventoryUtils.PRIMARY_2_SLOT, ItemStack.EMPTY)
        player.inventory.setItem(InventoryUtils.PISTOL_SLOT, ItemStack.EMPTY)
        InventoryUtils.syncHotbarSlot(player, InventoryUtils.PRIMARY_1_SLOT)
        InventoryUtils.syncHotbarSlot(player, InventoryUtils.PRIMARY_2_SLOT)
        InventoryUtils.syncHotbarSlot(player, InventoryUtils.PISTOL_SLOT)

        // Sync equipment
        player.setData(ModDataAttachments.PLAYER_EQUIPMENT, equipment)
        PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(equipment))
        player.containerMenu.broadcastChanges()
    }

    private fun applyMedicKit(player: ServerPlayer) {
        val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)

        val backpackStack = ItemStack(ModItems.BACKPACK_ITEM.get())
        var backpackGrid = GridInventory(5, 5)
        backpackGrid = addItemsToGrid(
            backpackGrid,
            listOf(
                ItemStack(ModItems.MEDKIT_LARGE_ITEM.get()),
                ItemStack(ModItems.MEDKIT_LARGE_ITEM.get()),
                ItemStack(ModItems.MEDKIT_SMALL_ITEM.get()),
                ItemStack(ModItems.MEDKIT_SMALL_ITEM.get()),
                ItemStack(ModItems.SURGERY_KIT_ITEM.get()),
                ItemStack(ModItems.SURGERY_KIT_ITEM.get()),
                ItemStack(ModItems.BANDAGE_ITEM.get()),
                ItemStack(ModItems.BANDAGE_ITEM.get()),
                ItemStack(ModItems.SPLINT_ITEM.get()),
                ItemStack(ModItems.SPLINT_ITEM.get()),
                ItemStack(ModItems.PAINKILLERS_ITEM.get()),
                ItemStack(ModItems.PAINKILLERS_ITEM.get())
            )
        )
        backpackStack.set(ModDataComponents.GRID_INVENTORY, backpackGrid)
        equipment.persistentGrids["backpack"] =
            GridInventory(2, 2, listOf(GridItemInstance(backpackStack, 0, 0, false)), filter = "backpack", singleItem = true)

        player.setData(ModDataAttachments.PLAYER_EQUIPMENT, equipment)
        PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(equipment))
        player.containerMenu.broadcastChanges()
    }

    private fun applyClearKit(player: ServerPlayer) {
        val freshEquipment = PlayerEquipment()
        player.setData(ModDataAttachments.PLAYER_EQUIPMENT, freshEquipment)

        player.inventory.setItem(InventoryUtils.PRIMARY_1_SLOT, ItemStack.EMPTY)
        player.inventory.setItem(InventoryUtils.PRIMARY_2_SLOT, ItemStack.EMPTY)
        player.inventory.setItem(InventoryUtils.PISTOL_SLOT, ItemStack.EMPTY)
        InventoryUtils.syncHotbarSlot(player, InventoryUtils.PRIMARY_1_SLOT)
        InventoryUtils.syncHotbarSlot(player, InventoryUtils.PRIMARY_2_SLOT)
        InventoryUtils.syncHotbarSlot(player, InventoryUtils.PISTOL_SLOT)

        PacketDistributor.sendToPlayer(player, SyncEquipmentPayload(freshEquipment))
        player.containerMenu.broadcastChanges()
    }
}
