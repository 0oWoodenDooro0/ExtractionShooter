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
        { properties -> GunItem(properties, ::M4A1Renderer) }) { ->
        Item.Properties().component(ModDataComponents.GUN_DATA, GunData())
    }

    val M4A1_MAGAZINE_ITEM: DeferredItem<MagazineItem> =
        ITEMS.registerItem(
            "m4a1_magazine",
            { properties -> MagazineItem(properties) }) { ->
            Item.Properties().component(ModDataComponents.MAGAZINE_DATA, MagazineData())
        }

    val AMMO_ITEM: DeferredItem<AmmoItem> =
        ITEMS.registerItem("ammo") { properties -> AmmoItem(properties) }
}