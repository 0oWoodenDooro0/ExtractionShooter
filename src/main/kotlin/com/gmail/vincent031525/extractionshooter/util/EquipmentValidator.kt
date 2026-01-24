package com.gmail.vincent031525.extractionshooter.util

import com.gmail.vincent031525.extractionshooter.registry.ModTags
import net.minecraft.world.item.ItemStack

object EquipmentValidator {
    fun isValid(slot: String, stack: ItemStack): Boolean {
        if (stack.isEmpty) return true
        
        return when (slot) {
            "helmet" -> stack.`is`(ModTags.HELMETS)
            "armor" -> stack.`is`(ModTags.ARMORS)
            // For now, we allow others, but we can refine this
            "tactical_rig" -> true 
            "backpack" -> true
            "primary_1", "primary_2" -> true
            "pistol" -> true
            else -> true
        }
    }
}