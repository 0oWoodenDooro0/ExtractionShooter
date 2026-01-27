package com.gmail.vincent031525.extractionshooter.client.event

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
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
}
