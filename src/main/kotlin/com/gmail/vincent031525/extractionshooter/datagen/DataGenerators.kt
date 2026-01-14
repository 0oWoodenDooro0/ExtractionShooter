package com.gmail.vincent031525.extractionshooter.datagen

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.data.DataGenerator
import net.minecraft.data.PackOutput
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent

@EventBusSubscriber(value = [Dist.CLIENT], modid = Extractionshooter.ID)
object DataGenerators {

    @SubscribeEvent
    fun gatherData(event: GatherDataEvent.Client) {
        val generator: DataGenerator = event.generator
        val packOutput: PackOutput = generator.packOutput

//        generator.addProvider(
//            true,
//            ModItemModelProvider(packOutput)
//        )
//
        generator.addProvider(
            true,
            ModLanguageProviderEnUs(packOutput)
        )
        generator.addProvider(
            true,
            ModLanguageProviderZhTw(packOutput)
        )
    }
}