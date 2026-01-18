package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.client.renderer.M4A1Renderer
import com.gmail.vincent031525.extractionshooter.datacomponent.GunData
import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.item.*
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(Extractionshooter.ID)

    val M4A1_ITEM: DeferredItem<GunItem<M4A1Renderer>> = ITEMS.registerItem(
        "m4a1",
        { properties -> GunItem(properties, ::M4A1Renderer) }) { ->
        Item.Properties().component(ModDataComponents.GUN_DATA, GunData())
    }

    val MAG_30_ITEM: DeferredItem<MagazineItem> =
        ITEMS.registerItem(
            "mag_30",
            { properties -> MagazineItem(properties) }) { ->
            Item.Properties().component(ModDataComponents.MAGAZINE_DATA, MagazineData())
        }

    val MAG_45_ITEM: DeferredItem<MagazineItem> =
        ITEMS.registerItem(
            "mag_45",
            { properties -> MagazineItem(properties) }) { ->
            Item.Properties().component(ModDataComponents.MAGAZINE_DATA, MagazineData())
        }

    val MAG_60_ITEM: DeferredItem<MagazineItem> =
        ITEMS.registerItem(
            "mag_60",
            { properties -> MagazineItem(properties) }) { ->
            Item.Properties().component(ModDataComponents.MAGAZINE_DATA, MagazineData())
        }

    val AMMO_556_ITEM: DeferredItem<AmmoItem> =
        ITEMS.registerItem("ammo_556") { properties -> AmmoItem(properties) }

    val BANDAGE_ITEM = ITEMS.registerItem("bandage") { properties -> BandageItem(properties.durability(2)) }
    val SPLINT_ITEM = ITEMS.registerItem("splint") { properties -> SplintItem(properties.durability(1)) }
    val SURGERY_KIT_ITEM =
        ITEMS.registerItem("surgery_kit") { properties -> SurgeryKitItem(properties.durability(3)) }
    val PAINKILLERS_ITEM = ITEMS.registerItem("painkillers") { properties ->
        PainkillersItem(properties.durability(6))
    }
    val MEDKIT_SMALL_ITEM = ITEMS.registerItem("medkit_small") { properties ->
        HealItem(properties.durability(220), 70f, 60)
    }
    val MEDKIT_LARGE_ITEM = ITEMS.registerItem("medkit_large") { properties ->
        HealItem(properties.durability(400), 85f, 60)
    }
}