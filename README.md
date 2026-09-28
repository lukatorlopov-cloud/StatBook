# StatBook

Client-side Fabric mod for Minecraft 26.2.

## Requirements
- Java 25
- Fabric Loader 0.19.5
- Yarn mappings `26.2+build.5`
- Fabric API `0.160.0+26.2`

## Features
- Press `'` to open the book.
- Two tabs: Mobs and Blocks.
- Auto-detects mob kills and block breaks made by the local player.
- Tracks favorite weapon/tool.
- Search and descending count sorting.
- Saves data to `.minecraft/config/statbook.json`.

## Build
- Windows: `gradlew.bat build`
- Linux/macOS: `./gradlew build`

Note: this project is targetted at the official Minecraft Java Edition 26.2 version, using Fabric dependencies for that release.
