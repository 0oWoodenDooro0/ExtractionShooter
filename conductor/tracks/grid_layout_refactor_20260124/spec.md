# Specification: Grid Inventory UI Layout Refactor

## Overview
Currently, the custom menu UI adds inventory slots (grids) vertically without bounds, causing them to exceed the screen height. This track aims to transition from a simple vertical stack to a manual coordinate-based system where each `GridInventory` or equipment section can be positioned precisely on the screen using X and Y coordinates.

## Functional Requirements
- **Manual Positioning:** Each `GridInventory` component (Backpacks, Rigs, Pouches) and Player Equipment section (Armor, Helmet) must be positionable via explicit X/Y screen coordinates.
- **Global Coordinate Mapping:** Implement a centralized way (e.g., in a data class or configuration) to define and easily adjust these coordinates.
- **Collision/Overlap Prevention:** Ensure slots are rendered according to their assigned coordinates without default vertical stacking interference.

## Non-Functional Requirements
- **Maintainability:** The coordinate definitions should be easy for a developer to find and modify quickly in the code.
- **Consistency:** The coordinate system should be consistent across different screen sizes (using Minecraft's GUI scaling).
- **Verification:** The implementation must be verified by running `./gradlew classes` successfully.
- **Git Protocol:** Standard Git practices (status, diff, commit with meaningful messages) should be followed.

## Acceptance Criteria
- Inventory grids no longer automatically stack vertically off-screen.
- Backpacks, Rigs, and Pouches appear at their manually assigned X/Y positions.
- Player Equipment (Armor/Helmets) appears at its manually assigned X/Y positions.
- Adjusting a coordinate in the code results in a corresponding movement of the UI element in-game.

## Out of Scope
- Implementation of a drag-and-drop UI editor (manual code adjustments only).
- Scrolling or pagination.
- Dynamic layout adjustments based on changing container content (static positioning only).
