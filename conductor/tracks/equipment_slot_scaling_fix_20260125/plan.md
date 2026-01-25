# Implementation Plan - Equipment Slot Item Scaling Fix

## Phase 1: Rendering Logic Update
- [x] Task: Investigate and enable `PoseStack` scaling in `GridInventoryScreen`.
    - [x] Sub-task: Create a helper method to calculate the optimal scale factor and offset for an item given a target slot width/height.
- [x] Task: Update `GridInventoryScreen.renderBg` to apply the calculated scale and translation when rendering items in `singleItem` slots. 223b59b
    - [x] Sub-task: Ensure the scale preserves the item's aspect ratio.
    - [x] Sub-task: Ensure the item is centered within the slot.
- [ ] Task: Verify the fix visually in the client.
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Rendering Logic Update' (Protocol in workflow.md)
