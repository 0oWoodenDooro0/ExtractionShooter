# Implementation Plan: Equipment Slot Drag-and-Drop Visuals

Refining the drag-and-drop visuals for equipment slots to prevent item icon snapping and scaling, ensuring a consistent floating experience.

## Phase 1: Investigation
- [x] Task: Locate `singleItem` handling in `GridInventoryScreen.kt`'s `render` method
- [x] Task: Verify interaction between `snapped` state and the final floating item draw
- [x] Task: Conductor - User Manual Verification 'Phase 1: Investigation' (Protocol in workflow.md)

## Phase 2: Implementation
- [x] Task: Update `render` loop for equipment slots (`singleItem` grids)
- [x] Task: Decouple the green/red slot tint from the item icon rendering in equipment slots
- [x] Task: Ensure the held item icon remains in the floating pass even when hovering over equipment slots
- [x] Task: Implement global slot hints for compatible equipment slots when dragging
- [x] Task: Conductor - User Manual Verification 'Phase 2: Implementation' (Protocol in workflow.md)

## Phase 3: Verification and Polishing
- [x] Task: Run `./gradlew classes` to ensure compilation
- [x] Task: Verify that ghost icons remain visible behind the slot highlight
- [x] Task: Verify no icon snapping/scaling occurs for all equipment slots
- [x] Task: Verify global hints appear for valid slots when picking up an item
- [x] Task: Conductor - User Manual Verification 'Phase 3: Verification and Polishing' (Protocol in workflow.md)
