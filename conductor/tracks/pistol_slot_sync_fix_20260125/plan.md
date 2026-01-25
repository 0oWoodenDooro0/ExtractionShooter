# Implementation Plan - Pistol Slot Ghost Image & Inventory Sync Fixes

## Phase 1: Pistol Slot Ghost Image Fix [checkpoint: d0c25d2]
- [x] Task: Create "Pistol" ghost image texture at `src/main/resources/assets/extractionshooter/textures/gui/slots/pistol.png`. [9a130b6]
- [x] Task: Locate and update the Inventory Screen logic to use the new `pistol.png` for the pistol slot background. [7d6eb8e]
- [x] Task: Verify the visual fix by running the client and checking the inventory UI. [d0c25d2]
- [x] Task: Conductor - User Manual Verification 'Phase 1: Pistol Slot Ghost Image Fix' (Protocol in workflow.md) [d0c25d2]

## Phase 2: Inventory Synchronization Fix [checkpoint: 5321a26]
- [x] Task: Investigate current packet handling for initial inventory sync on player join. [c80c351]
    - [x] Sub-task: Identify where the inventory capability is attached and synced. [c80c351]
    - [x] Sub-task: Debug why the initial sync packet might be missing or delayed. [c80c351]
- [x] Task: Implement fix to force synchronization of the custom inventory capability on the `PlayerLoggedInEvent`. [c80c351]
- [x] Task: Investigate and fix synchronization on `PlayerRespawnEvent` to ensure client state matches server state. [c80c351]
- [x] Task: Verify the sync fix by simulating player join and respawn scenarios. [5321a26]
- [x] Task: Conductor - User Manual Verification 'Phase 2: Inventory Synchronization Fix' (Protocol in workflow.md) [5321a26]
