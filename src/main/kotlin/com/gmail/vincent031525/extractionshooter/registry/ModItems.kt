package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.client.renderer.M4A1Renderer
import com.gmail.vincent031525.extractionshooter.datacomponent.GunData
import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.item.AmmoItem
import com.gmail.vincent031525.extractionshooter.item.GunItem
import com.gmail.vincent031525.extractionshooter.item.MagazineItem
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(Extractionshooter.ID)

    val M4A1_ITEM: DeferredItem<GunItem<M4A1Renderer>> = ITEMS.registerItem(
        "m4a1",
        { properties ->
            GunItem(
                properties,
                GunItem.GunStats(
                    5.0f,
                    10,
                    100.0,
                    1.2f,
                    0.5f,
                    listOf(GunItem.FireMode.AUTO, GunItem.FireMode.BURST, GunItem.FireMode.SEMI)
                ), ::M4A1Renderer
            )
        }) { ->
        Item.Properties().component(ModDataComponents.GUN_DATA, GunData())
    }

    val M4A1_MAGAZINE_ITEM: DeferredItem<MagazineItem> =
        ITEMS.registerItem(
            "m4a1_magazine",
            { properties ->
                MagazineItem(
                    properties,
                    MagazineItem.MagazineStats(
                        30,
                        40
                    )
                )
            }) { ->
            Item.Properties().component(ModDataComponents.MAGAZINE_DATA, MagazineData())
        }

    val AMMO_ITEM: DeferredItem<AmmoItem> = ITEMS.registerItem("ammo", ::AmmoItem)
}