package com.gmail.vincent031525.extractionshooter.network

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.network.payload.ReloadPayload
import com.gmail.vincent031525.extractionshooter.network.payload.ShootPayload
import com.gmail.vincent031525.extractionshooter.network.payload.SwitchModePayload
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.PayloadRegistrar

@EventBusSubscriber(modid = Extractionshooter.ID)
object ModMessages {

    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val registrar: PayloadRegistrar = event.registrar("1")

        registrar.playToServer(
            SwitchModePayload.TYPE,
            SwitchModePayload.STREAM_CODEC,
            ServerPayloadHandler::handleSwitchMode
        )
        registrar.playToServer(
            ShootPayload.TYPE,
            ShootPayload.STREAM_CODEC,
            ServerPayloadHandler::handleShoot
        )
        registrar.playToServer(
            ReloadPayload.TYPE,
            ReloadPayload.STREAM_CODEC,
            ServerPayloadHandler::handleReload
        )
    }
}