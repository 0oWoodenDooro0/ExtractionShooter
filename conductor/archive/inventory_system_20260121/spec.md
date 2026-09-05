# Track Specification: Implement Grid-Based Inventory System

## Goal
To replace the standard Minecraft inventory with a "Tarkov-like" grid-based inventory system. This involves creating a custom GUI, handling items of varying sizes (e.g., 1x1, 2x3), and implementing specialized equipment slots (Vest, Backpack, Pockets) and a persistent Stash.

## Core Requirements
1.  **Grid Inventory Logic:**
    -   Items must have defined dimensions (width x height).
    -   The inventory container must validate if an item fits in a specific location (x, y).
    -   Support for item rotation (optional but good for future).
2.  **Custom Player Inventory:**
    -   Override or hide the default vanilla inventory screen.
    -   Implement specific slots: Headset, Helmet, Armor, Rig (Vest), Backpack, Pockets, Secure Container.
    -   Nested containers: Rigs and Backpacks provide their own grid inventories.
3.  **UI/UX:**
    -   A graphical interface displaying the grid and slots.
    -   Drag-and-drop functionality respecting grid constraints.
    -   Tooltip rendering for custom item stats.
4.  **Data Persistence:**
    -   Save the state of the complex inventory (grids inside items) to NBT/Data Components.

## Technical Scope
-   **Backend:** Kotlin classes for `GridInventory`, `EquipmentSlots`, and `InventoryHandler`.
-   **Frontend:** `AbstractContainerScreen` implementation with custom rendering for grids.
-   **Networking:** Syncing complex inventory changes between server and client.
