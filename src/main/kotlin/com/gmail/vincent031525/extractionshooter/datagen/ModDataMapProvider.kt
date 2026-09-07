package com.gmail.vincent031525.extractionshooter.datagen

import com.gmail.vincent031525.extractionshooter.datamap.*
import com.gmail.vincent031525.extractionshooter.registry.ModDataMaps
import com.gmail.vincent031525.extractionshooter.registry.ModItems
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.neoforged.neoforge.common.data.DataMapProvider
import java.util.concurrent.CompletableFuture

class ModDataMapProvider(output: PackOutput, lookupProvider: CompletableFuture<HolderLookup.Provider>) :
    DataMapProvider(output, lookupProvider) {

    private data class LootDef(
        val item: Item,
        val size: ItemSize,
        val rarity: ItemRarity,
        val value: Int
    )

    override fun gather(p0: HolderLookup.Provider) {
        builder(ModDataMaps.GUN_STATS)
            .replace(true)
            .add(
                ModItems.M4A1_ITEM, GunStats(
                    10,
                    100.0,
                    1.2f,
                    0.5f,
                    listOf(GunStats.FireMode.AUTO, GunStats.FireMode.SEMI)
                ), false
            )
        builder(ModDataMaps.MAGAZINE_STATS)
            .replace(true)
            .add(
                ModItems.MAG_30_ITEM,
                MagazineStats(30, 20),
                false
            )
            .add(
                ModItems.MAG_45_ITEM,
                MagazineStats(45, 32),
                false
            )
            .add(
                ModItems.MAG_60_ITEM,
                MagazineStats(60, 40),
                false
            )
        builder(ModDataMaps.AMMO_STATS)
            .replace(true)
            .add(ModItems.AMMO_556_ITEM, AmmoStats(12f, 35f), false)
        builder(ModDataMaps.ARMOR_STATS)
            .replace(true)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.LEATHER_HELMET), ArmorStats(1, 40f, 0.25f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.LEATHER_CHESTPLATE), ArmorStats(1, 60f, 0.25f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.CHAINMAIL_HELMET), ArmorStats(2, 50f, 0.20f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.CHAINMAIL_CHESTPLATE), ArmorStats(2, 80f, 0.20f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.IRON_HELMET), ArmorStats(3, 60f, 0.15f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.IRON_CHESTPLATE), ArmorStats(3, 100f, 0.15f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.GOLDEN_HELMET), ArmorStats(4, 50f, 0.12f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.GOLDEN_CHESTPLATE), ArmorStats(4, 70f, 0.12f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.DIAMOND_HELMET), ArmorStats(5, 120f, 0.08f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.DIAMOND_CHESTPLATE), ArmorStats(5, 180f, 0.08f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.NETHERITE_HELMET), ArmorStats(6, 150f, 0.05f), false)
            .add(BuiltInRegistries.ITEM.wrapAsHolder(Items.NETHERITE_CHESTPLATE), ArmorStats(6, 220f, 0.05f), false)

        val itemSizeBuilder = builder(ModDataMaps.ITEM_SIZE).replace(true)
        val lootStatsBuilder = builder(ModDataMaps.LOOT_STATS).replace(true)

        // Register Mod Items Sizes & Loot Stats
        itemSizeBuilder
            .add(ModItems.M4A1_ITEM, ItemSize(5, 2), false)
            .add(ModItems.MAG_30_ITEM, ItemSize(1, 2), false)
            .add(ModItems.MAG_45_ITEM, ItemSize(1, 3), false)
            .add(ModItems.MAG_60_ITEM, ItemSize(1, 2), false)
            .add(ModItems.AMMO_556_ITEM, ItemSize(1, 1), false)
            .add(ModItems.BANDAGE_ITEM, ItemSize(1, 1), false)
            .add(ModItems.SPLINT_ITEM, ItemSize(1, 1), false)
            .add(ModItems.PAINKILLERS_ITEM, ItemSize(1, 1), false)
            .add(ModItems.SURGERY_KIT_ITEM, ItemSize(2, 1), false)
            .add(ModItems.MEDKIT_SMALL_ITEM, ItemSize(2, 1), false)
            .add(ModItems.MEDKIT_LARGE_ITEM, ItemSize(1, 2), false)
            .add(ModItems.RIG_ITEM, ItemSize(3, 3), false)
            .add(ModItems.BACKPACK_ITEM, ItemSize(4, 4), false)
            .add(ModItems.LOOT_CRATE_ITEM, ItemSize(2, 2), false)

        lootStatsBuilder
            .add(ModItems.M4A1_ITEM, LootStats(ItemRarity.HIGH_VALUE, 45000), false)
            .add(ModItems.MAG_30_ITEM, LootStats(ItemRarity.MILITARY, 1500), false)
            .add(ModItems.MAG_45_ITEM, LootStats(ItemRarity.MILITARY, 2200), false)
            .add(ModItems.MAG_60_ITEM, LootStats(ItemRarity.HIGH_VALUE, 4000), false)
            .add(ModItems.AMMO_556_ITEM, LootStats(ItemRarity.MILITARY, 150), false)
            .add(ModItems.BANDAGE_ITEM, LootStats(ItemRarity.COMMON, 300), false)
            .add(ModItems.SPLINT_ITEM, LootStats(ItemRarity.COMMON, 400), false)
            .add(ModItems.PAINKILLERS_ITEM, LootStats(ItemRarity.MILITARY, 1200), false)
            .add(ModItems.SURGERY_KIT_ITEM, LootStats(ItemRarity.HIGH_VALUE, 6500), false)
            .add(ModItems.MEDKIT_SMALL_ITEM, LootStats(ItemRarity.MILITARY, 2500), false)
            .add(ModItems.MEDKIT_LARGE_ITEM, LootStats(ItemRarity.HIGH_VALUE, 8000), false)
            .add(ModItems.RIG_ITEM, LootStats(ItemRarity.MILITARY, 8000), false)
            .add(ModItems.BACKPACK_ITEM, LootStats(ItemRarity.MILITARY, 15000), false)
            .add(ModItems.LOOT_CRATE_ITEM, LootStats(ItemRarity.HIGH_VALUE, 10000), false)

        // Defined Vanilla Items as Loot (Sizes, Rarities, Values)
        val lootDefs = listOf(
            // --- 大金頂級 (TOP_TIER) ---
            LootDef(Items.NETHERITE_BLOCK, ItemSize(2, 2), ItemRarity.TOP_TIER, 250000),
            LootDef(Items.DRAGON_EGG, ItemSize(2, 3), ItemRarity.TOP_TIER, 300000),
            LootDef(Items.NETHER_STAR, ItemSize(1, 1), ItemRarity.TOP_TIER, 150000),
            LootDef(Items.HEAVY_CORE, ItemSize(2, 2), ItemRarity.TOP_TIER, 120000),
            LootDef(Items.MACE, ItemSize(2, 3), ItemRarity.TOP_TIER, 120000),
            LootDef(Items.NETHERITE_CHESTPLATE, ItemSize(2, 3), ItemRarity.TOP_TIER, 110000),
            LootDef(Items.ENCHANTED_GOLDEN_APPLE, ItemSize(1, 1), ItemRarity.TOP_TIER, 100000),
            LootDef(Items.TOTEM_OF_UNDYING, ItemSize(1, 2), ItemRarity.TOP_TIER, 80000),
            LootDef(Items.NETHERITE_HELMET, ItemSize(2, 2), ItemRarity.TOP_TIER, 65000),
            LootDef(Items.NETHERITE_INGOT, ItemSize(1, 2), ItemRarity.TOP_TIER, 60000),
            LootDef(Items.NETHERITE_SCRAP, ItemSize(1, 1), ItemRarity.TOP_TIER, 15000),

            // --- 機密 / 貴金屬 (CONFIDENTIAL) ---
            LootDef(Items.DIAMOND_BLOCK, ItemSize(2, 2), ItemRarity.CONFIDENTIAL, 45000),
            LootDef(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE, ItemSize(1, 1), ItemRarity.CONFIDENTIAL, 40000),
            LootDef(Items.TRIDENT, ItemSize(1, 3), ItemRarity.CONFIDENTIAL, 38000),
            LootDef(Items.DIAMOND_CHESTPLATE, ItemSize(2, 3), ItemRarity.CONFIDENTIAL, 35000),
            LootDef(Items.HEART_OF_THE_SEA, ItemSize(1, 1), ItemRarity.CONFIDENTIAL, 35000),
            LootDef(Items.EMERALD_BLOCK, ItemSize(2, 2), ItemRarity.CONFIDENTIAL, 30000),
            LootDef(Items.MUSIC_DISC_PIGSTEP, ItemSize(2, 2), ItemRarity.CONFIDENTIAL, 28000),
            LootDef(Items.MUSIC_DISC_OTHERSIDE, ItemSize(2, 2), ItemRarity.CONFIDENTIAL, 28000),
            LootDef(Items.MUSIC_DISC_RELIC, ItemSize(2, 2), ItemRarity.CONFIDENTIAL, 28000),
            LootDef(Items.GOLD_BLOCK, ItemSize(2, 2), ItemRarity.CONFIDENTIAL, 25000),
            LootDef(Items.OMINOUS_TRIAL_KEY, ItemSize(1, 2), ItemRarity.CONFIDENTIAL, 25000),
            LootDef(Items.MUSIC_DISC_5, ItemSize(2, 2), ItemRarity.CONFIDENTIAL, 25000),
            LootDef(Items.MUSIC_DISC_CREATOR, ItemSize(2, 2), ItemRarity.CONFIDENTIAL, 25000),
            LootDef(Items.DIAMOND_HELMET, ItemSize(2, 2), ItemRarity.CONFIDENTIAL, 20000),
            LootDef(Items.ECHO_SHARD, ItemSize(1, 1), ItemRarity.CONFIDENTIAL, 20000),
            LootDef(Items.GOLDEN_APPLE, ItemSize(1, 1), ItemRarity.CONFIDENTIAL, 18000),
            LootDef(Items.DIAMOND, ItemSize(1, 1), ItemRarity.CONFIDENTIAL, 15000),

            // --- 高價值 (HIGH_VALUE) ---
            LootDef(Items.RECOVERY_COMPASS, ItemSize(2, 2), ItemRarity.HIGH_VALUE, 12000),
            LootDef(Items.GOLDEN_CHESTPLATE, ItemSize(2, 3), ItemRarity.HIGH_VALUE, 10000),
            LootDef(Items.ENDER_EYE, ItemSize(1, 1), ItemRarity.HIGH_VALUE, 10000),
            LootDef(Items.IRON_BLOCK, ItemSize(2, 2), ItemRarity.HIGH_VALUE, 9000),
            LootDef(Items.CLOCK, ItemSize(2, 2), ItemRarity.HIGH_VALUE, 8500),
            LootDef(Items.GHAST_TEAR, ItemSize(1, 1), ItemRarity.HIGH_VALUE, 8000),
            LootDef(Items.MUSIC_DISC_13, ItemSize(2, 2), ItemRarity.HIGH_VALUE, 8000),
            LootDef(Items.MUSIC_DISC_CAT, ItemSize(2, 2), ItemRarity.HIGH_VALUE, 8000),
            LootDef(Items.MUSIC_DISC_CHIRP, ItemSize(2, 2), ItemRarity.HIGH_VALUE, 8000),
            LootDef(Items.NAUTILUS_SHELL, ItemSize(2, 2), ItemRarity.HIGH_VALUE, 7500),
            LootDef(Items.SPYGLASS, ItemSize(1, 2), ItemRarity.HIGH_VALUE, 7000),
            LootDef(Items.ENDER_PEARL, ItemSize(1, 1), ItemRarity.HIGH_VALUE, 6500),
            LootDef(Items.SADDLE, ItemSize(2, 2), ItemRarity.HIGH_VALUE, 6500),
            LootDef(Items.GOLD_INGOT, ItemSize(1, 2), ItemRarity.HIGH_VALUE, 6000),
            LootDef(Items.TRIAL_KEY, ItemSize(1, 2), ItemRarity.HIGH_VALUE, 6000),
            LootDef(Items.GOLDEN_HELMET, ItemSize(2, 2), ItemRarity.HIGH_VALUE, 6000),
            LootDef(Items.COMPASS, ItemSize(2, 2), ItemRarity.HIGH_VALUE, 5500),
            LootDef(Items.GOAT_HORN, ItemSize(1, 2), ItemRarity.HIGH_VALUE, 5000),
            LootDef(Items.EMERALD, ItemSize(1, 1), ItemRarity.HIGH_VALUE, 5000),
            LootDef(Items.NAME_TAG, ItemSize(1, 1), ItemRarity.HIGH_VALUE, 4500),
            LootDef(Items.AMETHYST_SHARD, ItemSize(1, 1), ItemRarity.HIGH_VALUE, 4000),

            // --- 軍品 (MILITARY) ---
            LootDef(Items.IRON_CHESTPLATE, ItemSize(2, 3), ItemRarity.MILITARY, 5000),
            LootDef(Items.TNT, ItemSize(2, 2), ItemRarity.MILITARY, 3500),
            LootDef(Items.CROSSBOW, ItemSize(2, 3), ItemRarity.MILITARY, 3200),
            LootDef(Items.OBSERVER, ItemSize(2, 2), ItemRarity.MILITARY, 3200),
            LootDef(Items.IRON_HELMET, ItemSize(2, 2), ItemRarity.MILITARY, 3000),
            LootDef(Items.DISPENSER, ItemSize(2, 2), ItemRarity.MILITARY, 3000),
            LootDef(Items.SHIELD, ItemSize(2, 2), ItemRarity.MILITARY, 2800),
            LootDef(Items.CHAINMAIL_CHESTPLATE, ItemSize(2, 3), ItemRarity.MILITARY, 2500),
            LootDef(Items.STICKY_PISTON, ItemSize(2, 1), ItemRarity.MILITARY, 2500),
            LootDef(Items.REDSTONE_LAMP, ItemSize(2, 2), ItemRarity.MILITARY, 2500),
            LootDef(Items.COPPER_BLOCK, ItemSize(2, 2), ItemRarity.MILITARY, 2400),
            LootDef(Items.DROPPER, ItemSize(2, 2), ItemRarity.MILITARY, 2200),
            LootDef(Items.BOW, ItemSize(1, 3), ItemRarity.MILITARY, 2000),
            LootDef(Items.COMPARATOR, ItemSize(1, 1), ItemRarity.MILITARY, 2000),
            LootDef(Items.PISTON, ItemSize(2, 1), ItemRarity.MILITARY, 1800),
            LootDef(Items.REPEATER, ItemSize(1, 1), ItemRarity.MILITARY, 1500),
            LootDef(Items.CHAINMAIL_HELMET, ItemSize(2, 2), ItemRarity.MILITARY, 1500),
            LootDef(Items.IRON_INGOT, ItemSize(1, 2), ItemRarity.MILITARY, 1200),
            LootDef(Items.LEAD, ItemSize(1, 1), ItemRarity.MILITARY, 1200),
            LootDef(Items.GUNPOWDER, ItemSize(1, 1), ItemRarity.MILITARY, 1000),
            LootDef(Items.SHEARS, ItemSize(1, 2), ItemRarity.MILITARY, 950),
            LootDef(Items.GLOWSTONE_DUST, ItemSize(1, 1), ItemRarity.MILITARY, 900),
            LootDef(Items.FLINT_AND_STEEL, ItemSize(1, 1), ItemRarity.MILITARY, 850),
            LootDef(Items.REDSTONE, ItemSize(1, 1), ItemRarity.MILITARY, 800),

            // --- 普通 / 雜物 (COMMON) ---
            LootDef(Items.LEATHER_CHESTPLATE, ItemSize(2, 3), ItemRarity.COMMON, 800),
            LootDef(Items.BLAZE_ROD, ItemSize(1, 2), ItemRarity.COMMON, 800),
            LootDef(Items.BLAZE_POWDER, ItemSize(1, 1), ItemRarity.COMMON, 600),
            LootDef(Items.MAGMA_CREAM, ItemSize(1, 1), ItemRarity.COMMON, 500),
            LootDef(Items.FERMENTED_SPIDER_EYE, ItemSize(1, 1), ItemRarity.COMMON, 450),
            LootDef(Items.LEATHER_HELMET, ItemSize(2, 2), ItemRarity.COMMON, 400),
            LootDef(Items.SLIME_BALL, ItemSize(1, 1), ItemRarity.COMMON, 350),
            LootDef(Items.COPPER_INGOT, ItemSize(1, 2), ItemRarity.COMMON, 300),
            LootDef(Items.SPIDER_EYE, ItemSize(1, 1), ItemRarity.COMMON, 250),
            LootDef(Items.BOOK, ItemSize(1, 1), ItemRarity.COMMON, 250),
            LootDef(Items.GLASS, ItemSize(2, 2), ItemRarity.COMMON, 200),
            LootDef(Items.LEATHER, ItemSize(1, 1), ItemRarity.COMMON, 200),
            LootDef(Items.COAL, ItemSize(1, 1), ItemRarity.COMMON, 150),
            LootDef(Items.NETHER_BRICK, ItemSize(1, 1), ItemRarity.COMMON, 150),
            LootDef(Items.BRICK, ItemSize(1, 1), ItemRarity.COMMON, 120),
            LootDef(Items.BONE, ItemSize(1, 2), ItemRarity.COMMON, 120),
            LootDef(Items.CHARCOAL, ItemSize(1, 1), ItemRarity.COMMON, 100),
            LootDef(Items.FLINT, ItemSize(1, 1), ItemRarity.COMMON, 100),
            LootDef(Items.GLASS_BOTTLE, ItemSize(1, 1), ItemRarity.COMMON, 90),
            LootDef(Items.STRING, ItemSize(1, 1), ItemRarity.COMMON, 80),
            LootDef(Items.CLAY_BALL, ItemSize(1, 1), ItemRarity.COMMON, 80),
            LootDef(Items.PAPER, ItemSize(1, 1), ItemRarity.COMMON, 70),
            LootDef(Items.FEATHER, ItemSize(1, 1), ItemRarity.COMMON, 60),
            LootDef(Items.TORCH, ItemSize(1, 1), ItemRarity.COMMON, 60),
            LootDef(Items.ROTTEN_FLESH, ItemSize(1, 1), ItemRarity.COMMON, 50),
            LootDef(Items.BOWL, ItemSize(1, 1), ItemRarity.COMMON, 50),
            LootDef(Items.ARROW, ItemSize(1, 1), ItemRarity.COMMON, 50),
            LootDef(Items.STICK, ItemSize(1, 2), ItemRarity.COMMON, 40)
        )

        for (def in lootDefs) {
            val holder = BuiltInRegistries.ITEM.wrapAsHolder(def.item)
            itemSizeBuilder.add(holder, def.size, false)
            lootStatsBuilder.add(holder, LootStats(def.rarity, def.value), false)
        }
    }
}
