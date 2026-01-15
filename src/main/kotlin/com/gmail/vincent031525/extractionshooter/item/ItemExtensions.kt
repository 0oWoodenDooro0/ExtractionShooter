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

fun ItemStack.nextFireMode(): Int {
    val data = this.getGunData() ?: return -1
    val stats = (this.item as GunItem<*>).gunStats
    val nextFireModeIndex = (data.fireModeIndex + 1) % stats.fireModeCycle.size
    this.set(ModDataComponents.GUN_DATA, data.copy(fireModeIndex = nextFireModeIndex))
    return nextFireModeIndex
}

fun ItemStack.getFireMode(): GunItem.FireMode? {
    val data = this.getGunData() ?: return null
    val stats = (this.item as GunItem<*>).gunStats
    return stats.fireModeCycle[data.fireModeIndex]
}