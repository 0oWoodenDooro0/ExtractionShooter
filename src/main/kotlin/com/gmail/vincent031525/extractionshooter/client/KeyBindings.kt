package com.gmail.vincent031525.extractionshooter.client

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

object KeyBindings {
    val SWITCH_MODE_KEY = KeyMapping(
        "key.extractionshooter.switch_mode",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_B,
        KeyMapping.Category.MISC
    )

    val RELOAD_KEY = KeyMapping(
        "key.extractionshooter.reload",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        KeyMapping.Category.MISC
    )
}