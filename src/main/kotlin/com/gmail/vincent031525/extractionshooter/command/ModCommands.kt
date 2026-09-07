package com.gmail.vincent031525.extractionshooter.command

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.minecraft.commands.Commands
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent

@EventBusSubscriber(modid = Extractionshooter.ID)
object ModCommands {
    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        val root = Commands.literal("es")
        GiveCommand.register(root, event.buildContext)
        HealthCommand.register(root)
        KitCommand.register(root)
        InvCommand.register(root)
        GunCommand.register(root)
        DamageCommand.register(root)
        LootCommand.register(root)
        event.dispatcher.register(root)
    }
}
