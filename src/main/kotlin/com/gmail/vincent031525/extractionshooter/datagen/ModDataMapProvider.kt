package com.gmail.vincent031525.extractionshooter.datagen

import com.gmail.vincent031525.extractionshooter.datamap.*
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import com.gmail.vincent031525.extractionshooter.registry.ModItems
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Items
import net.neoforged.neoforge.common.data.DataMapProvider
import java.util.concurrent.CompletableFuture

class ModDataMapProvider(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>) :
    DataMapProvider(output, lookupProvider) {

    override fun gather(p0: HolderLookup.Provider) {
        builder(ModDataMaps.GUN_STATS)
            .replace(true)
            .add(
                ModItems.M4A1_ITEM, GunStats(
                    10,
                    100.0,
                    1.2f,
                    0.5f,
                    listOf(GunStats.FireMode.AUTO, GunStats.FireMode.SEMI)
                ), false
            )
        builder(ModDataMaps.MAGAZINE_STATS)
            .replace(true)
            .add(
                ModItems.MAG_30_ITEM,
                MagazineStats(30, 20),
                false
            )
            .add(
                ModItems.MAG_45_ITEM,
                MagazineStats(45, 32),
                false
            )
            .add(
                ModItems.MAG_60_ITEM,
                MagazineStats(60, 40),
                false
            )
        builder(ModDataMaps.AMMO_STATS)
            .replace(true)
            .add(ModItems.AMMO_556_ITEM, AmmoStats(12f, 35f), false)
        builder(ModDataMaps.ARMOR_STATS)
            .replace(true)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.LEATHER_HELMET), ArmorStats(1, 40f, 0.25f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.LEATHER_CHESTPLATE), ArmorStats(1, 60f, 0.25f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.CHAINMAIL_HELMET), ArmorStats(2, 50f, 0.20f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.CHAINMAIL_CHESTPLATE), ArmorStats(2, 80f, 0.20f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.IRON_HELMET), ArmorStats(3, 60f, 0.15f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.IRON_CHESTPLATE), ArmorStats(3, 100f, 0.15f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.GOLDEN_HELMET), ArmorStats(4, 50f, 0.12f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.GOLDEN_CHESTPLATE), ArmorStats(4, 70f, 0.12f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.DIAMOND_HELMET), ArmorStats(5, 120f, 0.08f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.DIAMOND_CHESTPLATE), ArmorStats(5, 180f, 0.08f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.NETHERITE_HELMET), ArmorStats(6, 150f, 0.05f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.NETHERITE_CHESTPLATE), ArmorStats(6, 220f, 0.05f), false)

        builder(ModDataMaps.ITEM_SIZE)
            .replace(true)
            .add(ModItems.M4A1_ITEM, ItemSize(5, 2), false)
            .add(ModItems.MAG_30_ITEM, ItemSize(1, 2), false)
            .add(ModItems.MAG_45_ITEM, ItemSize(1, 3), false)
            .add(ModItems.MAG_60_ITEM, ItemSize(1, 2), false)
            .add(ModItems.AMMO_556_ITEM, ItemSize(1, 1), false)
            .add(ModItems.BANDAGE_ITEM, ItemSize(1, 1), false)
            .add(ModItems.SPLINT_ITEM, ItemSize(1, 1), false)
            .add(ModItems.PAINKILLERS_ITEM, ItemSize(1, 1), false)
            .add(ModItems.SURGERY_KIT_ITEM, ItemSize(2, 1), false)
            .add(ModItems.MEDKIT_SMALL_ITEM, ItemSize(2, 1), false)
            .add(ModItems.MEDKIT_LARGE_ITEM, ItemSize(1, 2), false)
    }
}