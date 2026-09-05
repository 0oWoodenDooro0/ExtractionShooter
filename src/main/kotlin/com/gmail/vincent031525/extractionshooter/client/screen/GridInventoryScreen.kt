package com.gmail.vincent031525.extractionshooter.client.screen

import com.gmail.vincent031525.extractionshooter.client.gui.HealthHudOverlay
import com.gmail.vincent031525.extractionshooter.datamap.ItemSize
import com.gmail.vincent031525.extractionshooter.inventory.GridActionHandler
import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import com.gmail.vincent031525.extractionshooter.inventory.GridItemInstance
import com.gmail.vincent031525.extractionshooter.inventory.GridQuickMoveHelper
import com.gmail.vincent031525.extractionshooter.menu.GridInventoryMenu
import com.gmail.vincent031525.extractionshooter.network.payload.*
import com.gmail.vincent031525.extractionshooter.registry.ModDataAttachments
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import org.lwjgl.glfw.GLFW

class GridInventoryScreen(menu: GridInventoryMenu, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<GridInventoryMenu>(menu, playerInventory, title) {

    private var heldItemRotated: Boolean = false
    private var lastMouseX: Double = 0.0
    private var lastMouseY: Double = 0.0

    init {
        this.imageWidth = if (menu.isLooting()) 416 else 226
        this.imageHeight = 256
        this.inventoryLabelY = this.imageHeight - 94
    }

    private fun isShiftDown(): Boolean {
        val window = minecraft?.window ?: return false
        return InputConstants.isKeyDown(window, GLFW.GLFW_KEY_LEFT_SHIFT) ||
               InputConstants.isKeyDown(window, GLFW.GLFW_KEY_RIGHT_SHIFT)
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        // Remove default labels
    }

    private fun renderScaledItem(
        guiGraphics: GuiGraphics,
        stack: ItemStack,
        x: Int,
        y: Int,
        targetW: Int,
        targetH: Int,
        rotated: Boolean = false
    ) {
        val pose = guiGraphics.pose()
        pose.pushMatrix()

        if (rotated) {
            val scale = minOf(targetH.toFloat() / 16f, targetW.toFloat() / 16f)
            pose.translate(x.toFloat() + targetW / 2f, y.toFloat() + targetH / 2f)
            pose.rotate(Math.PI.toFloat() / 2f)
            pose.scale(scale, scale)
            pose.translate(-8f, -8f)
        } else {
            val scale = minOf(targetW.toFloat() / 16f, targetH.toFloat() / 16f)
            val scaledSize = 16f * scale
            val offsetX = (targetW - scaledSize) / 2f
            val offsetY = (targetH - scaledSize) / 2f
            pose.translate(x.toFloat() + offsetX, y.toFloat() + offsetY)
            pose.scale(scale, scale)
        }

        guiGraphics.renderItem(stack, 0, 0)
        pose.popMatrix()
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2

        // Draw background
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, -0x333334)

        // If looting, draw divider and container title
        if (menu.isLooting()) {
            guiGraphics.fill(x + 226, y + 6, x + 228, y + imageHeight - 6, -0x555556)
            guiGraphics.drawString(font, menu.containerTitle, x + 236, y + 10, 0xFFE0E0E0.toInt(), false)
            guiGraphics.drawString(font, Component.literal("YOUR GEAR"), x + 10, y + 10, 0xFFAAAAAA.toInt(), false)
        } else {
            guiGraphics.drawString(font, Component.literal("INVENTORY"), x + 10, y + 10, 0xFFAAAAAA.toInt(), false)
        }

        // Draw Health Card beside inventory
        val player = minecraft?.player
        if (player != null) {
            val health = player.getData(ModDataAttachments.PLAYER_HEALTH)
            val cardX = if (x >= 134) x - 130 else maxOf(4, x - 130)
            HealthHudOverlay.renderHealthCard(guiGraphics, cardX, y + 10, health, player)
        }

        // Render Active Grids
        val activeGrids = menu.getAllActiveGrids()
        activeGrids.forEach { (name, grid) ->
            val pos = MenuLayout.getPos(name)
            val gridX = x + pos.x
            val gridY = y + pos.y

            if (grid.singleItem) {
                // Draw Ghost Image for equipment slots
                val texture = when {
                    name.startsWith("primary") -> "primary"
                    name == "helmet" -> "helmet"
                    name == "armor" -> "chest"
                    name == "tactical_rig" -> "rig"
                    name == "backpack" -> "backpack"
                    name == "pistol" -> "pistol"
                    else -> null
                }

                if (texture != null) {
                    val resource = Identifier.fromNamespaceAndPath(
                        "extractionshooter", "textures/gui/slots/$texture.png"
                    )
                    guiGraphics.blit(
                        resource,
                        gridX,
                        gridY,
                        gridX + grid.columns * 18,
                        gridY + grid.rows * 18,
                        0f,
                        1f,
                        0f,
                        1f
                    )
                } else {
                    // Fallback to fill
                    guiGraphics.fill(gridX, gridY, gridX + grid.columns * 18, gridY + grid.rows * 18, -0xbbbbbc)
                }
            } else {
                // Draw standard grid lines
                for (row in 0 until grid.rows) {
                    for (col in 0 until grid.columns) {
                        val slotX = gridX + col * 18
                        val slotY = gridY + row * 18
                        guiGraphics.fill(slotX, slotY, slotX + 17, slotY + 17, -0xbbbbbc)
                    }
                }
            }

            // Draw items
            grid.items.forEach { instance ->
                val itemX = gridX + instance.x * 18
                val itemY = gridY + instance.y * 18

                if (grid.singleItem) {
                    val slotW = grid.columns * 18
                    val slotH = grid.rows * 18

                    renderScaledItem(guiGraphics, instance.stack, gridX, gridY, slotW, slotH, instance.rotated)
                    if (instance.stack.count > 1) {
                        guiGraphics.renderItemDecorations(font, instance.stack, gridX + slotW - 18, gridY + slotH - 18)
                    }
                } else {
                    val size = instance.getActualSize(InventoryUtils::getItemSize)
                    val targetW = size.width * 18
                    val targetH = size.height * 18

                    guiGraphics.fill(itemX, itemY, itemX + targetW - 1, itemY + targetH - 1, -0x555556)

                    renderScaledItem(guiGraphics, instance.stack, itemX, itemY, targetW, targetH, instance.rotated)
                    if (instance.stack.count > 1) {
                        guiGraphics.renderItemDecorations(font, instance.stack, itemX + targetW - 18, itemY + targetH - 18)
                    }
                }
            }
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        lastMouseX = mouseX.toDouble()
        lastMouseY = mouseY.toDouble()

        val carried = menu.carried
        if (!carried.isEmpty) {
            menu.carried = ItemStack.EMPTY
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick)

        if (!carried.isEmpty) {
            menu.carried = carried

            val baseSize = InventoryUtils.getItemSize(carried)
            val renderSize = if (heldItemRotated) ItemSize(baseSize.height, baseSize.width) else baseSize

            val targetW = renderSize.width * 18
            val targetH = renderSize.height * 18

            val x = (width - imageWidth) / 2
            val y = (height - imageHeight) / 2
            val activeGrids = menu.getAllActiveGrids()

            var hoveredGrid: GridInventory? = null
            var hoverGridX = 0
            var hoverGridY = 0

            for ((name, grid) in activeGrids) {
                val pos = MenuLayout.getPos(name)
                val gridX = x + pos.x
                val gridY = y + pos.y
                val gridHeight = grid.rows * 18
                val gridWidth = grid.columns * 18

                // Draw global hint for equipment slots
                if (grid.singleItem && grid.canPlace(carried, 0, 0, false)) {
                    guiGraphics.fill(gridX, gridY, gridX + gridWidth, gridY + gridHeight, 0x4000FF00.toInt())
                }

                if (mouseX >= gridX && mouseX < gridX + gridWidth && mouseY >= gridY && mouseY < gridY + gridHeight) {
                    hoveredGrid = grid
                    hoverGridX = gridX
                    hoverGridY = gridY
                }
            }

            var tint = 0x80FFFFFF.toInt()

            if (hoveredGrid != null) {
                val hoverSlotCol = if (hoveredGrid.singleItem) 0 else ((mouseX - hoverGridX) / 18).toInt()
                val hoverSlotRow = if (hoveredGrid.singleItem) 0 else ((mouseY - hoverGridY) / 18).toInt()
                val targetInstance = hoveredGrid.getItemInstance(hoverSlotCol, hoverSlotRow)

                if (targetInstance != null && minecraft?.level != null &&
                    GridActionHandler.canInteract(minecraft!!.level!!, hoveredGrid, targetInstance.x, targetInstance.y, carried, 1)
                ) {
                    tint = 0x8000FF00.toInt()
                } else {
                    val placeCol = if (hoveredGrid.singleItem) 0 else Math.round((mouseX - targetW / 2.0 - hoverGridX).toFloat() / 18f)
                    val placeRow = if (hoveredGrid.singleItem) 0 else Math.round((mouseY - targetH / 2.0 - hoverGridY).toFloat() / 18f)
                    val canPlace = hoveredGrid.canPlace(carried, placeCol, placeRow, heldItemRotated)
                    tint = if (canPlace) 0x8000FF00.toInt() else 0x80FF0000.toInt()
                }
            }

            // Smoothly follow cursor without snapping to grid
            val renderX = mouseX - (targetW / 2)
            val renderY = mouseY - (targetH / 2)

            guiGraphics.fill(renderX, renderY, renderX + targetW, renderY + targetH, tint)
            renderScaledItem(guiGraphics, carried, renderX, renderY, targetW, targetH, heldItemRotated)

            if (carried.count > 1) {
                guiGraphics.renderItemDecorations(font, carried, renderX + targetW - 18, renderY + targetH - 18)
            }
        }

        // Render tooltips when not holding an item
        if (menu.carried.isEmpty) {
            val hovered = findHoveredItem(mouseX.toDouble(), mouseY.toDouble())
            if (hovered != null) {
                guiGraphics.setTooltipForNextFrame(font, hovered.instance.stack, mouseX, mouseY)
            }
        }
    }

    data class HoveredItem(val gridName: String, val grid: GridInventory, val instance: GridItemInstance)

    private fun findHoveredItem(mouseX: Double, mouseY: Double): HoveredItem? {
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        val activeGrids = menu.getAllActiveGrids()
        for ((name, grid) in activeGrids) {
            val pos = MenuLayout.getPos(name)
            val gridX = x + pos.x
            val gridY = y + pos.y
            val gridHeight = grid.rows * 18
            val gridWidth = grid.columns * 18

            if (mouseX >= gridX && mouseX < gridX + gridWidth && mouseY >= gridY && mouseY < gridY + gridHeight) {
                val col = if (grid.singleItem) 0 else ((mouseX - gridX) / 18).toInt()
                val row = if (grid.singleItem) 0 else ((mouseY - gridY) / 18).toInt()
                val instance = grid.getItemInstance(col, row)
                if (instance != null) {
                    return HoveredItem(name, grid, instance)
                }
            }
        }
        return null
    }

    fun onMouseClick(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2

        // Shift + Click: Quick Move / Auto-Equip (only when empty handed)
        if (isShiftDown() && button == 0 && menu.carried.isEmpty) {
            val hovered = findHoveredItem(mouseX, mouseY)
            if (hovered != null) {
                val player = minecraft?.player ?: return false
                val moved = GridQuickMoveHelper.quickMove(
                    player,
                    menu,
                    hovered.gridName,
                    hovered.instance.x,
                    hovered.instance.y
                )
                if (moved) {
                    ClientPacketDistributor.sendToServer(
                        QuickMoveGridItemPayload(hovered.gridName, hovered.instance.x, hovered.instance.y)
                    )
                    player.level().playLocalSound(
                        player.x, player.y, player.z,
                        SoundEvents.ARMOR_EQUIP_GENERIC.value(),
                        SoundSource.PLAYERS,
                        0.8f,
                        1.0f,
                        false
                    )
                    return true
                }
            }
        }

        val activeGrids = menu.getAllActiveGrids()

        // Iterate grids to find click
        for ((name, grid) in activeGrids) {
            val pos = MenuLayout.getPos(name)
            val gridX = x + pos.x
            val gridY = y + pos.y
            val gridHeight = grid.rows * 18
            val gridWidth = grid.columns * 18

            if (mouseX >= gridX && mouseX < gridX + gridWidth && mouseY >= gridY && mouseY < gridY + gridHeight) {
                // Case 1: Carrying an item -> attempt interact or place
                if (!menu.carried.isEmpty) {
                    val slotCol = if (grid.singleItem) 0 else ((mouseX - gridX) / 18).toInt()
                    val slotRow = if (grid.singleItem) 0 else ((mouseY - gridY) / 18).toInt()
                    val targetInstance = grid.getItemInstance(slotCol, slotRow)

                    // Try item interaction first (e.g. loading ammo into magazine)
                    if (targetInstance != null && minecraft?.level != null) {
                        val interaction = GridActionHandler.interact(
                            minecraft!!.level!!,
                            grid,
                            targetInstance.x,
                            targetInstance.y,
                            menu.carried,
                            button
                        )

                        if (interaction != null) {
                            menu.updateGrid(name, interaction.newGrid)
                            menu.carried = interaction.newCarried
                            heldItemRotated = false

                            interaction.sound?.let { sound ->
                                minecraft?.player?.let { p ->
                                    p.level().playLocalSound(
                                        p.x, p.y, p.z,
                                        sound,
                                        SoundSource.PLAYERS,
                                        1.0f,
                                        interaction.pitch,
                                        false
                                    )
                                }
                            }

                            ClientPacketDistributor.sendToServer(
                                InteractGridItemPayload(name, targetInstance.x, targetInstance.y, button)
                            )
                            return true
                        }
                    }

                    // Otherwise attempt to place into grid
                    if (button == 0) {
                        val carried = menu.carried
                        val baseSize = InventoryUtils.getItemSize(carried)
                        val renderSize = if (heldItemRotated) ItemSize(baseSize.height, baseSize.width) else baseSize
                        val targetW = renderSize.width * 18
                        val targetH = renderSize.height * 18

                        val placeCol = if (grid.singleItem) 0 else Math.round((mouseX - targetW / 2.0 - gridX).toFloat() / 18f)
                        val placeRow = if (grid.singleItem) 0 else Math.round((mouseY - targetH / 2.0 - gridY).toFloat() / 18f)

                        if (grid.canPlace(carried, placeCol, placeRow, heldItemRotated)) {
                            val newGrid = grid.addItem(carried, placeCol, placeRow, heldItemRotated)
                            if (newGrid != null) {
                                menu.updateGrid(name, newGrid)
                                menu.carried = ItemStack.EMPTY
                            }

                            ClientPacketDistributor.sendToServer(
                                PlaceToGridPayload(name, placeCol, placeRow, heldItemRotated)
                            )
                            heldItemRotated = false
                            return true
                        }
                    }
                } else {
                    // Case 2: Empty handed -> pick up item
                    val slotCol = if (grid.singleItem) 0 else ((mouseX - gridX) / 18).toInt()
                    val slotRow = if (grid.singleItem) 0 else ((mouseY - gridY) / 18).toInt()
                    val targetInstance = grid.getItemInstance(slotCol, slotRow)

                    if (targetInstance != null && button == 0) {
                        val result = grid.removeItem(targetInstance.x, targetInstance.y)
                        if (result != null) {
                            val (newGrid, stack) = result
                            menu.updateGrid(name, newGrid)
                            menu.carried = stack
                            heldItemRotated = targetInstance.rotated
                        }

                        ClientPacketDistributor.sendToServer(
                            PickFromGridPayload(name, targetInstance.x, targetInstance.y)
                        )
                        return true
                    }
                }
            }
        }
        return false
    }

    fun onKeyPress(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (keyCode == GLFW.GLFW_KEY_R && !menu.carried.isEmpty) {
            heldItemRotated = !heldItemRotated
            return true
        }
        return false
    }
}
