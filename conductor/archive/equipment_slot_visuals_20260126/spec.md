# Specification: Equipment Slot Drag-and-Drop Visuals

## Overview
The user wants to refine the visual feedback when dragging items over specific equipment slots (helmet, armor, backpack, rig, primary, pistol). Currently, the system might be snapping or scaling the item icon when hovering over these slots. The goal is to disable this "snapping" and "scaling" behavior for the held item icon, keeping it "floating" with the mouse cursor at its original size. However, the target slot itself must still visually indicate validity (green highlight) if the item matches and the slot is empty.

## Functional Requirements
- **No Icon Snapping:** When hovering over an equipment slot, the held item's icon must NOT snap to the slot's position. It must continue to follow the mouse cursor smoothly.
- **No Icon Scaling:** When hovering over an equipment slot, the held item's icon must NOT automatically resize (scale up or down) to fit the slot. It should retain its original rendered size.
- **Slot Highlighting:** If the held item is valid for the hovered equipment slot and the slot is empty, the *slot itself* should be highlighted with a green tint.
- **Global Slot Hinting:** When an item is being dragged, all compatible and empty equipment slots should display a faint green highlight to guide the user, regardless of mouse position.
- **Ghost Icons:** The background "ghost" icon (e.g., helmet silhouette) in the equipment slot must remain visible behind the green highlight.

## Non-Functional Requirements
- **Consistency:** This behavior applies specifically to the named equipment slots (helmet, armor, backpack, rig, primary, pistol). Standard grid slots should maintain their existing behavior (which was updated to "floating" in the previous track).
- **Visual Clarity:** The user must be able to clearly see where they are placing the item without the item icon jumping or warping.

## Acceptance Criteria
- Dragging a valid item over an empty equipment slot shows a green highlight on the slot.
- The held item icon remains centered on the mouse cursor (floating) while hovering over the equipment slot.
- The held item icon does not change size (scale) while hovering over the equipment slot.
- The background ghost icon remains visible in the slot during the hover event.
- Dropping the item (clicking) successfully equips it into the slot.

## Out of Scope
- Changes to the actual inventory data logic (this is purely a visual/client-side change).
- Changes to the drag-and-drop logic for standard inventory grids (unless they share the same rendering code that needs adjustment).
