# Specification: Equipment Slot Item Scaling Fix

## Overview
This track addresses a visual bug where items placed in custom equipment slots (Helmet, Armor, Backpack, Rig, Primary Weapons, and Pistol) are rendered at a default small size (16x16) rather than being scaled to appropriately fit the dimensions of the slot.

## Functional Requirements

### 1. Visual Scaling Logic
- **Scaling Behavior:** Items placed in `singleItem` equipment slots MUST be scaled to fit within the slot's bounds while maintaining their original aspect ratio.
- **Affected Slots:**
    - Helmet (2x2 grid units)
    - Armor (2x2 grid units)
    - Tactical Rig (2x2 grid units)
    - Backpack (2x2 grid units)
    - Primary Weapons (4x2 grid units)
    - Pistol (2x2 grid units)
- **Centering:** Scaled items MUST be centered within the slot both horizontally and vertically.

### 2. Implementation Details
- **UI Logic Update:** Modify `GridInventoryScreen.renderBg` to correctly apply scaling transformations (using `PoseStack` or similar) when rendering items in `grid.singleItem` slots.
- **Aspect Ratio Preservation:** Ensure that square icons remain square even in rectangular slots (like Primary Weapon slots).

## Acceptance Criteria
- [ ] **Visual Check:** Items in 2x2 slots (Helmet, Rig, etc.) appear significantly larger than standard 1x1 grid items.
- [ ] **Visual Check:** Weapons in the 4x2 slots are scaled up to utilize the extra width while remaining centered.
- [ ] **Consistency Check:** Items maintain their correct aspect ratio (no stretching).
- [ ] **Centering Check:** Items are visually centered within their respective slot backgrounds.

## Out of Scope
- Changes to the "phantom" item rendering when dragging.
- Scaling of items in standard multi-slot grids (Backpacks/Rigs internal inventory).
