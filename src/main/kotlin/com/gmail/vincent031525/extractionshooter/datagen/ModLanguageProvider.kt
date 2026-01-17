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
        add(ModItems.MAG_30_ITEM.get(), "30-round Magazine")
        add(ModItems.MAG_45_ITEM.get(), "45-round Magazine")
        add(ModItems.MAG_60_ITEM.get(), "60-round Magazine")
        add(ModItems.AMMO_556_ITEM.get(), "5.56x45mm NATO")

        add(KeyBindings.SWITCH_MODE_KEY.name, "Switch Fire Mode")
        add(KeyBindings.RELOAD_KEY.name, "Reload")

        add("message.extractionshooter.firemode_changed", "Fire Mode: %s")

        add("tooltip.extractionshooter.ammo", "Remaining Ammo: %s")

        add("firemode.extractionshooter.semi", "Semi")
        add("firemode.extractionshooter.burst", "Burst")
        add("firemode.extractionshooter.auto", "Auto")

        add("bodypart.extractionshooter.head", "Head")
        add("bodypart.extractionshooter.body", "Body")
        add("bodypart.extractionshooter.legs", "Legs")

        add("effect.extractionshooter.fracture", "Fracture")
        add("effect.extractionshooter.bleeding", "Bleeding")

        add("death.attack.bleeding", $$"%1$s bled out")
        add("death.attack.bleeding.player", $$"%1$s bled out while fighting %2$s")
    }
}

class ModLanguageProviderZhTw(output: PackOutput) : LanguageProvider(output, Extractionshooter.ID, "zh_tw") {

    override fun addTranslations() {
        add("itemGroup.extractionshooter", "搜打撤行動")

        add(ModItems.M4A1_ITEM.get(), "M4A1")
        add(ModItems.MAG_30_ITEM.get(), "30發彈匣")
        add(ModItems.MAG_45_ITEM.get(), "45發彈匣")
        add(ModItems.MAG_60_ITEM.get(), "60發彈鼓")
        add(ModItems.AMMO_556_ITEM.get(), "5.56x45毫米 NATO")

        add(KeyBindings.SWITCH_MODE_KEY.name, "切換射擊模式")
        add(KeyBindings.RELOAD_KEY.name, "換彈")

        add("message.extractionshooter.firemode_changed", "射擊模式: %s")

        add("tooltip.extractionshooter.ammo", "剩餘子彈: %s")

        add("firemode.extractionshooter.semi", "半自動")
        add("firemode.extractionshooter.burst", "三連發")
        add("firemode.extractionshooter.auto", "自動")

        add("bodypart.extractionshooter.head", "頭部")
        add("bodypart.extractionshooter.body", "身體")
        add("bodypart.extractionshooter.legs", "腿部")

        add("effect.extractionshooter.fracture", "骨折")
        add("effect.extractionshooter.bleeding", "流血")

        add("death.attack.bleeding", $$"%1$s 失血過多身亡")
        add("death.attack.bleeding.player", $$"%1$s 在與 %2$s 戰鬥後失血過多身亡")
    }
}
