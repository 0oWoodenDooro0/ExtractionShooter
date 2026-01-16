package com.gmail.vincent031525.extractionshooter.item

import com.gmail.vincent031525.extractionshooter.datamap.AmmoStats
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Item

class AmmoItem(properties: Properties) : Item(properties) {

    fun getAmmoStats(): AmmoStats = BuiltInRegistries.ITEM.wrapAsHolder(this).getData(ModDataMaps.AMMO_STATS)!!
}
