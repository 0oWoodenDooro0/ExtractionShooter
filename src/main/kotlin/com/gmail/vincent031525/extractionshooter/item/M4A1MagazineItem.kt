package com.gmail.vincent031525.extractionshooter.item

class M4A1MagazineItemItem(properties: Properties) : MagazineItem(properties) {

    override val magazineStats = MagazineStats(
        maxAmmo = 30,
        reloadTick = 40
    )
}
