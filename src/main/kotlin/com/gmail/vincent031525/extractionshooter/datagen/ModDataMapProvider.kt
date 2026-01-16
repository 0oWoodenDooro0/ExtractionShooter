package com.gmail.vincent031525.extractionshooter.datagen

import com.gmail.vincent031525.extractionshooter.datamap.AmmoStats
import com.gmail.vincent031525.extractionshooter.datamap.GunStats
import com.gmail.vincent031525.extractionshooter.datamap.MagazineStats
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import com.gmail.vincent031525.extractionshooter.registry.ModItems
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.DataMapProvider
import java.util.concurrent.CompletableFuture

class ModDataMapProvider(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>) :
    DataMapProvider(output, lookupProvider) {

    override fun gather(p0: HolderLookup.Provider) {
        this.builder(ModDataMaps.GUN_STATS)
            .replace(true)
            .add(
                ModItems.M4A1_ITEM, GunStats(
                    5.0f,
                    10,
                    100.0,
                    1.2f,
                    0.5f,
                    listOf(GunStats.FireMode.AUTO, GunStats.FireMode.BURST, GunStats.FireMode.SEMI)
                ), false
            )
        this.builder(ModDataMaps.MAGAZINE_STATS)
            .replace(true)
            .add(
                ModItems.M4A1_MAGAZINE_ITEM,
                MagazineStats(30, 40),
                false
            )
        this.builder(ModDataMaps.AMMO_STATS)
            .replace(true)
            .add(ModItems.AMMO_ITEM, AmmoStats(5), false)
    }
}