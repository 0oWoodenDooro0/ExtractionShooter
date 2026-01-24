# Specification: GridInventoryMenu Refactor

## 1. Overview
This track aims to completely refactor the `GridInventoryMenu` to abandon vanilla Minecraft `Slot` implementations in favor of a full `GridInventory` system. This includes removing the vanilla hotbar integration and establishing a standalone custom equipment system that is decoupled from vanilla armor slots. Specific validation logic will be implemented to enforce equipment restrictions.

## 2. Functional Requirements

### 2.1 Slot Replacement
-   **Remove Vanilla Slots:** All instances of vanilla `Slot` and `Hotbar` slots must be removed from `GridInventoryMenu`.
-   **GridInventory Integration:** All item storage and interaction points within the menu must be backed by `GridInventory`.

### 2.2 Custom Equipment System
-   **Independent Slots:** Implement custom equipment slots (Head, Body, etc.) using the `GridInventory` system. These must not sync with or rely on standard vanilla player equipment slots.
-   **Placement Validation:** Implement specific logic within the Menu/Container to restrict item placement in equipment slots (e.g., only items valid for "Head" can be placed in the Head slot).

### 2.3 User Interface
-   **No Hotbar:** The menu must not display or interact with the player's vanilla hotbar.
-   **Visual Consistency:** Ensure the rendering of these new grid-based slots matches the existing "Pocket/Secure Container" visual style.

## 3. Technical Requirements
-   **Class:** `GridInventoryMenu`
-   **Validation:** Validation logic for equipment slots should be implemented within the Menu's slot interaction handling (e.g., `canPlaceItem` or equivalent overrides).
-   **Compatibility:** Ensure `GridInventory` data structures correctly persist and sync these new slot types.

## 4. Out of Scope
-   Modifying the vanilla inventory screen (this is specific to `GridInventoryMenu`).
-   Adding new equipment items (focus is on the slot logic).
-   Quick-move (Shift-Click) functionality within the GridInventory.
