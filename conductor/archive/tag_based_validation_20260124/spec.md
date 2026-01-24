# Specification: Tag-Based Equipment Validation

## 1. Overview
This track refactors the `EquipmentValidator` system to use Minecraft Item Tags instead of hardcoded logic. It leverages Data Generation to create specific tags for helmets and chestplates, filtering a specific subset of vanilla items.

## 2. Functional Requirements

### 2.1 Tag-Based Validation
-   **Refactor Validator:** Update `EquipmentValidator` to check `itemStack.is(ModTags.HELMETS)` and `itemStack.is(ModTags.ARMORS)`.

### 2.2 Tag Data Generation
-   **Method:** Use the project's Data Generation system (`ItemTagsProvider`).
-   **Tag: `extractionshooter:helmets`**
    -   Include: Leather, Iron, Gold, Diamond, Netherite.
    -   **Explicitly Exclude:** Chainmail, Turtle Shell.
-   **Tag: `extractionshooter:armors`**
    -   Include: Leather, Iron, Gold, Diamond, Netherite.
    -   **Explicitly Exclude:** Chainmail.
-   **Copper Armor:** If `Items` class contains Copper Armor, add it. (Note: User request).

### 2.3 Verification
-   **Compilability:** The project must pass `./gradlew classes` successfully before the track is marked complete.

## 3. Technical Requirements
-   **Class:** `EquipmentValidator`
-   **New Class:** `ModTags` (to hold TagKeys).
-   **DataGen:** New `ModItemTagProvider` class registered to the `DataGenerators`.

## 4. Out of Scope
-   Adding new Copper Armor items (only tagging existing ones).
