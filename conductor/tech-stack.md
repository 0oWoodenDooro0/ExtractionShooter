# Technology Stack

## Core Technologies
- **Programming Language:** [Kotlin](https://kotlinlang.org/) (JVM 21) - Chosen for its modern features, safety, and excellent interoperability with Java and the Minecraft ecosystem.
- **Mod Loader:** [NeoForge](https://neoforged.net/) - A community-driven fork of Forge, providing a modern and robust foundation for Minecraft modding.
- **Build System:** [Gradle](https://gradle.org/) - Utilizing the official NeoForge mod development plugin for automated building, dependency management, and run configuration setup.

## Specialized Libraries
- **[GeckoLib](https://geckolib.com/):** Used for handling complex 3D models and animations (using the `.geo.json` and `.animation.json` formats), essential for realistic firearm handling and custom entities.
- **[Kotlin for Forge](https://github.com/TheDarkColour/KotlinForForge):** Provides the necessary bootstrap logic and Kotlin language provider support for running Kotlin-based mods on the NeoForge platform.

## Development Tools
- **Mappings:** [Parchment](https://parchmentmc.org/) - Utilized to provide high-quality, human-readable names for Minecraft's internal methods and fields, improving code clarity.
- **Data Generation:** Built-in NeoForge data generation system is used to programmatically create JSON resources (assets and data maps), ensuring consistency and reducing manual error.
