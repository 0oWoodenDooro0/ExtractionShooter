package com.gmail.vincent031525.extractionshooter.client.event

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.client.KeyBindings
import com.gmail.vincent031525.extractionshooter.datamap.GunStats
import com.gmail.vincent031525.extractionshooter.item.GunItem
import com.gmail.vincent031525.extractionshooter.network.payload.ReloadPayload
import com.gmail.vincent031525.extractionshooter.network.payload.ShootPayload
import com.gmail.vincent031525.extractionshooter.network.payload.SwitchModePayload
import com.gmail.vincent031525.extractionshooter.registry.ModDataComponents
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.network.ClientPacketDistributor

import com.gmail.vincent031525.extractionshooter.client.screen.GridInventoryScreen
import com.gmail.vincent031525.extractionshooter.registry.ModMenus
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import net.neoforged.neoforge.client.event.ScreenEvent

@EventBusSubscriber(modid = Extractionshooter.ID, value = [Dist.CLIENT])
object ClientModEvents {

    @SubscribeEvent
    fun onRegisterKeyMappings(event: RegisterKeyMappingsEvent) {
        event.register(KeyBindings.SWITCH_MODE_KEY)
        event.register(KeyBindings.RELOAD_KEY)
        event.register(KeyBindings.TOGGLE_INVENTORY_KEY)
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

    private var wasShooting = false

    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        while (KeyBindings.SWITCH_MODE_KEY.consumeClick()) {
            ClientPacketDistributor.sendToServer(SwitchModePayload())
        }

        while (KeyBindings.RELOAD_KEY.consumeClick()) {
            ClientPacketDistributor.sendToServer(ReloadPayload())
        }

        while (KeyBindings.TOGGLE_INVENTORY_KEY.consumeClick()) {
            ClientPacketDistributor.sendToServer(com.gmail.vincent031525.extractionshooter.network.payload.OpenInventoryPayload.INSTANCE)
        }

        handleShoot()
        handleClientBurstRecoil()
    }

    private fun handleShoot() {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return
        val stack = player.mainHandItem
        val item = stack.item
        if (item !is GunItem<*>) return

        if (!minecraft.options.keyAttack.isDown) {
            wasShooting = false
            return
        }

        if (!item.canShoot(player.level(), stack)) {
            wasShooting = true
            return
        }

        GunItem.getFireMode(stack)?.let {
            if (it == GunStats.FireMode.AUTO || !wasShooting) {
                ClientPacketDistributor.sendToServer(ShootPayload())
                applyRecoil(player, item.getGunStats())
            }
            wasShooting = true
        }
    }

    private fun handleClientBurstRecoil() {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return
        val stack = player.mainHandItem
        val item = stack.item as? GunItem<*> ?: return

        val data = stack.get(ModDataComponents.GUN_DATA.get()) ?: return

        if (data.burstRemaining > 0 && data.burstTickDelay <= 0) {
            applyRecoil(player, item.getGunStats())
        }
    }

    private fun applyRecoil(player: Player, stats: GunStats) {

        val randomHorizontal = (Math.random().toFloat() * 2 - 1) * stats.horizontalRecoil
        player.xRot -= stats.verticalRecoil
        player.yRot += randomHorizontal
    }

}
