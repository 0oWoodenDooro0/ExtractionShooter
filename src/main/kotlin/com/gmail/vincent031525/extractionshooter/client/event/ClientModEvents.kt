package com.gmail.vincent031525.extractionshooter.client.event

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.client.KeyBindings
import com.gmail.vincent031525.extractionshooter.client.gun.ClientGunHandler
import com.gmail.vincent031525.extractionshooter.client.screen.GridInventoryScreen
import com.gmail.vincent031525.extractionshooter.client.util.InventoryInterceptor
import com.gmail.vincent031525.extractionshooter.network.payload.OpenInventoryPayload
import com.gmail.vincent031525.extractionshooter.network.payload.ReloadPayload
import com.gmail.vincent031525.extractionshooter.network.payload.SwitchModePayload
import com.gmail.vincent031525.extractionshooter.registry.ModMenus
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.ScreenEvent
import net.neoforged.neoforge.client.network.ClientPacketDistributor

@EventBusSubscriber(modid = Extractionshooter.ID, value = [Dist.CLIENT])
object ClientModEvents {

    @SubscribeEvent
    fun onScreenOpening(event: ScreenEvent.Opening) {
        val screen = event.newScreen
        if (screen != null && InventoryInterceptor.shouldIntercept(screen)) {
            event.isCanceled = true
            ClientPacketDistributor.sendToServer(OpenInventoryPayload.INSTANCE)
        }
    }

    @SubscribeEvent
    fun onRegisterKeyMappings(event: RegisterKeyMappingsEvent) {
        event.register(KeyBindings.SWITCH_MODE_KEY)
        event.register(KeyBindings.RELOAD_KEY)
    }

    @SubscribeEvent
    fun onRegisterMenuScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenus.GRID_INVENTORY_MENU.get(), ::GridInventoryScreen)
    }

    @SubscribeEvent
    fun onScreenMousePressed(event: ScreenEvent.MouseButtonPressed.Pre) {
        val screen = event.screen
        if (screen is GridInventoryScreen) {
            if (screen.onMouseClick(event.mouseX, event.mouseY, event.button)) {
                event.isCanceled = true
            }
        }
    }

    @SubscribeEvent
    fun onScreenKeyPressed(event: ScreenEvent.KeyPressed.Pre) {
        val screen = event.screen
        if (screen is GridInventoryScreen) {
            if (screen.onKeyPress(event.keyCode, event.scanCode, event.modifiers)) {
                event.isCanceled = true
            }
        }
    }
}

@EventBusSubscriber(modid = Extractionshooter.ID, value = [Dist.CLIENT])
object ClientGameEvents {

    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        while (KeyBindings.SWITCH_MODE_KEY.consumeClick()) {
            ClientPacketDistributor.sendToServer(SwitchModePayload())
        }

        while (KeyBindings.RELOAD_KEY.consumeClick()) {
            ClientPacketDistributor.sendToServer(ReloadPayload())
        }

        ClientGunHandler.onClientTick()
    }
}
