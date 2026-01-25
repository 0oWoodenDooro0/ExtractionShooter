# Plan: Equipment Slot Grid Refactor

## Phase 1: Asset Preparation
- [x] Task: Create placeholder textures for large slots 8b3c3ea
- [x] Task: Conductor - User Manual Verification 'Asset Preparation' (Protocol in workflow.md) 8b3c3ea

## Phase 2: Logic Implementation
- [ ] Task: Define Custom Slot Classes
    - [ ] Create or modify a `CustomSizeSlot` class that supports defining width and height (in grid units).
    - [ ] Implement `isItemValid` to enforce Tag checks but bypass strict 1x1 size checks (allow any item that matches the tag, assuming it fits the logic of "one item per slot").
- [ ] Task: Update Menu Layout
    - [ ] Refactor `ExtractionShooterMenu` to replace standard equipment slots with the new `CustomSizeSlot` instances.
    - [ ] Configure the Primary Weapon slot as 4x2.
    - [ ] Configure Armor/Rig/Backpack slots as 2x2.
- [ ] Task: Conductor - User Manual Verification 'Logic Implementation' (Protocol in workflow.md)

## Phase 3: UI & Rendering
- [ ] Task: Implement Slot Rendering
    - [ ] Update `ExtractionShooterScreen` to render the background of these custom slots using the new ghost textures instead of tiling 1x1 squares.
- [ ] Task: Implement Item Scaling Logic
    - [ ] Override or hook into the item rendering (e.g., `renderItem`) specifically for these slots.
    - [ ] Calculate the scale factor: `min(slotWidth / itemWidth, slotHeight / itemHeight)` (in pixels).
    - [ ] Apply scaling transformation before rendering the item model/texture.
    - [ ] Center the item within the slot boundaries.
- [ ] Task: Conductor - User Manual Verification 'UI & Rendering' (Protocol in workflow.md)
