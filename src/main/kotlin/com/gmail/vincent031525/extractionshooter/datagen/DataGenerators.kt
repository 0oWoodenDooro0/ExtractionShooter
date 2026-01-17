package com.gmail.vincent031525.extractionshooter.datagen

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.registry.ModDamageTypes
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.core.registries.Registries
import net.minecraft.world.damagesource.DamageEffects
import net.minecraft.world.damagesource.DamageScaling
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.damagesource.DeathMessageType
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.data.event.GatherDataEvent

@EventBusSubscriber(value = [Dist.CLIENT], modid = Extractionshooter.ID)
object DataGenerators {

    @SubscribeEvent
    fun gatherData(event: GatherDataEvent.Client) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val lookupProvider = event.lookupProvider

        val builder = RegistrySetBuilder().add(Registries.DAMAGE_TYPE) { bootstrap ->
            bootstrap.register(
                ModDamageTypes.BLEEDING, DamageType(
                    ModDamageTypes.BLEEDING.identifier().toString(),
                    DamageScaling.NEVER, 0.1f, DamageEffects.HURT, DeathMessageType.DEFAULT
                )
            )
        }

        generator.addProvider(
            true,
            ModItemModelProvider(packOutput)
        )

        generator.addProvider(
            true,
            ModLanguageProviderEnUs(packOutput)
        )
        generator.addProvider(
            true,
            ModLanguageProviderZhTw(packOutput)
        )

        generator.addProvider(
            true,
            ModDataMapProvider(packOutput, lookupProvider)
        )

        generator.addProvider(
            true,
            DatapackBuiltinEntriesProvider(packOutput, lookupProvider, builder, setOf(Extractionshooter.ID))
        )

        generator.addProvider(
            true,
            ModDamageTypeTagsProvider(packOutput, lookupProvider)
        )
    }
}