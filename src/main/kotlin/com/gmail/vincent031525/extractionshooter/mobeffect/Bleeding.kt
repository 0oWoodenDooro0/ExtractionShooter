package com.gmail.vincent031525.extractionshooter.mobeffect

import com.gmail.vincent031525.extractionshooter.registry.ModDamageTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity

class Bleeding : MobEffect(MobEffectCategory.HARMFUL, 0x8B0000) {

    override fun applyEffectTick(level: ServerLevel, entity: LivingEntity, amplifier: Int): Boolean {
        val damage = if (amplifier > 0) 4.0f else 2.0f
        entity.hurtServer(level, entity.damageSources().source(ModDamageTypes.BLEEDING), damage)
        return true
    }

    override fun shouldApplyEffectTickThisTick(tickCount: Int, amplifier: Int): Boolean {
        // 一級（輕度流血）：每 4 秒（80 ticks）
        // 二級（重度流血）：每 2 秒（40 ticks）
        val interval = if (amplifier > 0) 40 else 80
        return tickCount % interval == 0
    }
}
