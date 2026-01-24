package com.gmail.vincent031525.extractionshooter.client.util

import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen

object InventoryInterceptor {
    fun shouldIntercept(screen: Screen): Boolean {
        return screen is InventoryScreen || screen is CreativeModeInventoryScreen
    }
}
