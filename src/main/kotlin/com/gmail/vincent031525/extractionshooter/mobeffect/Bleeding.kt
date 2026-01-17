package com.gmail.vincent031525.extractionshooter.mobeffect

import com.gmail.vincent031525.extractionshooter.registry.ModDamageTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity

class Bleeding : MobEffect(MobEffectCategory.HARMFUL, 0x8B0000) {

    override fun applyEffectTick(level: ServerLevel, entity: LivingEntity, amplifier: Int): Boolean {
        entity.hurtServer(level, entity.damageSources().source(ModDamageTypes.BLEEDING), (amplifier + 1).toFloat())
        return true
    }

    override fun shouldApplyEffectTickThisTick(tickCount: Int, amplifier: Int): Boolean {
        return tickCount % 40 == 0
    }
}