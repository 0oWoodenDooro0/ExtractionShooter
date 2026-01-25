package com.gmail.vincent031525.extractionshooter.command

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent

@EventBusSubscriber(modid = Extractionshooter.ID)
object ModCommands {
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        GiveCommand.register(event.dispatcher, event.buildContext)
    }
}
