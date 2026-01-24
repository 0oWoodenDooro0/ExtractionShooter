package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.menu.GridInventoryMenu
import net.minecraft.core.registries.Registries
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister

object ModMenus {
    val MENUS: DeferredRegister<MenuType<*>> = DeferredRegister.create(Registries.MENU, Extractionshooter.ID)

    val GRID_INVENTORY_MENU: DeferredHolder<MenuType<*>, MenuType<GridInventoryMenu>> =
        MENUS.register("grid_inventory_menu") { ->
            IMenuTypeExtension.create(::GridInventoryMenu)
        }
}
