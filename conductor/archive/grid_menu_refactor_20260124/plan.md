# Implementation Plan: GridInventoryMenu Refactor

## Phase 1: Preparation & Cleanup
- [x] Task: Remove Vanilla Slot/Hotbar logic from `GridInventoryMenu`
    - [x] Identify and delete code adding vanilla player inventory and hotbar slots in `GridInventoryMenu`.
    - [x] Ensure the menu still opens and closes correctly without crashing.

## Phase 2: Equipment Logic & Validation
- [x] Task: Implement Equipment Validation Logic
    - [x] Write unit tests for equipment placement validation (e.g., Head slot only accepts Head items).
    - [x] Implement validation logic in a reusable way (likely within `GridInventory` or a dedicated helper).
    - [x] Verify tests pass.
- [x] Task: Define Custom Equipment Slots in `GridInventoryMenu`
    - [x] Define the coordinate system and IDs for the new custom equipment slots.
    - [x] Update `GridInventoryMenu` to register these as `GridInventory` areas rather than vanilla slots.
- [x] Task: Conductor - User Manual Verification 'Equipment Logic' (Protocol in workflow.md)

## Phase 3: Grid-Based Slot Integration
- [x] Task: Refactor Main Inventory to Grid-Only
    - [x] Write unit tests ensuring items can be moved between different `GridInventory` sections within the menu.
    - [x] Replace remaining vanilla-style slot logic with `GridInventory` logic.
    - [x] Ensure vanilla shift-clicking/quick-move logic is disabled/removed.
- [x] Task: UI Rendering & Synchronization
    - [x] Update the Screen class associated with `GridInventoryMenu` to render the new grid structure correctly.
    - [x] Ensure visual feedback for invalid equipment placement is working.
- [ ] Task: Conductor - User Manual Verification 'Grid-Based Integration' (Protocol in workflow.md)

## Phase 4: Finalization
- [x] Task: Verification & Cleanup
    - [x] Run all project tests to ensure no regressions in other parts of the inventory system.
    - [x] Verify that the hotbar is completely isolated from this menu.
- [x] Task: Conductor - User Manual Verification 'Finalization' (Protocol in workflow.md)
