package com.gmail.vincent031525.extractionshooter.menu

import com.gmail.vincent031525.extractionshooter.block.entity.LootCrateBlockEntity
import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import com.gmail.vincent031525.extractionshooter.inventory.PlayerEquipment
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.registry.ModMenus
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.BarrelBlockEntity

class GridInventoryMenu(
    windowId: Int,
    val playerInventory: Inventory,
    initialEquipment: PlayerEquipment? = null,
    val containerPos: BlockPos? = null,
    var containerGrid: GridInventory? = null,
    val containerTitle: Component = Component.literal("Inventory")
) : AbstractContainerMenu(ModMenus.GRID_INVENTORY_MENU.get(), windowId) {

    private data class ContainerMenuData(
        val pos: BlockPos,
        val title: Component,
        val grid: GridInventory
    )

    // For opening on client
    constructor(windowId: Int, playerInventory: Inventory, data: RegistryFriendlyByteBuf?) : this(
        windowId,
        playerInventory,
        playerInventory.player.getData(ModDataAttachments.PLAYER_EQUIPMENT),
        readContainerData(data)
    )

    private constructor(
        windowId: Int,
        playerInventory: Inventory,
        equipment: PlayerEquipment,
        containerData: ContainerMenuData?
    ) : this(
        windowId,
        playerInventory,
        equipment,
        containerPos = containerData?.pos,
        containerGrid = containerData?.grid,
        containerTitle = containerData?.title ?: Component.literal("Inventory")
    )

    companion object {
        private fun readContainerData(buf: RegistryFriendlyByteBuf?): ContainerMenuData? {
            if (buf == null || buf.readableBytes() <= 0) return null
            val hasContainer = buf.readBoolean()
            if (!hasContainer) return null
            val pos = buf.readBlockPos()
            val titleStr = buf.readUtf()
            val grid = GridInventory.STREAM_CODEC.decode(buf)
            return ContainerMenuData(pos, Component.literal(titleStr), grid)
        }
    }

    val equipment: PlayerEquipment
        get() = playerInventory.player.getData(ModDataAttachments.PLAYER_EQUIPMENT)

    fun isLooting(): Boolean = containerGrid != null

    fun getAllActiveGrids(): Map<String, GridInventory> {
        val all = equipment.getAllActiveGrids(playerInventory.player).toMutableMap()
        containerGrid?.let {
            all["container"] = it
        }
        return all
    }

    fun updateGrid(name: String, newGrid: GridInventory) {
        if (name == "container") {
            containerGrid = newGrid
            if (containerPos != null && !playerInventory.player.level().isClientSide) {
                val level = playerInventory.player.level()
                val be = level.getBlockEntity(containerPos)
                if (be is LootCrateBlockEntity) {
                    be.grid = newGrid
                    be.setChanged()
                } else if (be != null) {
                    val blockState = level.getBlockState(containerPos)
                    val container = if (blockState.block is ChestBlock) {
                        ChestBlock.getContainer(
                            blockState.block as ChestBlock,
                            blockState,
                            level,
                            containerPos,
                            true
                        ) ?: (be as? Container)
                    } else {
                        be as? Container
                    }
                    if (container != null) {
                        InventoryUtils.gridToContainer(newGrid, container, be)
                    }
                }
            }
        } else {
            equipment.updateGrid(name, newGrid, playerInventory.player)
        }
    }

    override fun stillValid(player: Player): Boolean {
        if (containerPos != null) {
            return player.distanceToSqr(
                containerPos.x + 0.5,
                containerPos.y + 0.5,
                containerPos.z + 0.5
            ) <= 64.0
        }
        return true
    }

    override fun removed(player: Player) {
        super.removed(player)
        if (!player.level().isClientSide) {
            val carriedItem = this.carried
            if (!carriedItem.isEmpty) {
                this.carried = ItemStack.EMPTY
                player.drop(carriedItem, false)
            }

            if (containerPos != null) {
                val level = player.level()
                val be = level.getBlockEntity(containerPos)
                val sound = when (be) {
                    is BarrelBlockEntity -> SoundEvents.BARREL_CLOSE
                    else -> SoundEvents.CHEST_CLOSE
                }
                level.playSound(
                    null,
                    containerPos,
                    sound,
                    SoundSource.BLOCKS,
                    0.5f,
                    level.random.nextFloat() * 0.1f + 0.9f
                )
            }
        }
    }

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        return ItemStack.EMPTY
    }
}
