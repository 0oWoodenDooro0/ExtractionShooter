package com.gmail.vincent031525.extractionshooter

import com.gmail.vincent031525.extractionshooter.registry.*
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.Mod
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent
import net.neoforged.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

/**
 * Main mod class.
 */
@Mod(Extractionshooter.ID)
object Extractionshooter {
    const val ID = "extractionshooter"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

        init {

            ModDataComponents.DATA_COMPONENTS.register(MOD_BUS)

            ModItems.ITEMS.register(MOD_BUS)

            ModMenus.MENUS.register(MOD_BUS)

            ModCreativeTabs.CREATIVE_TABS.register(MOD_BUS)

            ModDataAttachments.ATTACHMENT_TYPES.register(MOD_BUS)

            ModEffects.MOB_EFFECTS.register(MOD_BUS)

    

            val obj = runForDist(clientTarget = {
            MOD_BUS.addListener(::onClientSetup)
        }, serverTarget = {
            MOD_BUS.addListener(::onServerSetup)
            "test"
        })

        println(obj)
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }

    @SubscribeEvent
    fun onCommonSetup(event: FMLCommonSetupEvent) {
        LOGGER.log(Level.INFO, "Hello! This is working!")
    }
}
