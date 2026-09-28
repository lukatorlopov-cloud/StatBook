# StatBook

A lightweight Fabric client mod for Minecraft 1.20.1 that opens a compact GUI with two tabs:
- Mobs
- Blocks

The mod stores simple local statistics and lets you browse them with search and sorting by count.

## Features
- Press `B` to open the book
- Search by name
- Show a list of mobs and blocks with counts
- Favorite weapon / favorite tool tracking
- Commands for testing and data entry:
  - `/statbook addkill Skeleton Diamond_Sword`
  - `/statbook addblock Obsidian Netherite_Pickaxe`
  - `/statbook clear`

## Run
1. Install JDK 17
2. Open the project in your terminal
3. Run:
   - Windows: `gradlew.bat runClient`
   - Linux/macOS: `./gradlew runClient`

If you want, I can also retarget this project to another Fabric/Minecraft version (for example 1.21.x or 1.20.4) and add deeper automatic stat tracking by event hooks.
