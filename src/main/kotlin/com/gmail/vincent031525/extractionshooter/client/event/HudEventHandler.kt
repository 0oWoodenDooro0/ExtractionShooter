package com.gmail.vincent031525.extractionshooter.client.event

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.client.gui.HealthHudOverlay
import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderGuiEvent
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers

@EventBusSubscriber(modid = Extractionshooter.ID, value = [Dist.CLIENT])
object HudEventHandler {

    @SubscribeEvent
    fun onRenderGuiLayerPre(event: RenderGuiLayerEvent.Pre) {
        if (event.name != VanillaGuiLayers.CROSSHAIR) {
            event.isCanceled = true
        }
    }

    @SubscribeEvent
    fun onRenderGuiPost(event: RenderGuiEvent.Post) {
        val mc = Minecraft.getInstance()
        if (mc.screen == null) {
            HealthHudOverlay.render(event.guiGraphics)
        }
    }
}
