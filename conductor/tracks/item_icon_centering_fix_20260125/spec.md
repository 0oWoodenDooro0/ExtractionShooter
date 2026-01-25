# Specification - Item Icon Centering and Scaling Fix

## Overview
Currently, in the custom inventory system, item icons are not centered within their background slots and do not scale according to their `item_size` data. This bug fix aims to ensure that item icons are perfectly centered within their occupied grid cells and are scaled to fit that area while maintaining their aspect ratio.

## Functional Requirements
1.  **Centering:**
    -   Item icons must be rendered at the exact center of the total grid area they occupy.
    -   This applies to:
        -   The item icon attached to the mouse cursor while dragging (the "held" item).
        -   The item icon as it appears sitting inside the grid slots.
2.  **Scaling (Aspect Fit):**
    -   Icons must be scaled to be as large as possible within their occupied grid cells.
    -   The original aspect ratio of the item icon must be strictly maintained (no distortion).
3.  **Exclusions:**
    -   The special equipment slots (Helmet, Armor, Primary Weapon, Rig, Backpack, Pistol) are excluded from this change as they likely use different rendering logic or fixed textures.
4.  **Scope:**
    -   Applies to the grid-based storage areas inside items (Backpacks, Rigs).
    -   Applies to the player's survival inventory grid interactions.

## Non-Functional Requirements
-   Rendering performance should not be significantly impacted by the scaling calculations.
-   Visual consistency with the "Vanilla-Friendly" aesthetic.

## Acceptance Criteria
-   [ ] In the player inventory and item grids, items of various sizes (1x1, 1x2, 2x2, etc.) are perfectly centered in their slots.
-   [ ] While dragging an item, the icon follows the mouse cursor and remains centered/scaled correctly.
-   [ ] No distortion is visible on scaled item icons.
-   [ ] Equipment slots (like Armor) remain unaffected.

## Out of Scope
-   Scaling logic for the "ghost" landing overlay.
-   External storage grids (Stash, Loot Crates) - though the logic might be shared, the focus is on A and C.
