# StatBook

Client-side Fabric mod for Minecraft 1.20.1.

## Current functionality
- Press `'` (apostrophe) to open the book; the key can be changed in Minecraft controls.
- Automatically counts blocks broken by the local player through Fabric's client block-break event.
- Detects kills of living entities where the local player was the attacker and records the final held weapon.
- Stores data in `.minecraft/config/statbook.json`, so it persists across restarts.
- Search field and descending count sorting in both tabs.
- Tab buttons, with the active tab visibly disabled.

Build with JDK 17:
- Windows: `gradlew.bat build`
- Linux/macOS: `./gradlew build`

The mod targets Minecraft 1.20.1 because the repository's current Gradle properties use that version. Minecraft `26.2` is not a released Fabric version in this project; retargeting requires the exact Minecraft release and matching Yarn/Fabric Loader/API coordinates.
