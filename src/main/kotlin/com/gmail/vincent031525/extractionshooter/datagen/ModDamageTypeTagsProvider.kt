package com.gmail.vincent031525.extractionshooter.datagen

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.registry.ModDamageTypes
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.tags.DamageTypeTagsProvider
import net.minecraft.tags.DamageTypeTags
import java.util.concurrent.CompletableFuture

class ModDamageTypeTagsProvider(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>) :
    DamageTypeTagsProvider(output, lookupProvider, Extractionshooter.ID) {

    override fun addTags(provider: HolderLookup.Provider) {
        tag(DamageTypeTags.NO_KNOCKBACK)
            .addOptional(ModDamageTypes.BLEEDING)
    }
}