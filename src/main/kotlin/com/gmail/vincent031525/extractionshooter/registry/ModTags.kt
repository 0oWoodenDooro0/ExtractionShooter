package com.gmail.vincent031525.extractionshooter.registry

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item

object ModTags {
    val HELMETS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("extractionshooter", "helmets"))
    val ARMORS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("extractionshooter", "armors"))
    val BACKPACKS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("extractionshooter", "backpacks"))
    val RIGS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("extractionshooter", "rigs"))
    val PRIMARY_WEAPONS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("extractionshooter", "primary_weapons"))
    val PISTOLS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("extractionshooter", "pistols"))
    val GUNS: TagKey<Item> = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("extractionshooter", "guns"))
}
