# Implementation Plan: Inventory Replacement and Debug Command

## Phase 1: Keybinding Cleanup and Inventory Interception
- [x] Task: Remove custom inventory keybinding [8835702]
    - [ ] Remove `TOGGLE_INVENTORY_KEY` from `src/main/kotlin/com/gmail/vincent031525/extractionshooter/client/KeyBindings.kt`
    - [ ] Unregister `TOGGLE_INVENTORY_KEY` in `src/main/kotlin/com/gmail/vincent031525/extractionshooter/client/event/ClientModEvents.kt`
    - [ ] Remove consumption logic for `TOGGLE_INVENTORY_KEY` in `ClientModEvents.kt`
- [x] Task: Intercept vanilla inventory screen opening [4f67a7e]
    - [ ] Register a client-side event handler for `ScreenEvent.Opening` in `ClientModEvents.kt`
    - [ ] Implement logic to detect `InventoryScreen` or `CreativeModeInventoryScreen` (for all game modes)
    - [ ] Cancel the event if detected and send `OpenInventoryPayload.INSTANCE` to the server
- [x] Task: Verify compilation [4f67a7e]
    - [ ] Run `./gradlew classes` to ensure no syntax errors or broken references
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Keybinding Cleanup and Inventory Interception' (Protocol in workflow.md)

## Phase 2: Debug Command Implementation
- [x] Task: Implement `/es give` command [c4cc0a0]
    - [x] Register `/es give <player> <item> [amount] <grid_id>` command in `DamageHandler.kt` (or a new command handler if preferred)
    - [x] Implement `executeGive` logic:
        - [x] Verify `grid_id` exists in player's `PlayerEquipment.persistentGrids`
        - [x] Find first available slot for the item in the specified grid
        - [x] Add the item to the grid and sync with client
- [x] Task: Verify compilation [c4cc0a0]
    - [x] Run `./gradlew classes` to ensure command implementation is syntactically correct
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Debug Command Implementation' (Protocol in workflow.md)

## Phase 3: Final Verification
- [ ] Task: Verify global replacement in all game modes (Survival, Creative)
- [ ] Task: Verify chest interactions still work correctly
- [ ] Task: Verify debug command correctly places items in targeted grids
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Final Verification' (Protocol in workflow.md)
