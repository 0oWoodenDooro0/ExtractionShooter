package com.gmail.vincent031525.extractionshooter.util

import com.gmail.vincent031525.extractionshooter.registry.ModTags
import net.minecraft.world.item.ItemStack

object EquipmentValidator {
    fun isValid(slot: String, stack: ItemStack): Boolean {
        if (stack.isEmpty) return true
        
        return when (slot) {
            "helmet" -> stack.`is`(ModTags.HELMETS)
            "armor" -> stack.`is`(ModTags.ARMORS)
            "tactical_rig" -> stack.`is`(ModTags.RIGS)
            "backpack" -> stack.`is`(ModTags.BACKPACKS)
            "primary_1", "primary_2" -> stack.`is`(ModTags.PRIMARY_WEAPONS)
            "pistol" -> stack.`is`(ModTags.PISTOLS)
            else -> true
        }
    }
}