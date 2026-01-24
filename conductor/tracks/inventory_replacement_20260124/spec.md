# Specification: Replace Vanilla Inventory with GridInventoryScreen

## Overview
This track aims to integrate the custom `GridInventoryScreen` as the primary inventory interface for players. Instead of opening the vanilla Minecraft inventory, the standard "Open/Close Inventory" keybinding (default 'E') will now trigger our custom grid-based system. The previous custom keybinding dedicated to the grid inventory will be removed. Additionally, a debug command will be implemented to facilitate testing by adding items directly to a specific grid within the player's inventory.

## Functional Requirements
- **Total Replacement**: Pressing the vanilla "Open/Close Inventory" key must open the `GridInventoryScreen` instead of the vanilla `InventoryScreen` or `CreativeModeInventoryScreen`.
- **Global Scope**: This replacement applies to all game modes (Survival, Creative, Adventure, Spectator).
- **Personal Inventory Only**: This change only affects the player's personal inventory access. Interactions with external containers (chests, machines, etc.) must continue to open their respective vanilla or modded interfaces.
- **Keybinding Cleanup**: The custom `TOGGLE_INVENTORY_KEY` keybinding defined in `KeyBindings.kt` must be removed.
- **Toggle Behavior**: Pressing the inventory key while the `GridInventoryScreen` is open should close it, matching vanilla behavior.
- **Debug Command**: Implement a command (e.g., `/es give <player> <item> [amount] <grid_id>`) to add items to a specific grid in the player's `PlayerEquipment`.
    - **Target Grid**: The `grid_id` parameter specifies which grid in the `persistentGrids` map to target (e.g., "pocket").
    - **Automatic Placement**: The command will automatically find the first valid position for the item within the specified `grid_id`.
    - **Validation**: If the `grid_id` does not exist in the player's `persistentGrids` or if the grid is full, the command should notify the sender with an appropriate error message.

## Non-Functional Requirements
- **Integration**: Use NeoForge event hooks (e.g., `ScreenEvent.Opening`) to intercept the opening of vanilla inventory screens on the client side.
- **Maintainability**: Ensure the logic for switching screens is centralized and doesn't interfere with other mods' screens.

## Acceptance Criteria
- Pressing 'E' (or the configured inventory key) opens the `GridInventoryScreen` in Survival mode.
- Pressing 'E' (or the configured inventory key) opens the `GridInventoryScreen` in Creative mode.
- The previous custom keybinding for "Open Grid Inventory" no longer appears in the controls menu.
- Opening a chest still opens the chest UI, not the grid inventory.
- Pressing 'E' while the `GridInventoryScreen` is open closes it.
- Executing `/es give @s extractionshooter:m4a1 1 "pocket"` adds an M4A1 to the "pocket" grid if space is available.
- Executing the command with an invalid `grid_id` or a full grid returns an error message.

## Out of Scope
- Modifying or replacing container UIs (chests, furnaces, etc.).
- Adding a "Legacy" or "Vanilla" toggle within the new UI.
