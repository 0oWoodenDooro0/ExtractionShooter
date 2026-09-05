# Implementation Plan - Implement Grid-Based Inventory System

## Phase 1: Core Logic Implementation
- [x] Task: Define Item Dimensions Data Component
    - [ ] Define a Data Map or Data Component to store item width and height (default 1x1).
    - [ ] Create a utility to retrieve item size.
- [x] Task: Implement GridInventory Class
    - [ ] Create a class representing a generic grid (width x height).
    - [ ] Implement `canPlaceItem(item, x, y)` logic checking for overlaps.
    - [ ] Implement `insertItem`, `removeItem`, and `getItem` methods.
    - [ ] Write unit tests for grid logic (overlap detection, boundary checks).
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Core Logic Implementation' (Protocol in workflow.md)

## Phase 2: Equipment System & Data Structure
- [x] Task: Define Custom Player Capability/Attachment
    - [x] Create a Data Attachment (NeoForge Capabilities) for the player to hold the new inventory state.
    - [x] Define the specific equipment slots (Headwear, Armor, Rig, Backpack, etc.).
- [x] Task: Implement Container Nesting Logic
    - [x] Ensure items like Backpacks and Rigs can expose a `GridInventory` capability.
    - [x] Implement serialization/deserialization (NBT) for nested inventories.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Equipment System & Data Structure' (Protocol in workflow.md)

## Phase 3: UI/UX Implementation
- [x] Task: Create Custom Menu and Container
    - [x] Implement `MenuType` and `AbstractContainerMenu` for the new inventory.
    - [x] Handle server-to-client syncing of grid contents.
- [x] Task: Implement Inventory Screen
    - [x] Create `GridInventoryScreen` extending `AbstractContainerScreen`.
    - [x] Implement rendering logic for the grid (drawing grid lines, item icons).
    - [x] Implement custom drag-and-drop logic (calculating grid position from mouse coordinates).
- [x] Task: Keybinding and Event Handling
    - [x] Intercept the default inventory key (E) to open the custom GUI instead.
- [x] Task: Conductor - User Manual Verification 'Phase 3: UI/UX Implementation' (Protocol in workflow.md)
