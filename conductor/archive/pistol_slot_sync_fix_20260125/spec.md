# Specification: Pistol Slot Ghost Image & Inventory Sync Fixes

## Overview
This track addresses two visual and functional bugs in the custom inventory system:
1.  **Visual:** The "Pistol" equipment slot incorrectly displays a "Rig" ghost image (background icon) instead of a "Pistol" icon.
2.  **Functional:** Custom inventory contents are sometimes invisible to the player upon joining the world or respawning, appearing only after forcing an update via commands like `/es give`.

## Functional Requirements

### 1. Pistol Slot Ghost Image
-   **Asset Creation:** Create a new ghost image texture representing a "Pistol" at `src/main/resources/assets/extractionshooter/textures/gui/slots/pistol.png` that matches the style of existing slot icons.
-   **UI Update:** Update the Inventory Screen logic to render this new pistol texture for the designated pistol slot background, replacing the incorrect "Rig" texture.

### 2. Inventory Synchronization
-   **Player Join:** Ensure the server sends the full state of the custom inventory capability to the client immediately when a player joins the world.
-   **Player Respawn:** Ensure the inventory state is correctly synchronized to the client after the player respawns.
    -   *Note:* While the game rules typically drop items on death, the client-side view must always accurately reflect the server-side state (empty or otherwise) without desync.

## Acceptance Criteria
-   [ ] **Visual Check:** The pistol slot in the inventory GUI displays a distinct pistol icon background, not a rig icon.
-   [ ] **Sync Check (Join):** A player logging into the world with items in their custom inventory (from a previous session) sees them immediately.
-   [ ] **Sync Check (Respawn):** After dying and respawning, the client's inventory view is consistent with the server's state (no "invisible" items that require a refresh).

## Out of Scope
-   Changes to the actual "loss of items on death" logic (gameplay rules), unless it directly causes the sync bug.
