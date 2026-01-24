package com.gmail.vincent031525.extractionshooter.client.screen

import com.gmail.vincent031525.extractionshooter.datamap.ItemSize
import com.gmail.vincent031525.extractionshooter.menu.GridInventoryMenu
import com.gmail.vincent031525.extractionshooter.network.payload.MoveGridItemPayload
import com.gmail.vincent031525.extractionshooter.util.InventoryUtils
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
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
            
            guiGraphics.drawString(font, name, gridX, gridY, 0xFFFFFF)
            val slotsStartY = gridY + 10
            
            // Draw grid lines and items
            for (row in 0 until grid.rows) {
                for (col in 0 until grid.columns) {
                    val slotX = gridX + col * 18
                    val slotY = slotsStartY + row * 18
                    guiGraphics.fill(slotX, slotY, slotX + 17, slotY + 17, -0xbbbbbc)
                }
            }

            // Draw items
            grid.items.forEach { instance ->
                val itemX = gridX + instance.x * 18
                val itemY = slotsStartY + instance.y * 18
                val size = instance.getActualSize(grid.sizeProvider)
                
                guiGraphics.fill(itemX, itemY, itemX + size.width * 18 - 1, itemY + size.height * 18 - 1, -0x555556)
                guiGraphics.renderItem(instance.stack, itemX + (size.width * 18 - 16) / 2, itemY + (size.height * 18 - 16) / 2)
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
                val gridHeaderHeight = 10
                val gridHeight = grid.rows * 18
                val gridStartY = gridY + gridHeaderHeight
                
                if (mouseX >= gridX && mouseX < gridX + grid.columns * 18 &&
                    mouseY >= gridStartY && mouseY < gridStartY + gridHeight) {
                    
                    val col = ((mouseX - gridX) / 18).toInt()
                    val row = ((mouseY - gridStartY) / 18).toInt()
                    
                    tint = if (grid.canPlace(carried, col, row, heldItemRotated)) {
                        0x8000FF00.toInt() // Valid: Green
                    } else {
                        0x80FF0000.toInt() // Invalid: Red
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
            val gridHeaderHeight = 10
            val gridHeight = grid.rows * 18
            val gridStartY = gridY + gridHeaderHeight
            
            if (mouseX >= gridX && mouseX < gridX + grid.columns * 18 &&
                mouseY >= gridStartY && mouseY < gridStartY + gridHeight) {
                
                val col = ((mouseX - gridX) / 18).toInt()
                val row = ((mouseY - gridStartY) / 18).toInt()

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
                            com.gmail.vincent031525.extractionshooter.network.payload.PickFromGridPayload(name, instance.x, instance.y)
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
                            com.gmail.vincent031525.extractionshooter.network.payload.PlaceToGridPayload(name, col, row, heldItemRotated)
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