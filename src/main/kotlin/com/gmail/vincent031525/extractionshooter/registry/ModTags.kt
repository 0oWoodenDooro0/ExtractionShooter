package com.gmail.vincent031525.extractionshooter.registry

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object ModTags {
    val HELMETS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("extractionshooter", "helmets"))
    val ARMORS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("extractionshooter", "armors"))
}
