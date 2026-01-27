# Product Guidelines

## Technical Architecture
- **Layer-based Organization:** The codebase follows a clear separation by technical layer (e.g., `client`, `network`, `registry`, `item`). This aligns with standard Minecraft modding practices and facilitates easier navigation between client-side rendering and server-side logic.
- **Strict Modularity:** Systems such as firearms, health management, and inventory should remain decoupled. Changes to the health system (e.g., adding a new injury type) should not require modifications to the gun logic.
- **Data-Driven Configuration:** Game balance and item statistics (gun damage, reload speeds, healing amounts) MUST be defined via Minecraft's **Data Maps** and **JSON** files. This allows for rapid iteration and community balancing without requiring code recompilation.

## Asset & Visual Standards
- **Texture Resolution:** All item and block textures should adhere to the standard **16x16** Minecraft resolution to maintain a "Vanilla" aesthetic.
- **Model Complexity:** 3D models (e.g., firearms) must be **low-poly**. They should provide enough detail to be recognizable as tactical gear while respecting the blocky nature of the Minecraft world and ensuring high performance.

## UI/UX Principles
- **Information-Rich HUD:** The user interface should prioritize clarity. Essential data—including current health status of body parts, remaining ammunition, and raid time—must be clearly visible to the player to support tactical decision-making.
- **Custom HUD:** To ensure maximum control over the tactical experience, all vanilla Minecraft HUD elements (except for the crosshair) are hidden by default, providing a clean slate for the custom, high-fidelity UI.

## Quality Standards
- **Modular Code:** Prioritize writing clean, independent modules. Use events and data components to facilitate communication between systems rather than tight coupling.
