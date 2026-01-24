package com.gmail.vincent031525.extractionshooter.datagen

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.BlockTagsProvider
import java.util.concurrent.CompletableFuture

class ModBlockTagProvider(
    output: PackOutput,
    lookupProvider: CompletableFuture<HolderLookup.Provider>
) : BlockTagsProvider(output, lookupProvider, Extractionshooter.ID) {
    override fun addTags(provider: HolderLookup.Provider) {
        // Empty
    }
}