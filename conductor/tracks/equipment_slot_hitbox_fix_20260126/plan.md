# Implementation Plan: Equipment Slot Placement Hitbox Fix

Fixing the placement logic in `GridInventoryScreen` to ensure equipment slots (single-item grids) can accept items regardless of where the mouse is within their boundaries.

## Phase 1: Investigation and Preparation
- [x] Task: Review `GridInventoryScreen.kt` to identify the precise lines in `render` and `onMouseClick` where `col` and `row` are calculated.
- [x] Task: Confirm that all equipment slots (Helmet, Armor, etc.) consistently use the `singleItem` property.
- [x] Task: Conductor - User Manual Verification 'Phase 1: Investigation and Preparation' (Protocol in workflow.md)

## Phase 2: Implementation [checkpoint: a13642f]
- [x] Task: Update the `render` method in `GridInventoryScreen.kt` to force `col = 0` and `row = 0` when the hovered grid is a `singleItem` grid. df7ddb7
- [x] Task: Update the `onMouseClick` method in `GridInventoryScreen.kt` to force `col = 0` and `row = 0` when the clicked grid is a `singleItem` grid. df7ddb7
- [x] Task: Run `./gradlew classes` to ensure compilation.
- [x] Task: Conductor - User Manual Verification 'Phase 2: Implementation' (Protocol in workflow.md)

## Phase 3: Verification and Polishing [checkpoint: a13642f]
- [ ] Task: Verify successful placement of a 5x2 item (e.g., M4A1) into the Primary slot from all corners (top-left, bottom-left, top-right, etc.).
- [ ] Task: Verify that standard grid-based placement (e.g., placing items into a Backpack) still requires correct coordinate alignment.
- [ ] Task: Final compilation check.
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Verification and Polishing' (Protocol in workflow.md)
