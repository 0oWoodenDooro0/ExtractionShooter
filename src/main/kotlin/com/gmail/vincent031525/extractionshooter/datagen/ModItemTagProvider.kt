package com.gmail.vincent031525.extractionshooter.datagen

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.registry.ModTags
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Items
import net.neoforged.neoforge.common.data.ItemTagsProvider
import java.util.concurrent.CompletableFuture

class ModItemTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>,
) : ItemTagsProvider(output, lookupProvider, Extractionshooter.ID) {

        override fun addTags(provider: HolderLookup.Provider) {

            tag(ModTags.HELMETS).add(

                Items.LEATHER_HELMET,

                Items.IRON_HELMET,

                Items.GOLDEN_HELMET,

                Items.DIAMOND_HELMET,

                Items.NETHERITE_HELMET,

                Items.COPPER_HELMET

            )

    

            tag(ModTags.ARMORS).add(

                Items.LEATHER_CHESTPLATE,

                Items.IRON_CHESTPLATE,

                Items.GOLDEN_CHESTPLATE,

                Items.DIAMOND_CHESTPLATE,

                Items.NETHERITE_CHESTPLATE,

                Items.COPPER_CHESTPLATE

            )

        }
}
