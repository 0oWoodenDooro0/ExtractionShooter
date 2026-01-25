# Implementation Plan: Tag-based Equipment Validation Expansion

## Phase 1: Tag Registry and Data Generation [checkpoint: 5fa4478]
- [x] Task: Define new TagKeys in `ModTags.kt` 18e5959
    - [x] Add `BACKPACKS`, `RIGS`, `PRIMARY_WEAPONS`, `PISTOLS`, and `GUNS` to `ModTags.kt`
- [x] Task: Update `ModItemTagProvider` for data generation 9a4e27c
    - [x] Add `extractionshooter:m4a1` to `GUNS` and `PRIMARY_WEAPONS`
    - [x] Add `extractionshooter:backpack` to `BACKPACKS`
    - [x] Add `extractionshooter:rig" to `RIGS`
    - [x] Set up hierarchical relationship (e.g., `PRIMARY_WEAPONS` and `PISTOLS` include `GUNS`)
- [x] Task: Run Data Generation 9a4e27c
    - [x] Execute `./gradlew runData` and verify JSON files in `src/generated/resources`
- [x] Task: Verify compilation 9a4e27c
    - [x] Run `./gradlew classes`
- [x] Task: Conductor - User Manual Verification 'Phase 1: Tag Registry and Data Generation' (Protocol in workflow.md) 5fa4478

## Phase 2: Refactor EquipmentValidator [checkpoint: 655509a]
- [x] Task: Update `EquipmentValidator.kt` logic 2185393
    - [x] Replace `true` defaults with `stack.is(ModTags.TAG_NAME)` for `backpack`, `tactical_rig`, `primary_1`, `primary_2`, and `pistol`
- [x] Task: Verify compilation 2185393
    - [x] Run `./gradlew classes`
- [x] Task: Conductor - User Manual Verification 'Phase 2: Refactor EquipmentValidator' (Protocol in workflow.md) 655509a

## Phase 3: Final Verification
- [ ] Task: Final compilation and data check
    - [ ] Ensure all generated resources are tracked and code compiles
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Final Verification' (Protocol in workflow.md)
