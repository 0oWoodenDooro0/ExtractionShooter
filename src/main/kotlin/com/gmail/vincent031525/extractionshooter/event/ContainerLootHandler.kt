package com.gmail.vincent031525.extractionshooter.event

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.block.entity.LootCrateBlockEntity
import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import com.gmail.vincent031525.extractionshooter.menu.GridInventoryMenu
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Container
import net.minecraft.world.InteractionResult
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.ChestBlock
import net.minecraft.world.level.block.entity.BarrelBlockEntity
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.ChestBlockEntity
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.common.extensions.IMenuProviderExtension
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent

@EventBusSubscriber(modid = Extractionshooter.ID)
object ContainerLootHandler {

    @SubscribeEvent
    fun onRightClickBlock(event: PlayerInteractEvent.RightClickBlock) {
        val player = event.entity
        if (player.isSpectator) return
        val level = event.level
        val pos = event.pos

        if (player.isShiftKeyDown && !event.itemStack.isEmpty) return

        val blockEntity = level.getBlockEntity(pos) ?: return
        val blockState = level.getBlockState(pos)

        val isChest = blockEntity is ChestBlockEntity
        val isBarrel = blockEntity is BarrelBlockEntity
        val isShulker = blockEntity is ShulkerBoxBlockEntity
        val isLootCrate = blockEntity is LootCrateBlockEntity

        if (!isChest && !isBarrel && !isShulker && !isLootCrate) return

        if (isChest && ChestBlock.isChestBlockedAt(level, pos)) return

        event.isCanceled = true
        event.cancellationResult = InteractionResult.SUCCESS

        if (level.isClientSide) return

        val serverPlayer = player as? ServerPlayer ?: return

        openLootMenu(serverPlayer, pos, blockEntity)
    }

    private fun openLootMenu(player: ServerPlayer, pos: BlockPos, blockEntity: BlockEntity) {
        val level = player.level()
        val equipment = player.getData(ModDataAttachments.PLAYER_EQUIPMENT)

        val (containerGrid, containerTitle) = when (blockEntity) {
            is LootCrateBlockEntity -> {
                blockEntity.grid to Component.translatable("container.extractionshooter.loot_crate")
            }
            is ChestBlockEntity -> {
                val blockState = level.getBlockState(pos)
                val container = if (blockState.block is ChestBlock) {
                    ChestBlock.getContainer(blockState.block as ChestBlock, blockState, level, pos, true) ?: blockEntity
                } else blockEntity
                val title = (container as? MenuProvider)?.displayName ?: Component.literal("Chest")
                val grid = InventoryUtils.containerToGrid(container, blockEntity)
                grid to title
            }
            is BarrelBlockEntity -> {
                val title = blockEntity.displayName
                val grid = InventoryUtils.containerToGrid(blockEntity, blockEntity)
                grid to title
            }
            is ShulkerBoxBlockEntity -> {
                val title = blockEntity.displayName
                val grid = InventoryUtils.containerToGrid(blockEntity, blockEntity)
                grid to title
            }
            is Container -> {
                val title = (blockEntity as? MenuProvider)?.displayName ?: Component.literal("Container")
                val grid = InventoryUtils.containerToGrid(blockEntity, blockEntity)
                grid to title
            }
            else -> return
        }

        val sound = when (blockEntity) {
            is BarrelBlockEntity -> SoundEvents.BARREL_OPEN
            else -> SoundEvents.CHEST_OPEN
        }
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 0.5f, level.random.nextFloat() * 0.1f + 0.9f)

        player.openMenu(object : MenuProvider, IMenuProviderExtension {
            override fun getDisplayName(): Component = containerTitle

            override fun createMenu(id: Int, inv: Inventory, p: Player): AbstractContainerMenu {
                return GridInventoryMenu(id, inv, equipment, pos, containerGrid, containerTitle)
            }

            override fun writeClientSideData(menu: AbstractContainerMenu, buf: RegistryFriendlyByteBuf) {
                buf.writeBoolean(true)
                buf.writeBlockPos(pos)
                buf.writeUtf(containerTitle.string)
                GridInventory.STREAM_CODEC.encode(buf, containerGrid)
            }
        })
    }
}
