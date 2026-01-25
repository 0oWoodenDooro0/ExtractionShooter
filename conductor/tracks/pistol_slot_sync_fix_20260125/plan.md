# Implementation Plan - Pistol Slot Ghost Image & Inventory Sync Fixes

## Phase 1: Pistol Slot Ghost Image Fix
- [x] Task: Create "Pistol" ghost image texture at src/main/resources/assets/extractionshooter/textures/gui/slots/pistol.png. [7d6eb8e]
- [x] Task: Locate and update the Inventory Screen logic to use the new `pistol.png` for the pistol slot background. [7d6eb8e]
- [ ] Task: Verify the visual fix by running the client and checking the inventory UI.
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Pistol Slot Ghost Image Fix' (Protocol in workflow.md)

## Phase 2: Inventory Synchronization Fix
- [ ] Task: Investigate current packet handling for initial inventory sync on player join.
    - [ ] Sub-task: Identify where the inventory capability is attached and synced.
    - [ ] Sub-task: Debug why the initial sync packet might be missing or delayed.
- [ ] Task: Implement fix to force synchronization of the custom inventory capability on the `PlayerLoggedInEvent`.
- [ ] Task: Investigate and fix synchronization on `PlayerRespawnEvent` to ensure client state matches server state.
- [ ] Task: Verify the sync fix by simulating player join and respawn scenarios.
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Inventory Synchronization Fix' (Protocol in workflow.md)
