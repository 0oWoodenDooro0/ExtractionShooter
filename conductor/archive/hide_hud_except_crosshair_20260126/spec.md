# Specification: Hide All Vanilla HUD Elements Except Crosshair

## Overview
This feature aims to clear the screen of all standard Minecraft HUD elements (Health, Food, Armor, Hotbar, Experience, etc.), leaving only the Crosshair visible. This provides a clean slate for a custom UI or a more immersive experience.

## Functional Requirements
1.  **Event Subscription**:
    -   The system must subscribe to the `RenderGuiLayerEvent.Pre` on the NeoForge client-side event bus.

2.  **Filtering Logic**:
    -   The event handler must check the `GuiLayer` associated with the event.
    -   **Condition**: If the layer is **NOT** `VanillaGuiLayers.CROSSHAIR`.

3.  **Cancellation**:
    -   If the condition is met (i.e., it is any layer other than the crosshair), the event must be cancelled (`event.setCanceled(true)`).
    -   This applies unconditionally.

## Non-Functional Requirements
-   **Performance**: The check must be lightweight as this event fires every frame for every GUI layer.
-   **Client-Side Only**: This logic is strictly visual and must only run on the client distribution.

## Acceptance Criteria
-   [ ] The **Crosshair** remains visible in the center of the screen.
-   [ ] **All other vanilla HUD elements** are hidden. This includes but is not limited to:
    -   Hotbar
    -   Item Name text
    -   Health Hearts
    -   Food/Hunger bar
    -   Armor bar
    -   Air bubbles
    -   Experience bar
    -   Mount health/jump bar
-   [ ] No errors or crashes occur during HUD rendering.
