# Specification: Floating Placement Highlight

## Overview
Currently, the valid/invalid placement highlight (green/red tint) snaps to the grid slots based on the mouse position. The user wants to change this behavior so that the highlight box moves smoothly with the item icon (following the mouse cursor) instead of snapping to the grid, while keeping the actual placement logic based on the mouse position.

## Functional Requirements
- **Floating Tint:** The green/red validation background must move smoothly with the held item, centered on the mouse cursor (matching the item rendering).
- **Placement Logic:** The actual placement action (on click) must still use the "mouse-closest-cell" logic established in the previous track.
- **Visual Feedback:** The tint should be "Free Floating," meaning it does not visually snap to grid lines while dragging.

## Non-Functional Requirements
- **Consistency:** The tint and the item icon must move in perfect sync.
- **Clarity:** Even though the tint doesn't snap, it should be clear enough (via the mouse position logic) where the item will land.

## Acceptance Criteria
- When holding an item, the green/red background box follows the mouse cursor smoothly.
- The tint box aligns perfectly with the held item icon.
- Clicking to place the item still drops it into the grid slot closest to the mouse cursor.
- The tint does not "jump" or snap to the grid cells visually.

## Out of Scope
- Changing the underlying mathematical logic for where the item lands (it remains mouse-centric).
