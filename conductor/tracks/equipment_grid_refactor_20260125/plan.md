# Plan: Equipment Slot Grid Refactor

## Phase 1: Asset Preparation [checkpoint: 71bd562]
- [x] Task: Create placeholder textures for large slots 8b3c3ea
- [x] Task: Conductor - User Manual Verification 'Asset Preparation' (Protocol in workflow.md) 8b3c3ea

## Phase 2: Logic Implementation [checkpoint: 04ba1f4]
- [x] Task: Define Custom Slot Classes 1c4b727
- [x] Task: Update Menu Layout 1c4b727
- [x] Task: Conductor - User Manual Verification 'Logic Implementation' (Protocol in workflow.md) 1c4b727

## Phase 3: UI & Rendering
- [ ] Task: Implement Slot Rendering
    - [ ] Update `ExtractionShooterScreen` to render the background of these custom slots using the new ghost textures instead of tiling 1x1 squares.
- [ ] Task: Implement Item Scaling Logic
    - [ ] Override or hook into the item rendering (e.g., `renderItem`) specifically for these slots.
    - [ ] Calculate the scale factor: `min(slotWidth / itemWidth, slotHeight / itemHeight)` (in pixels).
    - [ ] Apply scaling transformation before rendering the item model/texture.
    - [ ] Center the item within the slot boundaries.
- [ ] Task: Conductor - User Manual Verification 'UI & Rendering' (Protocol in workflow.md)
