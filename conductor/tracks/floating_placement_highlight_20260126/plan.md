# Implementation Plan: Floating Placement Highlight

Reverting the visual snapping of the placement highlight to provide a smoother "floating" experience while maintaining precision placement.

## Phase 1: Investigation and Preparation
- [x] Task: Locate visual snapping logic in `GridInventoryScreen.kt`
- [x] Task: Confirm current `tintX` and `tintY` calculation vs `renderX` and `renderY`
- [x] Task: Conductor - User Manual Verification 'Phase 1: Investigation and Preparation' (Protocol in workflow.md)

## Phase 2: Implementation [checkpoint: 6767a65]
- [x] Task: Modify `render` method to decouple validation highlight from grid snapping 1476076
- [x] Task: Ensure `tintX` and `tintY` consistently follow `renderX` and `renderY` 1476076
- [x] Task: Verify that placement logic (col/row calculation) remains mouse-centric for validation 1476076
- [x] Task: Conductor - User Manual Verification 'Phase 2: Implementation' (Protocol in workflow.md) 6767a65

## Phase 3: Verification and Polishing
- [ ] Task: Run `./gradlew classes` to ensure compilation
- [ ] Task: Verify smooth movement of green/red highlight for various item sizes
- [ ] Task: Confirm item still drops in the expected (closest) grid slot
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Verification and Polishing' (Protocol in workflow.md)
