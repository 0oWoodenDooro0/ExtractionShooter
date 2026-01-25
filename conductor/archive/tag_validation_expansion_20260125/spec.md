# Specification: Tag-based Equipment Validation Expansion

## Overview
This track expands the `EquipmentValidator` system to enforce item restrictions for `backpack`, `tactical_rig`, `primary_1`, `primary_2`, and `pistol` slots using Minecraft Item Tags. It also establishes a hierarchical tag structure for firearms and utilizes Data Generation to manage tag entries for the initial set of items (M4A1, Backpack, Rig).

## Functional Requirements
- **Tag-Based Validation**: Update `EquipmentValidator` to validate items in the following slots against their respective tags:
    - `backpack` -> `#extractionshooter:backpacks`
    - `tactical_rig` -> `#extractionshooter:rigs`
    - `primary_1` & `primary_2` -> `#extractionshooter:primary_weapons`
    - `pistol` -> `#extractionshooter:pistols`
- **Hierarchical Gun Tags**:
    - Create a base `#extractionshooter:guns` tag.
    - `#extractionshooter:primary_weapons` and `#extractionshooter:pistols` should be structured to allow for shared traits if needed, but primarily act as the filter for their specific slots.
- **Initial Tag Content**:
    - Add `extractionshooter:backpack` to `backpacks`.
    - Add `extractionshooter:rig` to `rigs`.
    - Add `extractionshooter:m4a1` to `guns` and `primary_weapons`.
- **Data Generation**: Update `ModItemTagProvider` to programmatically generate these tags and their associated JSON files.

## Non-Functional Requirements
- **Consistency**: Maintain the pattern established in previous refactors where `EquipmentValidator` remains the central logic point but delegates the "source of truth" to tags.
- **Syntax Validation**: Use `./gradlew classes` to ensure all code changes are syntactically correct and type-safe.

## Acceptance Criteria
- A player cannot place an M4A1 in the `backpack` or `tactical_rig` slots.
- A player can place an M4A1 in `primary_1` or `primary_2` slots.
- A player can place a Backpack item only in the `backpack` slot.
- A player can place a Rig item only in the `tactical_rig` slot.
- Running `./gradlew runData` generates the correct JSON files in `src/generated/resources`.
- Running `./gradlew classes` completes without errors.

## Out of Scope
- Implementation of new items or weapons not mentioned (e.g., actual pistols).
- Validation for "pocket" or "secure container" slots (these remain unrestricted).
