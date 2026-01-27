# Plan: Hide All Vanilla HUD Elements Except Crosshair

## Phase 1: Infrastructure & Event Handling [checkpoint: b88d310]
- [x] Task: Create HUD event handler class 4b616d1
    - [x] Create `com.gmail.vincent031525.extractionshooter.client.event.HudEventHandler`
    - [x] Add `@EventBusSubscriber` annotation with `Dist.CLIENT`
- [x] Task: Implement layer cancellation logic 4b616d1
    - [x] Create a method listening to `RenderGuiLayerEvent.Pre`
    - [x] Implement conditional check: `if (event.getName() != VanillaGuiLayers.CROSSHAIR)`
    - [x] Cancel the event if the condition matches
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Infrastructure & Event Handling' (Protocol in workflow.md)

## Phase 2: Verification [checkpoint: cd6dbc3]
- [x] Task: Manual verification in-game 4b616d1
    - [x] Launch the game client
    - [x] Verify that only the crosshair is visible
    - [x] Verify that hotbar, health, food, etc., are all hidden
- [x] Task: Conductor - User Manual Verification 'Phase 2: Verification' (Protocol in workflow.md) 4b616d1
