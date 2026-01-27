# Plan: Hide All Vanilla HUD Elements Except Crosshair

## Phase 1: Infrastructure & Event Handling
- [x] Task: Create HUD event handler class 4b616d1
    - [x] Create `com.gmail.vincent031525.extractionshooter.client.event.HudEventHandler`
    - [x] Add `@EventBusSubscriber` annotation with `Dist.CLIENT`
- [x] Task: Implement layer cancellation logic 4b616d1
    - [x] Create a method listening to `RenderGuiLayerEvent.Pre`
    - [x] Implement conditional check: `if (event.getName() != VanillaGuiLayers.CROSSHAIR)`
    - [x] Cancel the event if the condition matches
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Infrastructure & Event Handling' (Protocol in workflow.md)

## Phase 2: Verification
- [ ] Task: Manual verification in-game
    - [ ] Launch the game client
    - [ ] Verify that only the crosshair is visible
    - [ ] Verify that hotbar, health, food, etc., are all hidden
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Verification' (Protocol in workflow.md)
