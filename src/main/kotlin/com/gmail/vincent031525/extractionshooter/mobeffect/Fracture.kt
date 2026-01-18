package com.gmail.vincent031525.extractionshooter.mobeffect

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.registry.ModEffects
import net.minecraft.resources.Identifier
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes

class Fracture : MobEffect(MobEffectCategory.HARMFUL, 0x8B4513) {

    companion object {
        val FRACTURE_MOVEMENT_SPEED_ID =
            Identifier.fromNamespaceAndPath(Extractionshooter.ID, "fracture_movement_speed")
        val FRACTURE_JUMP_STRENGTH_ID = Identifier.fromNamespaceAndPath(Extractionshooter.ID, "fracture_jump_strength")
    }

    override fun applyEffectTick(level: ServerLevel, entity: LivingEntity, amplifier: Int): Boolean {
        val speedAttr = entity.getAttribute(Attributes.MOVEMENT_SPEED) ?: return true

        val hasPainkiller = entity.hasEffect(ModEffects.ON_PAINKILLERS)

        if (hasPainkiller) {
            if (speedAttr.getModifier(FRACTURE_MOVEMENT_SPEED_ID) != null) {
                speedAttr.removeModifier(FRACTURE_MOVEMENT_SPEED_ID)
            }
            if (speedAttr.getModifier(FRACTURE_JUMP_STRENGTH_ID) != null) {
                speedAttr.removeModifier(FRACTURE_JUMP_STRENGTH_ID)
            }
        } else {
            if (speedAttr.getModifier(FRACTURE_MOVEMENT_SPEED_ID) == null) {
                speedAttr.addTransientModifier(
                    AttributeModifier(
                        FRACTURE_MOVEMENT_SPEED_ID,
                        -0.30,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                )
            }
            if (speedAttr.getModifier(FRACTURE_JUMP_STRENGTH_ID) == null) {
                speedAttr.addTransientModifier(
                    AttributeModifier(
                        FRACTURE_JUMP_STRENGTH_ID,
                        -0.20,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                    )
                )
            }
        }
        return true
    }

    override fun shouldApplyEffectTickThisTick(tickCount: Int, amplifier: Int): Boolean {
        return true
    }
}
