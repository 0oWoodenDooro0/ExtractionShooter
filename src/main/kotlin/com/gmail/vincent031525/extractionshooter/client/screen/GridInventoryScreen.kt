package com.gmail.vincent031525.extractionshooter.client.screen

import com.gmail.vincent031525.extractionshooter.datamap.ItemSize
import com.gmail.vincent031525.extractionshooter.menu.GridInventoryMenu
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
import kotlin.math.roundToInt
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.client.network.ClientPacketDistributor
import org.lwjgl.glfw.GLFW

class GridInventoryScreen(menu: GridInventoryMenu, playerInventory: Inventory, title: Component) :
    AbstractContainerScreen<GridInventoryMenu>(menu, playerInventory, title) {

    private var heldItemRotated: Boolean = false

    init {
        this.imageWidth = 256
        this.imageHeight = 256
        this.inventoryLabelY = this.imageHeight - 94
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
        targetH: Int
    ) {
        val scale = minOf(targetW.toFloat() / 16f, targetH.toFloat() / 16f)
        val scaledSize = 16f * scale

        val offsetX = (targetW - scaledSize) / 2f
        val offsetY = (targetH - scaledSize) / 2f

        val poseStack = guiGraphics.pose()
        poseStack.pushMatrix()
        poseStack.translate(x.toFloat() + offsetX, y.toFloat() + offsetY)
        poseStack.scale(scale, scale)
        guiGraphics.renderItem(stack, 0, 0)
        poseStack.popMatrix()
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2

        // Draw background
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, -0x333334)

        // Render Active Grids
        val activeGrids = menu.equipment.getAllActiveGrids()
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
                    // Image sizes: Primary is 64x32, others are 32x32
                    // Use normalized UVs (0f, 0f, 1f, 1f) to stretch the full texture to the target width/height
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
                    // Scale item to fit the entire slot
                    val slotW = grid.columns * 18
                    val slotH = grid.rows * 18

                    renderScaledItem(guiGraphics, instance.stack, gridX, gridY, slotW, slotH)
                } else {
                    val size = instance.getActualSize(grid.sizeProvider)
                    val targetW = size.width * 18
                    val targetH = size.height * 18

                    guiGraphics.fill(itemX, itemY, itemX + targetW - 1, itemY + targetH - 1, -0x555556)

                    renderScaledItem(guiGraphics, instance.stack, itemX, itemY, targetW, targetH)
                }
            }
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val carried = menu.carried
        if (!carried.isEmpty) {
            menu.carried = ItemStack.EMPTY
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick)

        if (!carried.isEmpty) {
            menu.carried = carried

            // Render phantom shape if holding item
            val size = InventoryUtils.getItemSize(carried)
            val renderSize = if (heldItemRotated) ItemSize(size.height, size.width) else size

            val targetW = renderSize.width * 18
            val targetH = renderSize.height * 18

            // Center the item on the mouse cursor
            val renderX = mouseX - (targetW / 2)
            val renderY = mouseY - (targetH / 2)

            var tint = 0x80FFFFFF.toInt() // Default: Semi-transparent white
            
            // Where to draw the background tint (default to floating with mouse)
            var tintX = renderX
            var tintY = renderY

            var snapped = false
            // Check if hovering over a grid to show valid/invalid placement
            val x = (width - imageWidth) / 2
            val y = (height - imageHeight) / 2
            val activeGrids = menu.equipment.getAllActiveGrids()

            for ((name, grid) in activeGrids) {
                val pos = MenuLayout.getPos(name)
                val gridX = x + pos.x
                val gridY = y + pos.y
                val gridHeight = grid.rows * 18
                val gridWidth = grid.columns * 18

                // Draw global hint for equipment slots
                if (grid.singleItem && grid.canPlace(carried, 0, 0, false)) {
                    // Slightly fainter green for hint
                    guiGraphics.fill(gridX, gridY, gridX + gridWidth, gridY + gridHeight, 0x4000FF00.toInt())
                }

                if (mouseX >= gridX && mouseX < gridX + gridWidth && mouseY >= gridY && mouseY < gridY + gridHeight) {

                    // Calculate col/row based on mouse position relative to item center
                    val col = if (grid.singleItem) 0 else ((mouseX - gridX) / 18.0 - renderSize.width / 2.0).roundToInt()
                    val row = if (grid.singleItem) 0 else ((mouseY - gridY) / 18.0 - renderSize.height / 2.0).roundToInt()
                    
                    if (grid.canPlace(carried, col, row, heldItemRotated)) {
                        tint = 0x8000FF00.toInt() // Valid: Green
                        if (grid.singleItem) {
                            // Brighter highlight for the hovered slot
                            guiGraphics.fill(gridX, gridY, gridX + gridWidth, gridY + gridHeight, tint)
                            // Do NOT snap or scale. Let it fall through to floating render.
                            break
                        }
                    } else {
                        tint = 0x80FF0000.toInt() // Invalid: Red
                    }
                    break
                }
            }

            if (!snapped) {
                // Draw phantom background (at snapped position if hovering grid, or floating if not)
                guiGraphics.fill(tintX, tintY, tintX + targetW, tintY + targetH, tint)

                // Draw the actual scaled item (always floating with mouse)
                renderScaledItem(guiGraphics, carried, renderX, renderY, targetW, targetH)
            }
        }

        renderTooltip(guiGraphics, mouseX, mouseY)
    }

    fun onMouseClick(mouseX: Double, mouseY: Double, button: Int): Boolean {
        // We don't call super here because we are handling it externally via event

        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        val activeGrids = menu.equipment.getAllActiveGrids()

        // Iterate grids to find click
        for ((name, grid) in activeGrids) {
            val pos = MenuLayout.getPos(name)
            val gridX = x + pos.x
            val gridY = y + pos.y
            val gridHeight = grid.rows * 18

            if (mouseX >= gridX && mouseX < gridX + grid.columns * 18 && mouseY >= gridY && mouseY < gridY + gridHeight) {

                if (menu.carried.isEmpty) {
                    val col = ((mouseX - gridX) / 18).toInt()
                    val row = ((mouseY - gridY) / 18).toInt()

                    // Try pickup
                    val instance = grid.getItemInstance(col, row)
                    if (instance != null) {
                        // Optimistic update
                        val result = grid.removeItem(col, row)
                        if (result != null) {
                            val (newGrid, stack) = result
                            menu.equipment.updateGrid(name, newGrid)
                            menu.carried = stack
                        }

                        heldItemRotated = instance.rotated

                        // Send Packet
                        ClientPacketDistributor.sendToServer(
                            com.gmail.vincent031525.extractionshooter.network.payload.PickFromGridPayload(
                                name, instance.x, instance.y
                            )
                        )
                        return true
                    }
                } else {
                    // Try place
                    val carried = menu.carried

                    val size = InventoryUtils.getItemSize(carried)
                    val renderSize = if (heldItemRotated) ItemSize(size.height, size.width) else size
                    val targetW = renderSize.width * 18
                    val targetH = renderSize.height * 18

                    val renderX = mouseX - (targetW / 2)
                    val renderY = mouseY - (targetH / 2)

                    // Calculate col/row based on mouse position relative to item center
                    val col = if (grid.singleItem) 0 else ((mouseX - gridX) / 18.0 - renderSize.width / 2.0).roundToInt()
                    val row = if (grid.singleItem) 0 else ((mouseY - gridY) / 18.0 - renderSize.height / 2.0).roundToInt()

                    // We need to check locally if it fits to avoid desync flickering
                    // Use a temp instance to check fit with current rotation
                    if (grid.canPlace(carried, col, row, heldItemRotated)) {
                        // Optimistic update
                        val newGrid = grid.addItem(carried, col, row, heldItemRotated)
                        if (newGrid != null) {
                            menu.equipment.updateGrid(name, newGrid)
                            menu.carried = ItemStack.EMPTY
                        }

                        ClientPacketDistributor.sendToServer(
                            com.gmail.vincent031525.extractionshooter.network.payload.PlaceToGridPayload(
                                name, col, row, heldItemRotated
                            )
                        )
                        // Reset rotation after placing? Or keep it? Usually keep for multi-place, but here we place whole stack.
                        heldItemRotated = false
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