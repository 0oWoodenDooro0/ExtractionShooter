# Specification - Equipment Slot Highlight Fix

## Overview
This track addresses a visual bug in `GridInventoryScreen` where compatible equipment slots fail to highlight correctly when an item is held. Specifically, if an item is compatible with multiple slots (e.g., two primary weapon slots), only one may light up depending on the cursor position, or some slots may remain un-highlighted even if they are valid targets.

The user suspects a `break` statement in the rendering loop is prematurely terminating the validation check for all slots.

## Functional Requirements
- When an item is picked up (held by the cursor), all empty equipment slots that are compatible with that item MUST display a light green highlight.
- The highlight must be consistent across all compatible slots simultaneously, regardless of which specific slot the cursor is currently hovering over.
- This behavior should apply to all equipment categories (Primary Weapons, Rigs, etc.) that have multiple slots.

## Non-Functional Requirements
- **Performance:** The highlight calculation must be efficient enough to run every frame during UI rendering without causing stutter.
- **Maintainability:** Ensure the logic for "is slot compatible" is centralized or consistently applied to avoid future sync issues.

## Acceptance Criteria
- [ ] Holding a primary weapon highlights BOTH `primary_1` and `primary_2` slots in light green if they are empty.
- [ ] Moving the item around the screen does not cause highlights to flicker or disappear for other valid slots.
- [ ] Dropping the item into a slot still works as intended (functional parity).

## Out of Scope
- Changing the highlight color or visual style.
- Modifying the inventory grid (non-equipment) highlighting logic.
