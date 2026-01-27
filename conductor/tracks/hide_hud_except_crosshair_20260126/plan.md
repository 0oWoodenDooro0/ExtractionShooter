# Plan: Hide All Vanilla HUD Elements Except Crosshair

## Phase 1: Infrastructure & Event Handling
- [x] Task: Create HUD event handler class 4b616d1
    - [ ] Create `com.gmail.vincent031525.extractionshooter.client.event.HudEventHandler`
    - [ ] Add `@EventBusSubscriber` annotation with `Dist.CLIENT`
- [ ] Task: Implement layer cancellation logic
    - [ ] Create a method listening to `RenderGuiLayerEvent.Pre`
    - [ ] Implement conditional check: `if (event.getName() != VanillaGuiLayers.CROSSHAIR)`
    - [ ] Cancel the event if the condition matches
- [ ] Task: Conductor - User Manual Verification 'Phase 1: Infrastructure & Event Handling' (Protocol in workflow.md)

## Phase 2: Verification
- [ ] Task: Manual verification in-game
    - [ ] Launch the game client
    - [ ] Verify that only the crosshair is visible
    - [ ] Verify that hotbar, health, food, etc., are all hidden
- [ ] Task: Conductor - User Manual Verification 'Phase 2: Verification' (Protocol in workflow.md)
