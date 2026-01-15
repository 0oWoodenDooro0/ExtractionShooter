package com.gmail.vincent031525.extractionshooter.client.model

import com.gmail.vincent031525.extractionshooter.Extractionshooter
import com.gmail.vincent031525.extractionshooter.client.renderer.M4A1Renderer
import com.gmail.vincent031525.extractionshooter.item.GunItem
import net.minecraft.resources.Identifier
import software.bernie.geckolib.model.DefaultedGeoModel

class M4A1Model :
    DefaultedGeoModel<GunItem<M4A1Renderer>>(Identifier.fromNamespaceAndPath(Extractionshooter.ID, "m4a1")) {

    override fun subtype(): String {
        return "item"
    }
}
