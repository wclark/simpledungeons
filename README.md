# Simple Dungeons

Simple Dungeons is a Minecraft mod concept focused on adding three memorable dungeon structures to exploration: one in the sky, one on the surface, and one underground.

## Mod Identity

- Repository: `simpledungeons`
- Mod name: `Simple Dungeons`
- Mod id: `simpledungeons`
- Minecraft: `1.21.1`
- Mod loader: NeoForge `21.1.219`
- Java: `21`
- Current status: NeoForge project scaffold

## Dungeon Concepts

### Sky Island Dungeon

A dungeon built into or on top of a floating island. This should feel rare, visible, and a little dangerous to reach, with vertical traversal, exposed ledges, and sky-themed rewards.

### Surface Crypt

A crypt-style dungeon found on the surface, either freestanding or partially sunken into terrain. This should read clearly from a distance, invite exploration, and work well in overworld biomes where a ruined burial site feels natural.

### Cave-Bound Underground Dungeon

An underground dungeon that always generates inside a cave. The surrounding cave should be part of the structure experience, not just a tunnel leading to it. Generation should preserve a natural cave opening or chamber so the dungeon feels discovered rather than pasted into stone.

## Initial Roadmap

1. Define structure generation rules for all three dungeon types.
2. Build placeholder structure templates and loot tables.
3. Add biome, height, spacing, and rarity configuration.
4. Wire generation into NeoForge worldgen.
5. Iterate on dungeon layouts, hazards, rewards, and polish.

## Development

Import this folder as a Gradle project in Eclipse or IntelliJ IDEA.

Useful commands:

```powershell
.\gradlew.bat build
.\gradlew.bat runClient
.\gradlew.bat data
```

The built mod jar appears in `build/libs`.

## Notes

The loader and Minecraft versions are pinned to match the existing Simple Spells project, which uses NeoForge `21.1.219` for Minecraft `1.21.1`.
