# Specification: Equipment Slot Grid Refactor

## Overview
This track involves refactoring the player's equipment slots in the inventory UI to support variable grid sizes. Specifically, the primary weapon slot will be expanded to 4x2, and other equipment slots (Armor, Helmet, Rig, etc.) will be 2x2. These slots will accept items based on tags rather than strict size matching, and the UI will scale item icons to fit the new slot dimensions.

## Functional Requirements
- **Variable Slot Sizes:**
    - Primary Weapon Slots: 4x2 grid units.
    - Other Equipment Slots (Helmet, Body Armor, Rig, Backpack, etc.): 2x2 grid units.
- **Tag-Based Validation:** Slots must continue to enforce item compatibility based on tags (e.g., `extractionshooter:primary_weapons`), but ignore the item's `item_size` data map for placement validity.
- **UI Scaling:**
    - Item icons rendered within these larger slots must be scaled to the maximum possible size that fits within the slot boundaries.
    - Maintain the aspect ratio of the item icon during scaling.
- **Slot Textures:** Replace individual 1x1 grid textures for these slots with a single large "ghost" texture (silhouette) representing the slot type.

## Non-Functional Requirements
- **Maintain Performance:** Scaling item icons in the UI should not noticeably impact frame rates.
- **Compatibility:** Ensure the new slot sizes interact correctly with the existing drag-and-drop logic.

## Acceptance Criteria
- [ ] Primary weapon slot is visually 4x2 and accepts valid guns.
- [ ] Other equipment slots are visually 2x2 and accept valid items (e.g., helmets in helmet slot).
- [ ] Items smaller than the slot (e.g., a 1x1 item in a 2x2 slot) are scaled up to fit the 2x2 area.
- [ ] Large items (e.g., a 4x2 gun) fit perfectly in the 4x2 slot.
- [ ] Tag restrictions are still enforced (e.g., cannot put a 2x2 rig in a 2x2 helmet slot).
- [ ] Slots display a single large ghost texture instead of multiple small grid squares.

## Out of Scope
- Changing the grid-based layout of the main "Stash" or "Backpack" internal inventory (these remain standard 1x1 grid containers).
- Adding new equipment slots.
