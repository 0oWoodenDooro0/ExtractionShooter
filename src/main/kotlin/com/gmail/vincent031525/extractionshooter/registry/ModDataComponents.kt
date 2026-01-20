package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.datacomponent.GunData
import com.gmail.vincent031525.extractionshooter.datacomponent.MagazineData
import com.gmail.vincent031525.extractionshooter.inventory.GridInventory
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister


object ModDataComponents {
    val DATA_COMPONENTS: DeferredRegister<DataComponentType<*>> =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, Extractionshooter.ID)

    val GUN_DATA: DeferredHolder<DataComponentType<*>, DataComponentType<GunData>> =
        DATA_COMPONENTS.register("gun_data") { ->
            DataComponentType.builder<GunData>().persistent(GunData.CODEC)
                .networkSynchronized(GunData.STREAM_CODEC).build()
        }

    val MAGAZINE_DATA: DeferredHolder<DataComponentType<*>, DataComponentType<MagazineData>> =
        DATA_COMPONENTS.register("magazine_data") { ->
            DataComponentType.builder<MagazineData>().persistent(MagazineData.CODEC)
                .networkSynchronized(MagazineData.STREAM_CODEC).build()
        }

    val GRID_INVENTORY = DATA_COMPONENTS.register("grid_inventory") { ->
        DataComponentType.builder<GridInventory>().persistent(GridInventory.CODEC)
            .networkSynchronized(GridInventory.STREAM_CODEC).build()
    }
}