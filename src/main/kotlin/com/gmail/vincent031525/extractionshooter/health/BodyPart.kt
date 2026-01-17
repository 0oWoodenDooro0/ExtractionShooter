package com.gmail.vincent031525.extractionshooter.health

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

enum class BodyPart(val displayName: MutableComponent, val maxHealth: Float) {
    HEAD(Component.translatable("bodypart.extractionshooter.head"), 35f),
    BODY(Component.translatable("bodypart.extractionshooter.body"), 85f),
    LEGS(Component.translatable("bodypart.extractionshooter.legs"), 65f),
}