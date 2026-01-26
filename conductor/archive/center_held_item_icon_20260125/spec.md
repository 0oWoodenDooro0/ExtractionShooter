# Specification: Center Held Item Icon on Mouse

## Overview
Currently, in `GridInventoryScreen`, when an item is being "held" (dragged) by the mouse, the cursor is aligned with the top-left corner of the item's icon. This track aims to improve the user experience by centering the mouse cursor on the exact geometric center of the held item's icon.

## Functional Requirements
- **Geometric Centering:** The mouse cursor must be positioned at the center of the held item's bounding box.
- **Dynamic Calculation:** The centering offset must be calculated based on the item's grid dimensions (width × height) and the GUI scale/cell size.
- **Scope:** This behavior change is limited to `GridInventoryScreen`.

## Non-Functional Requirements
- **Consistency:** The centering should feel consistent regardless of the item size (1x1, 2x3, etc.).
- **Performance:** Offset calculations should be efficient and not impact UI frame rates.

## Acceptance Criteria
- When picking up any item in `GridInventoryScreen`, the icon's center snaps to the mouse cursor position.
- For a 1x1 item, the cursor is in the middle of that single cell.
- For a multi-cell item (e.g., 2x3), the cursor is in the middle of the entire 2x3 area.

## Out of Scope
- Changes to other screens (e.g., standard Minecraft inventories or other custom screens not using the grid system).
- Changing the "pickup" logic (only the visual rendering of the held item).
