package com.gmail.vincent031525.extractionshooter.datamap

import com.mojang.serialization.Codec
import net.minecraft.ChatFormatting
import net.minecraft.util.StringRepresentable

enum class ItemRarity(
    private val key: String,
    val translationKey: String,
    val defaultDisplayName: String,
    val formatting: ChatFormatting,
    val backgroundColor: Int
) : StringRepresentable {
    COMMON(
        "common",
        "rarity.extractionshooter.common",
        "普通",
        ChatFormatting.GRAY,
        0xFF3A3D40.toInt()
    ),
    MILITARY(
        "military",
        "rarity.extractionshooter.military",
        "軍品",
        ChatFormatting.GREEN,
        0xFF234E2B.toInt()
    ),
    HIGH_VALUE(
        "high_value",
        "rarity.extractionshooter.high_value",
        "高價值",
        ChatFormatting.AQUA,
        0xFF1E426D.toInt()
    ),
    CONFIDENTIAL(
        "confidential",
        "rarity.extractionshooter.confidential",
        "機密",
        ChatFormatting.LIGHT_PURPLE,
        0xFF532368.toInt()
    ),
    TOP_TIER(
        "top_tier",
        "rarity.extractionshooter.top_tier",
        "大金頂級",
        ChatFormatting.GOLD,
        0xFF8C600A.toInt()
    );

    override fun getSerializedName(): String = key

    companion object {
        val CODEC: Codec<ItemRarity> = StringRepresentable.fromEnum(ItemRarity::values)
    }
}
