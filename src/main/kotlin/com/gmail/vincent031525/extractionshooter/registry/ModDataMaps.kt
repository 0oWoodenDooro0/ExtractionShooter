package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.datamap.AmmoStats
import com.gmail.vincent031525.extractionshooter.datamap.ArmorStats
import com.gmail.vincent031525.extractionshooter.datamap.GunStats
import com.gmail.vincent031525.extractionshooter.datamap.MagazineStats
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.registries.datamaps.DataMapType
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent

@EventBusSubscriber(modid = Extractionshooter.ID)
object ModDataMaps {

    val GUN_STATS = DataMapType.builder(
        Identifier.fromNamespaceAndPath(Extractionshooter.ID, "gun_stats"),
        Registries.ITEM, GunStats.CODEC
    ).synced(GunStats.CODEC, true).build()

    val MAGAZINE_STATS = DataMapType.builder(
        Identifier.fromNamespaceAndPath(Extractionshooter.ID, "magazine_stats"),
        Registries.ITEM, MagazineStats.CODEC
    ).synced(MagazineStats.CODEC, true).build()

    val AMMO_STATS = DataMapType.builder(
        Identifier.fromNamespaceAndPath(Extractionshooter.ID, "ammo_stats"),
        Registries.ITEM, AmmoStats.CODEC
    ).synced(AmmoStats.CODEC, true).build()

    val ARMOR_STATS = DataMapType.builder(
        Identifier.fromNamespaceAndPath(Extractionshooter.ID, "armor_stats"),
        Registries.ITEM, ArmorStats.CODEC
    ).synced(ArmorStats.CODEC, true).build()

    @SubscribeEvent
    fun registerDataMapTypes(event: RegisterDataMapTypesEvent) {
        event.register(GUN_STATS)
        event.register(MAGAZINE_STATS)
        event.register(AMMO_STATS)
        event.register(ARMOR_STATS)
    }
}