# Specification: Equipment Slot Placement Hitbox Fix

## Overview
Currently, in `GridInventoryScreen`, equipment slots (Helmet, Armor, Rig, Backpack, Primary, Pistol) use the mouse-relative-to-item-center logic to calculate the target grid coordinates (`col`, `row`). For large items like the M4A1 (5x2), this calculation results in non-zero coordinates (e.g., negative offsets) when the mouse is at the top-left of the slot, causing the placement validation to fail (red highlight). The user expects that any mouse position within the equipment slot's visual boundary should allow for successful placement (treating it as coordinate 0,0).

## Functional Requirements
- **Unified Hitbox for Equipment Slots:** For all grids marked as `singleItem` (equipment slots), the placement logic must ignore the calculated `col` and `row` offsets based on item size. Instead, it should default to `(0, 0)` as long as the mouse is within the grid's boundary.
- **Consistency:** This applies to both the visual rendering (highlighting) in `render` and the actual placement logic in `onMouseClick`.
- **Scope:** This fix is specific to `singleItem` grids. Standard inventory grids (Backpacks, Stash, Rig internals) must retain their existing coordinate-based placement logic to allow precise item positioning.

## Non-Functional Requirements
- **UX Reliability:** Eliminates the "dead zones" when trying to equip large items into equipment slots.

## Acceptance Criteria
- Dragging a 5x2 M4A1 over the Primary weapon slot shows a green highlight regardless of where the mouse is within the slot boundary.
- Clicking to place the M4A1 succeeds from any position within the slot boundary.
- Other equipment slots (Helmet, Armor, etc.) exhibit the same behavior (allowing placement from any position within their boundary).
- Standard grid placement (e.g., putting an item into a backpack) remains unchanged and requires correct alignment.

## Out of Scope
- Changes to item rotation logic.
- Changes to tag-based validation (which items fit in which slots).
