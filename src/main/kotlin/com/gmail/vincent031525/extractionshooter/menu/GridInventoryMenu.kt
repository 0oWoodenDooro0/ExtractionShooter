package com.gmail.vincent031525.extractionshooter.menu

import com.gmail.vincent031525.extractionshooter.inventory.PlayerEquipment
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.registry.ModMenus
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

class GridInventoryMenu(windowId: Int, val playerInventory: Inventory, val equipment: PlayerEquipment) :
    AbstractContainerMenu(ModMenus.GRID_INVENTORY_MENU.get(), windowId) {

    // For opening on client
    constructor(windowId: Int, playerInventory: Inventory, data: FriendlyByteBuf?) : this(
        windowId,
        playerInventory,
        playerInventory.player.getData(ModDataAttachments.PLAYER_EQUIPMENT)
    )

    init {
        // Vanilla slots and hotbar removed as per GridInventory refactor.
        // Custom GridInventory slots will be added in subsequent phases.
    }

    override fun stillValid(player: Player): Boolean = true

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }
}
