# Plan - Item Icon Centering and Scaling Fix

## Phase 1: Research and Investigation
- [x] Task: Analyze `GridInventoryScreen` and related rendering logic.
    - [x] Locate `GridInventoryScreen` (likely in `com.gmail.vincent031525.extractionshooter.client.menu` or similar).
    - [x] Identify the specific methods responsible for rendering items within grid slots and the "held" item (cursor stack).
    - [x] Confirm how `item_size` (width/height) is currently retrieved in this context.

## Phase 2: Implementation - Grid Item Rendering [checkpoint: 6252d15]
- [x] Task: Implement Aspect Fit scaling and centering in `GridInventoryScreen`. 5dd5501
    - [x] Modify the slot rendering logic to calculate the total pixel dimensions of the item's occupied grid cells.
    - [x] Apply `PoseStack` transformations (scale and translate) to:
        1. Scale the item icon to fit within the occupied area while maintaining aspect ratio.
        2. Center the item icon within that area.
    - [x] Ensure special equipment slots (Helmet, Armor, etc.) are NOT affected by this logic (verify if they use the same renderer or a separate one).
- [x] Task: Conductor - User Manual Verification 'Grid Item Rendering' (Protocol in workflow.md)

## Phase 3: Implementation - Held Item Rendering
- [x] Task: Update the "Held Item" (Cursor) rendering in `GridInventoryScreen`. 951af20
    - [x] Locate the logic for rendering the item currently being dragged (often called after the main background rendering).
    - [x] Apply the same aspect-fit scaling and centering logic relative to the mouse cursor position.
- [ ] Task: Conductor - User Manual Verification 'Held Item Rendering' (Protocol in workflow.md)

## Phase 4: Verification and Polish
- [ ] Task: Final compilation check and code cleanup.
    - [ ] Run `./gradlew classes` to ensure no build errors.
    - [ ] Verify that items of various sizes (1x1, 1x2, 2x2) render correctly centered and scaled in backpacks/rigs and the player inventory grid.
- [ ] Task: Conductor - User Manual Verification 'Verification and Polish' (Protocol in workflow.md)
