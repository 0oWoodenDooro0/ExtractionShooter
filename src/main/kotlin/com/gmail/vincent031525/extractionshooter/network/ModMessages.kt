package com.gmail.vincent031525.extractionshooter.network

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.network.payload.*
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
        registrar.playToClient(
            SyncEquipmentPayload.ID,
            SyncEquipmentPayload.STREAM_CODEC,
            ClientPayloadHandler::handleSyncEquipment
        )
        registrar.playToServer(
            OpenInventoryPayload.ID,
            OpenInventoryPayload.STREAM_CODEC,
            ServerPayloadHandler::handleOpenInventory
        )
        registrar.playToServer(
            PickFromGridPayload.ID,
            PickFromGridPayload.STREAM_CODEC,
            ServerPayloadHandler::handlePickFromGrid
        )
        registrar.playToServer(
            PlaceToGridPayload.ID,
            PlaceToGridPayload.STREAM_CODEC,
            ServerPayloadHandler::handlePlaceToGrid
        )
        registrar.playToServer(
            InteractGridItemPayload.ID,
            InteractGridItemPayload.STREAM_CODEC,
            ServerPayloadHandler::handleInteractGridItem
        )
    }
}
