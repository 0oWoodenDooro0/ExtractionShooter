package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.datacomponent.GunData
import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import net.minecraft.world.item.ItemStack

fun ItemStack.getGunData(): GunData? {
    return this.get(ModDataComponents.GUN_DATA)
}

fun ItemStack.getMagazineData(): MagazineData? {
    val fromGun = getMagazineStack().get(ModDataComponents.MAGAZINE_DATA)
    return fromGun ?: this.get(ModDataComponents.MAGAZINE_DATA)
}

fun ItemStack.getMagazineStack(): ItemStack {
    return this.getGunData()?.magazineStack ?: ItemStack.EMPTY
}