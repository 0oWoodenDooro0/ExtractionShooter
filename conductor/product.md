# Product Definition

## Initial Concept
This project aims to create a "Tarkov-like" extraction shooter experience within Minecraft. It simplifies the complex mechanics of Escape from Tarkov while retaining the core gameplay loop of "Loot, Fight, Extract," providing an accessible yet tactical option for players.

## Target Audience
- Casual players seeking a tactical shooter experience in Minecraft without the extreme learning curve of hardcore simulators.
- Players interested in a high-stakes survival loop (risk vs. reward) within the Minecraft engine.

## Core Gameplay Features
1.  **Raid Mechanics:**
    -   Players enter a map instance ("Raid") with a time limit.
    -   The primary goal is to scavenge for loot and reach an extraction point alive.
    -   Death results in the loss of all carried gear (high stakes).

2.  **Advanced Health System:**
    -   Goes beyond standard Minecraft HP.
    -   Includes status effects like **Bleeding** (requires bandages) and **Fractures** (requires splints).
    -   Medical items are essential for survival.

3.  **Economy & Stash:**
    -   **Persistent Stash:** A safe inventory outside of raids to store looted gear.
    -   **Trading:** A system to sell loot and buy equipment (weapons, ammo, meds).

4.  **Combat & Ballistics:**
    -   Features firearms (starting with M4A1) with custom stats.
    -   Uses a vanilla-friendly aesthetic with simple 3D models for weapons.

## Visual Style
-   **Vanilla-Friendly:** Textures and UI elements blend with the default Minecraft look.
-   **3D Elements:** Weapons and key items utilize simple 3D models (using Blockbench/GeckoLib) to distinguish them without breaking the blocky aesthetic.

## Extraction System
-   **Fixed Extraction Points:** Raids feature designated zones on the map where players must go to extract.
-   Reaching these zones allows the player to leave the raid with their loot.

## Technology Scope
-   **Loader:** NeoForge
-   **Language:** Kotlin
-   **Key Libraries:** GeckoLib (for 3D animations/models).
-   **Integration:** Self-contained; no external mod dependencies (like JEI/Curios) required for the MVP.
