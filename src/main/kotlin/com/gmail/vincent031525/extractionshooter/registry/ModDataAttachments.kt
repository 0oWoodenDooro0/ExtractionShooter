package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.dataattachment.PlayerHealth
import com.gmail.vincent031525.extractionshooter.inventory.PlayerEquipment
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries

object ModDataAttachments {
    val ATTACHMENT_TYPES: DeferredRegister<AttachmentType<*>> =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Extractionshooter.ID)

    val PLAYER_HEALTH = ATTACHMENT_TYPES.register("player_health") { ->
        AttachmentType.builder { -> PlayerHealth() }
            .serialize(PlayerHealth.CODEC)
            .build()
    }

    val PLAYER_EQUIPMENT = ATTACHMENT_TYPES.register("player_equipment") { ->
        AttachmentType.builder { -> PlayerEquipment() }
            .serialize(PlayerEquipment.CODEC)
            .copyOnDeath()
            .build()
    }
}