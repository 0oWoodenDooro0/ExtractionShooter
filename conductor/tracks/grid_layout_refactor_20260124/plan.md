# Implementation Plan: Grid Inventory UI Layout Refactor

This plan transitions the grid inventory UI from a vertical stack to a manual coordinate-based system.

## Phase 1: Infrastructure & Coordinate Mapping
- [x] **Task: Define `MenuLayout` utility** [65e4adf]
    - Create `com.gmail.vincent031525.extractionshooter.client.screen.MenuLayout` to map grid names to X/Y coordinates.
    - Centralize all coordinate definitions here for easy adjustment.
- [ ] **Task: Conductor - User Manual Verification 'Infrastructure' (Protocol in workflow.md)**

## Phase 2: UI Implementation (Manual Positioning)
- [x] **Task: Refactor `GridInventoryScreen.renderBg`** [2354e24]
    - Replace the `gridY` increment logic with a lookup in `MenuLayout`.
    - Ensure both grid headers and slots use the manual coordinates.
- [x] **Task: Refactor `GridInventoryScreen` interactions** [2354e24]
    - Update `render` (phantom shape) and `onMouseClick` to use the same coordinate lookup logic.
    - Eliminate all vertical stacking offsets.

- [ ] **Task: Conductor - User Manual Verification 'UI Implementation' (Protocol in workflow.md)**

## Phase 3: Finalization & Verification
- [ ] **Task: Build Verification**
    - Run `./gradlew classes` to ensure everything compiles and integrates correctly.
- [ ] **Task: Git Commitment**
    - Follow Git protocol to commit changes and attach task summaries via git notes.
- [ ] **Task: Conductor - User Manual Verification 'Finalization' (Protocol in workflow.md)**
