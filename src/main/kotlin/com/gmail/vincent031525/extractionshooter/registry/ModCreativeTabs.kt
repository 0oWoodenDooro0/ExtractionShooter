package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

object ModCreativeTabs {
    val CREATIVE_TABS: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Extractionshooter.ID)

    val TAB: Supplier<CreativeModeTab> = CREATIVE_TABS.register("extractionshooter") { ->
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup." + Extractionshooter.ID))
            .icon { ItemStack(ModItems.M4A1_ITEM.get()) }
            .displayItems { parameters, output ->
                output.accept(ModItems.M4A1_ITEM)
                output.accept(ModItems.M4A1_MAGAZINE_ITEM)
                output.accept(ModItems.AMMO_ITEM)
            }
            .build()
    }
}