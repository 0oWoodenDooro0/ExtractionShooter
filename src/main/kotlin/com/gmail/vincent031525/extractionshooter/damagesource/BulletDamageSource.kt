package com.gmail.vincent031525.extractionshooter.damagesource

import com.gmail.vincent031525.extractionshooter.datamap.AmmoStats
import net.minecraft.core.Holder
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3

class BulletDamageSource(
    typeHolder: Holder<DamageType>,
    val stats: AmmoStats,
    attacker: Entity?,
    val hitPos: Vec3
) : DamageSource(typeHolder, attacker)
