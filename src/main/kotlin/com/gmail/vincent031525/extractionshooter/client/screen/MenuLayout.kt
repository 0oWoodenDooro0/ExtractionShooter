package com.gmail.vincent031525.extractionshooter.client.screen

data class GridPos(val x: Int, val y: Int)

object MenuLayout {
    private val layout = mutableMapOf<String, GridPos>()

    init {
        // Equipment Slots (1x1 typically)
        layout["helmet"] = GridPos(10, 20)
        layout["armor"] = GridPos(10, 40)
        layout["tactical_rig"] = GridPos(10, 60)
        layout["backpack"] = GridPos(10, 80)
        layout["primary_1"] = GridPos(10, 100)
        layout["primary_2"] = GridPos(10, 120)
        layout["pistol"] = GridPos(10, 140)

        // Pockets & Special (various sizes)
        layout["pockets_1"] = GridPos(50, 20)
        layout["pockets_2"] = GridPos(70, 20)
        layout["pockets_3"] = GridPos(90, 20)
        layout["pockets_4"] = GridPos(110, 20)
        layout["secure_container"] = GridPos(50, 50)

        // Grids (from items) - These are dynamically named as slotName_grid
        layout["helmet_grid"] = GridPos(150, 20)
        layout["armor_grid"] = GridPos(150, 50)
        layout["tactical_rig_grid"] = GridPos(150, 80)
        layout["backpack_grid"] = GridPos(150, 140)
        layout["primary_1_grid"] = GridPos(50, 120)
        layout["primary_2_grid"] = GridPos(50, 150)
        layout["pistol_grid"] = GridPos(50, 180)
    }

    fun getPos(name: String): GridPos {
        return layout[name] ?: GridPos(8, 20) // Default fallback position
    }
}
