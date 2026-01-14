package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.datacomponent.GunData
import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.item.M4A1Item
import com.gmail.vincent031525.extractionshooter.item.M4A1MagazineItemItem
import com.gmail.vincent031525.extractionshooter.item.MagazineItem
import net.minecraft.world.item.Item
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

object ModItems {
    val ITEMS: DeferredRegister.Items = DeferredRegister.createItems(Extractionshooter.ID)

    val M4A1_ITEM: DeferredItem<M4A1Item> = ITEMS.registerItem("m4a1", ::M4A1Item) { ->
        Item.Properties().component(
            ModDataComponents.GUN_DATA.get(),
            GunData(fireMode = GunData.FireMode.AUTO)
        )
    }

    val M4A1_MAGAZINE_ITEM: DeferredItem<MagazineItem> =
        ITEMS.registerItem("m4a1_magazine", ::M4A1MagazineItemItem) { ->
            Item.Properties().component(ModDataComponents.MAGAZINE_DATA.get(), MagazineData(ammoCount = 30))
        }
}