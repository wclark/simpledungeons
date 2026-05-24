# Simple Dungeons Blockbench Workspace

Use this folder for Blockbench source projects and exports.

## Source projects

- `blockbench/projects/entities/` - Save `.bbmodel` files for mobs, bosses, and animated entity models.
- `blockbench/projects/items/` - Save `.bbmodel` files for held/item models, staffs, artifacts, and props.

## Export targets

For game-ready files, export or copy assets into the normal Minecraft resource paths:

- Entity textures: `src/main/resources/assets/simpledungeons/textures/entity/`
- Item textures: `src/main/resources/assets/simpledungeons/textures/item/`
- Item model JSON: `src/main/resources/assets/simpledungeons/models/item/`

For entity model code, export Java/entity model drafts into:

- `blockbench/exports/entity-models/`

Then convert or copy the final model pieces into `src/main/java/com/github/wclark/simpledungeons/client/`.

## Recommended Blockbench model types

- Items and handheld props: use `Java Block/Item`.
- Minecraft-style mobs: use `Modded Entity` or `Generic Model`, then export Java model code for review.

