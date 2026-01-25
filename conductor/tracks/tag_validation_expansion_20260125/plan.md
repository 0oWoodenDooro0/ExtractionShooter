# Implementation Plan: Tag-based Equipment Validation Expansion

## Phase 1: Tag Registry and Data Generation
- [x] Task: Define new TagKeys in `ModTags.kt` 18e5959
    - [ ] Add `BACKPACKS`, `RIGS`, `PRIMARY_WEAPONS`, `PISTOLS`, and `GUNS` to `ModTags.kt`
- [ ] Task: Update `ModItemTagProvider` for data generation
    - [ ] Add `extractionshooter:m4a1` to `GUNS` and `PRIMARY_WEAPONS`
    - [ ] Add `extractionshooter:backpack` to `BACKPACKS`
    - [ ] Add `extractionshooter:rig" to `RIGS`
    - [ ] Set up hierarchical relationship (e.g., `PRIMARY_WEAPONS` and `PISTOLS` include `GUNS`)
- [ ] Task: Run Data Generation
    - [ ] Execute `./gradlew runData` and verify JSON files in `src/generated/resources`
- [ ] Task: Verify compilation
    - [ ] Run `./gradlew classes`
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Tag Registry and Data Generation' (Protocol in workflow.md)

## Phase 2: Refactor EquipmentValidator
- [ ] Task: Update `EquipmentValidator.kt` logic
    - [ ] Replace `true` defaults with `stack.is(ModTags.TAG_NAME)` for `backpack`, `tactical_rig`, `primary_1`, `primary_2`, and `pistol`
- [ ] Task: Verify compilation
    - [ ] Run `./gradlew classes`
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Refactor EquipmentValidator' (Protocol in workflow.md)

## Phase 3: Final Verification
- [ ] Task: Final compilation and data check
    - [ ] Ensure all generated resources are tracked and code compiles
- [ ] Task: Conductor - User Manual Verification 'Phase 3: Final Verification' (Protocol in workflow.md)
