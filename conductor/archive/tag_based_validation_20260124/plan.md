# Implementation Plan: Tag-Based Equipment Validation

## Phase 1: Tag Infrastructure
- [x] Task: Create `ModTags` Registry
    - [x] Create `src/main/kotlin/com/gmail/vincent031525/extractionshooter/registry/ModTags.kt`.
    - [x] Define `TagKey<Item>` for `HELMETS` and `ARMORS` under the `extractionshooter` namespace.
- [x] Task: Refactor `EquipmentValidator`
    - [x] Update `EquipmentValidator.kt` to use these tags for validation instead of hardcoded checks.
    - [x] Remove dependencies on `ArmorItem` class checks.
- [ ] Task: Conductor - User Manual Verification 'Tag Infrastructure' (Protocol in workflow.md)

## Phase 2: Data Generation
- [~] Task: Implement `ModItemTagProvider`
    - [ ] Create `src/main/kotlin/com/gmail/vincent031525/extractionshooter/datagen/ModItemTagProvider.kt` extending `ItemTagsProvider`.
    - [ ] Register this provider in `DataGenerators.kt`.
- [x] Task: Populate Tags
    - [x] In `ModItemTagProvider`, add the specified vanilla items (Leather, Iron, Gold, Diamond, Netherite) to `HELMETS` and `ARMORS`.
    - [x] Ensure Chainmail and Turtle Shell are NOT added.
    - [x] Check for existence of Copper armor items (if `Items` class has them) and add them if present.
- [ ] Task: Conductor - User Manual Verification 'Data Generation' (Protocol in workflow.md)

## Phase 3: Verification
- [x] Task: Verify Compilation
    - [x] Run `./gradlew classes` to ensure everything compiles correctly.
    - [x] Run `./gradlew runData` (or `clientData` run config) to generate the JSON files.
    - [x] Inspect generated JSON files in `src/generated/resources/data/extractionshooter/tags/item/`.
- [ ] Task: Conductor - User Manual Verification 'Final Verification' (Protocol in workflow.md)
