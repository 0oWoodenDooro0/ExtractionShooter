package com.gmail.vincent031525.extractionshooter.client.screen

data class GridPos(val x: Int, val y: Int)

object MenuLayout {
    private val layout = mutableMapOf<String, GridPos>()

    init {
        // Equipment Slots (Larger now)
        layout["helmet"] = GridPos(10, 10)         // 2x2
        layout["armor"] = GridPos(10, 50)          // 2x2
        layout["tactical_rig"] = GridPos(10, 90)   // 2x2
        layout["backpack"] = GridPos(10, 130)       // 2x2
        
        layout["primary_1"] = GridPos(50, 50)      // 4x2
        layout["primary_2"] = GridPos(50, 90)      // 4x2
        layout["pistol"] = GridPos(50, 130)        // 2x2 (Assuming 2x2 for consistency)

        // Pockets & Special (various sizes)
        layout["pockets_1"] = GridPos(130, 10)
        layout["pockets_2"] = GridPos(150, 10)
        layout["pockets_3"] = GridPos(170, 10)
        layout["pockets_4"] = GridPos(190, 10)
        layout["secure_container"] = GridPos(130, 40) // 3x3

        // Grids (from items) - These are dynamically named as slotName_grid
        layout["helmet_grid"] = GridPos(200, 130) // Moved away for now
        layout["armor_grid"] = GridPos(50, 10)
        layout["tactical_rig_grid"] = GridPos(130, 100)
        layout["backpack_grid"] = GridPos(10, 180)
        layout["primary_1_grid"] = GridPos(130, 140)
        layout["primary_2_grid"] = GridPos(130, 180)
        layout["pistol_grid"] = GridPos(130, 220)
    }

    fun getPos(name: String): GridPos {
        return layout[name] ?: GridPos(8, 20) // Default fallback position
    }
}
