# Dungeon Design

This document captures the first design pass for Simple Dungeons.

## Design Goals

- Add a small set of high-quality dungeon structures rather than many generic ones.
- Make each dungeon type feel tied to where it generates.
- Give players a clear reason to explore vertically, across the surface, and underground.
- Keep structure generation predictable enough to tune, but rare enough to feel special.

## Sky Island Dungeon

### Placement

- Generates above the overworld terrain on a floating island.
- Should avoid spawning too close to world height limits.
- Should be rare enough that seeing one from the ground feels exciting.

### Shape

- Floating island base with dungeon rooms embedded into the island or built on top.
- Possible broken bridges, exposed stairs, towers, or small exterior platforms.
- Entry could be visible from below to hint that the island is explorable.

### Gameplay Notes

- Fall risk should be part of the challenge.
- Loot can lean toward exploration, movement, or sky-themed utility.
- Enemies should account for open edges and vertical space.

## Surface Crypt

### Placement

- Generates on the surface.
- Works best in plains, forests, dark forests, swamps, taigas, and similar overworld biomes.
- Can be partially buried or ruined so it blends into terrain.

### Shape

- Stone crypt entrance with stairs leading down.
- Burial rooms, alcoves, cracked walls, hidden chambers, and a final tomb.
- Exterior silhouette should be recognizable without being oversized.

### Gameplay Notes

- Good fit for undead enemies, traps, and sealed rooms.
- Loot can include bones, old tools, enchanted books, grave goods, and rare crypt rewards.
- The entrance should remain discoverable after terrain placement.

## Cave-Bound Underground Dungeon

### Placement

- Generates underground only when connected to, or overlapping with, a cave.
- The cave should be part of the structure itself.
- Generation should avoid fully sealed placements with no natural access.

### Shape

- Built around a natural cave chamber or tunnel.
- Dungeon walls, supports, platforms, and rooms should adapt around open cave space.
- Could include collapsed passages, exposed ore pockets, hanging chains, or carved ruins.

### Gameplay Notes

- The cave should create irregular sight lines and approaches.
- Loot can reward deep exploration and mining-adjacent progression.
- The structure should feel like it was discovered inside a cave, not simply generated beside one.

## Implementation Notes To Resolve

- Target Minecraft `1.21.1` with NeoForge `21.1.219` so Simple Dungeons can run beside Simple Spells.
- Decide whether structures should use jigsaw pieces, static NBT templates, or a hybrid.
- Define rarity, spacing, biome tags, and height constraints after choosing the target Minecraft version.
- Add config options once the core generation behavior works.
