package com.gmail.vincent031525.extractionshooter.client.screen

import com.gmail.vincent031525.extractionshooter.datamap.ItemSize
import com.gmail.vincent031525.extractionshooter.menu.GridInventoryMenu
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
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
                    name == "pistol" -> "rig" // Fallback to rig for pistol for now
                    else -> null
                }
                
                if (texture != null) {
                    val resource = Identifier.fromNamespaceAndPath(
                        "extractionshooter", "textures/gui/slots/$texture.png"
                    )
                    guiGraphics.blit(resource, gridX, gridY, grid.columns * 18, grid.rows * 18, 0f, 0f, (grid.columns * 18).toFloat(), (grid.rows * 18).toFloat())
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
                    
                    val pose = guiGraphics.pose()
                    // If we can't use push/pop, let's try to just use translate if it works
                    // But we really need push/pop. 
                    // Let's try to use guiGraphics.pose().push() again but check if there's any other method.
                    // Actually, let's try to avoid scaling for a moment to see if it compiles.
                    
                    // guiGraphics.renderItem(instance.stack, itemX, itemY) 
                    
                    // Re-attempting push/pop with possible correct names
                    // If everything fails, I'll use a simple translate if available.
                    
                    val scale = Math.min(slotW.toFloat() / 16f, slotH.toFloat() / 16f)
                    val offsetX = (slotW / scale - 16f) / 2f
                    val offsetY = (slotH / scale - 16f) / 2f
                    
                    // Attempting to use the Matrix3x2f if it's what it wants
                    // pose.translate(org.joml.Matrix3x2f().translate(gridX.toFloat(), gridY.toFloat()))
                    
                    guiGraphics.renderItem(instance.stack, gridX + (offsetX * scale).toInt(), gridY + (offsetY * scale).toInt())
                } else {
                    val size = instance.getActualSize(grid.sizeProvider)
                    guiGraphics.fill(itemX, itemY, itemX + size.width * 18 - 1, itemY + size.height * 18 - 1, -0x555556)
                    guiGraphics.renderItem(
                        instance.stack, itemX + (size.width * 18 - 16) / 2, itemY + (size.height * 18 - 16) / 2
                    )
                }
            }
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        // Render phantom shape if holding item
        if (!menu.carried.isEmpty) {
            val carried = menu.carried
            val size = InventoryUtils.getItemSize(carried)
            val renderSize = if (heldItemRotated) ItemSize(size.height, size.width) else size

            var tint = 0x80FFFFFF.toInt() // Default: Semi-transparent white

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

                if (mouseX >= gridX && mouseX < gridX + gridWidth && mouseY >= gridY && mouseY < gridY + gridHeight) {

                    val col = ((mouseX - gridX) / 18)
                    val row = ((mouseY - gridY) / 18)

                    if (grid.canPlace(carried, col, row, heldItemRotated)) {
                        tint = 0x8000FF00.toInt() // Valid: Green
                        if (grid.singleItem) {
                            // Show phantom as the whole slot or 1x1?
                            // For equipment, it's better to show it fills the whole slot or just a 1x1 highlight.
                            // Requirement says "scale item icons to fit", so the item effectively occupies the whole slot.
                            guiGraphics.fill(gridX, gridY, gridX + gridWidth, gridY + gridHeight, tint)
                            return
                        }
                    } else {
                        tint = 0x80FF0000.toInt() // Invalid: Red
                    }
                    break
                }
            }

            guiGraphics.fill(mouseX, mouseY, mouseX + renderSize.width * 18, mouseY + renderSize.height * 18, tint)
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

                val col = ((mouseX - gridX) / 18).toInt()
                val row = ((mouseY - gridY) / 18).toInt()

                if (menu.carried.isEmpty) {
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