package com.gmail.vincent031525.extractionshooter.inventory

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class GridQuickMoveTest {

    @Test
    fun testIsEquippedSlot() {
        assertTrue(GridQuickMoveHelper.isEquippedSlot("helmet"))
        assertTrue(GridQuickMoveHelper.isEquippedSlot("armor"))
        assertTrue(GridQuickMoveHelper.isEquippedSlot("tactical_rig"))
        assertTrue(GridQuickMoveHelper.isEquippedSlot("backpack"))
        assertTrue(GridQuickMoveHelper.isEquippedSlot("primary_1"))
        assertTrue(GridQuickMoveHelper.isEquippedSlot("primary_2"))
        assertTrue(GridQuickMoveHelper.isEquippedSlot("pistol"))

        assertFalse(GridQuickMoveHelper.isEquippedSlot("backpack_grid"))
        assertFalse(GridQuickMoveHelper.isEquippedSlot("tactical_rig_grid"))
        assertFalse(GridQuickMoveHelper.isEquippedSlot("pockets_1"))
        assertFalse(GridQuickMoveHelper.isEquippedSlot("container"))
    }
}
