package com.gmail.vincent031525.extractionshooter.registry

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.mobeffect.Bleeding
import com.gmail.vincent031525.extractionshooter.mobeffect.Fracture
import com.gmail.vincent031525.extractionshooter.mobeffect.OnPainkillers
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.registries.DeferredRegister

object ModEffects {
    val MOB_EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, Extractionshooter.ID)

    val FRACTURE = MOB_EFFECTS.register("fracture") { -> Fracture() }

    val BLEEDING = MOB_EFFECTS.register("bleeding") { -> Bleeding() }

    val ON_PAINKILLERS = MOB_EFFECTS.register("on_painnkillers") { -> OnPainkillers() }
}