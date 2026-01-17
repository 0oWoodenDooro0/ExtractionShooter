package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.mobeffect.Bleeding
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.neoforged.neoforge.registries.DeferredRegister

object ModEffects {
    val MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Extractionshooter.ID)

    val FRACTURE = MOB_EFFECTS.register("fracture") { ->
        val effect = object : MobEffect(MobEffectCategory.HARMFUL, 0x8B4513) {}
        effect.addAttributeModifier(
            Attributes.MOVEMENT_SPEED,
            Identifier.fromNamespaceAndPath(Extractionshooter.ID, "fracture_movement_speed"),
            -0.30,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
        effect.addAttributeModifier(
            Attributes.JUMP_STRENGTH,
            Identifier.fromNamespaceAndPath(Extractionshooter.ID, "fracture_jump_strength"),
            -0.20,
            AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        )
        effect
    }

    val BLEEDING = MOB_EFFECTS.register("bleeding") { -> Bleeding() }
}