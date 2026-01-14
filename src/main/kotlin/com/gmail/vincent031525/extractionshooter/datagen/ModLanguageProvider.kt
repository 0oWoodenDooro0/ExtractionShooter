package com.gmail.vincent031525.extractionshooter.datagen

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.client.KeyBindings
import com.gmail.vincent031525.extractionshooter.registry.ModItems
import net.minecraft.data.PackOutput
import net.neoforged.neoforge.common.data.LanguageProvider

class ModLanguageProviderEnUs(output: PackOutput) : LanguageProvider(output, Extractionshooter.ID, "en_us") {

    override fun addTranslations() {
        add("itemGroup.extractionshooter", "Extraction Shooter")

        add(ModItems.M4A1_ITEM.get(), "M4A1")

        add(KeyBindings.SWITCH_MODE_KEY.name, "Switch Fire Mode")
        add(KeyBindings.RELOAD_KEY.name, "Reload")

        add("message.extractionshooter.firemode_changed", "Fire Mode: %s")

        add("tooltip.extractionshooter.ammo", "Remaining Ammo: %s")

        add("firemode.extractionshooter.semi", "SEMI")
        add("firemode.extractionshooter.burst", "BURST")
        add("firemode.extractionshooter.auto", "AUTO")
    }
}

class ModLanguageProviderZhTw(output: PackOutput) : LanguageProvider(output, Extractionshooter.ID, "zh_tw") {

    override fun addTranslations() {
        add("itemGroup.extractionshooter", "搜打撤行動")

        add(ModItems.M4A1_ITEM.get(), "M4A1")

        add(KeyBindings.SWITCH_MODE_KEY.name, "切換射擊模式")
        add(KeyBindings.RELOAD_KEY.name, "換彈")

        add("message.extractionshooter.firemode_changed", "射擊模式: %s")

        add("tooltip.extractionshooter.ammo", "剩餘子彈: %s")

        add("firemode.extractionshooter.semi", "半自動")
        add("firemode.extractionshooter.burst", "三連發")
        add("firemode.extractionshooter.auto", "自動")
    }
}
