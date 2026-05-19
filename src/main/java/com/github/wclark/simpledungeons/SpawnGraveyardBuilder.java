package com.github.wclark.simpledungeons;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.Heightmap;

public final class SpawnGraveyardBuilder {
    private static final int HALF_WIDTH = 24;
    private static final int HALF_DEPTH = 20;
    private static final int CLEAR_HEIGHT = 14;
    private static final int MARKER_OFFSET_X = HALF_WIDTH - 2;
    private static final int MARKER_OFFSET_Z = -HALF_DEPTH + 2;

    private SpawnGraveyardBuilder() {
    }

    public static boolean ensureSpawnGraveyard(ServerLevel level) {
        if (!Config.ENABLE_SURFACE_CRYPT.getAsBoolean() || level.dimension() != Level.OVERWORLD) {
            return false;
        }

        BlockPos spawn = level.getSharedSpawnPos();
        BlockPos centerGround = groundAt(level, spawn.getX(), spawn.getZ());
        if (hasCurrentBuildMarker(level, centerGround)) {
            return false;
        }

        build(level, centerGround, level.random);
        placeCurrentBuildMarker(level, centerGround);
        return true;
    }

    private static void build(ServerLevel level, BlockPos centerGround, RandomSource random) {
        prepareGround(level, centerGround, random);
        placeOuterWall(level, centerGround, random);
        placePaths(level, centerGround, random);
        placeCrypt(level, centerGround, random);
        placeGraveRows(level, centerGround, random);
        scatterWeathering(level, centerGround, random);
    }

    private static void prepareGround(ServerLevel level, BlockPos centerGround, RandomSource random) {
        for (int x = -HALF_WIDTH; x <= HALF_WIDTH; x++) {
            for (int z = -HALF_DEPTH; z <= HALF_DEPTH; z++) {
                BlockPos ground = new BlockPos(centerGround.getX() + x, centerGround.getY(), centerGround.getZ() + z);
                shapeGroundColumn(level, ground);
                level.setBlock(ground, random.nextFloat() < 0.28F
                        ? Blocks.COARSE_DIRT.defaultBlockState()
                        : Blocks.GRASS_BLOCK.defaultBlockState(), 2);
            }
        }
    }

    private static void placeOuterWall(ServerLevel level, BlockPos centerGround, RandomSource random) {
        for (int x = -HALF_WIDTH; x <= HALF_WIDTH; x++) {
            placeBrokenWall(level, centerGround.getX() + x, centerGround.getZ() - HALF_DEPTH, random);
            placeBrokenWall(level, centerGround.getX() + x, centerGround.getZ() + HALF_DEPTH, random);
        }

        for (int z = -HALF_DEPTH; z <= HALF_DEPTH; z++) {
            placeBrokenWall(level, centerGround.getX() - HALF_WIDTH, centerGround.getZ() + z, random);
            placeBrokenWall(level, centerGround.getX() + HALF_WIDTH, centerGround.getZ() + z, random);
        }

        clearGate(level, centerGround.getX(), centerGround.getZ() + HALF_DEPTH, 2);
        clearGate(level, centerGround.getX(), centerGround.getZ() - HALF_DEPTH, 2);
    }

    private static void placePaths(ServerLevel level, BlockPos centerGround, RandomSource random) {
        for (int z = -HALF_DEPTH + 1; z <= HALF_DEPTH - 1; z++) {
            for (int x = -2; x <= 2; x++) {
                placePath(level, centerGround.getX() + x, centerGround.getZ() + z, random);
            }
        }

        for (int x = -HALF_WIDTH + 2; x <= HALF_WIDTH - 2; x++) {
            for (int z = -1; z <= 1; z++) {
                placePath(level, centerGround.getX() + x, centerGround.getZ() + z, random);
            }
        }
    }

    private static void placeCrypt(ServerLevel level, BlockPos centerGround, RandomSource random) {
        BlockState stairDown = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
        BlockState topSlab = Blocks.STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP);

        for (int x = -5; x <= 5; x++) {
            for (int z = -18; z <= -11; z++) {
                BlockPos ground = groundAt(level, centerGround.getX() + x, centerGround.getZ() + z);
                clearColumn(level, ground);
                level.setBlock(ground, agedBrick(random), 2);

                boolean edge = x == -5 || x == 5 || z == -18 || z == -11;
                boolean doorway = z == -11 && Math.abs(x) <= 1;
                if (edge && !doorway) {
                    level.setBlock(ground.above(), agedBrick(random), 2);
                    level.setBlock(ground.above(2), agedBrick(random), 2);
                    if (random.nextFloat() < 0.24F) {
                        level.setBlock(ground.above(3), Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState(), 2);
                    }
                }

                if (Math.abs(x) <= 4 && z >= -17 && z <= -12) {
                    level.setBlock(ground.above(3), topSlab, 2);
                }
            }
        }

        for (int step = 0; step < 8; step++) {
            BlockPos surface = groundAt(level, centerGround.getX(), centerGround.getZ() - 10 + step);
            BlockPos stairPos = new BlockPos(surface.getX(), centerGround.getY() - step, surface.getZ());
            level.setBlock(stairPos, stairDown, 2);
            level.setBlock(stairPos.above(), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(stairPos.above(2), Blocks.AIR.defaultBlockState(), 2);
        }

        BlockPos sealedDoor = new BlockPos(centerGround.getX(), centerGround.getY() - 6, centerGround.getZ());
        level.setBlock(sealedDoor, Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 2);
        level.setBlock(sealedDoor.above(), Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 2);

        for (int x = -3; x <= 3; x += 6) {
            BlockPos lampGround = groundAt(level, centerGround.getX() + x, centerGround.getZ() - 10);
            level.setBlock(lampGround.above(), Blocks.SOUL_LANTERN.defaultBlockState(), 2);
        }
    }

    private static void placeGraveRows(ServerLevel level, BlockPos centerGround, RandomSource random) {
        int[] leftXs = {-19, -15, -11, -7};
        int[] rightXs = {7, 11, 15, 19};
        int[] zs = {-7, -3, 5, 9, 13, 17};

        for (int z : zs) {
            for (int x : leftXs) {
                if (random.nextFloat() >= 0.08F) {
                    placeGrave(level, centerGround, x, z, random);
                }
            }

            for (int x : rightXs) {
                if (random.nextFloat() >= 0.08F) {
                    placeGrave(level, centerGround, x, z, random);
                }
            }
        }
    }

    private static void placeGrave(ServerLevel level, BlockPos centerGround, int offsetX, int offsetZ, RandomSource random) {
        int worldX = centerGround.getX() + offsetX;
        int worldZ = centerGround.getZ() + offsetZ;
        BlockPos headGround = groundAt(level, worldX, worldZ - 1);
        BlockPos bodyGround = groundAt(level, worldX, worldZ);
        BlockPos triggerGround = groundAt(level, worldX, worldZ + 1);

        clearColumn(level, headGround);
        clearColumn(level, bodyGround);
        clearColumn(level, triggerGround);

        BlockState headstone = random.nextBoolean()
                ? Blocks.STONE_BRICK_WALL.defaultBlockState()
                : Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState();
        level.setBlock(headGround.above(), headstone, 2);
        if (random.nextFloat() < 0.55F) {
            level.setBlock(headGround.above(2), headstone, 2);
        }

        level.setBlock(bodyGround, Blocks.COARSE_DIRT.defaultBlockState(), 2);
        level.setBlock(triggerGround, SimpleDungeons.RESTLESS_GRAVE_SOIL.get().defaultBlockState(), 2);

        if (random.nextFloat() < 0.22F) {
            BlockPos candleGround = groundAt(level, worldX + (random.nextBoolean() ? 1 : -1), worldZ - 1);
            level.setBlock(candleGround.above(), Blocks.CANDLE.defaultBlockState(), 2);
        }
    }

    private static void scatterWeathering(ServerLevel level, BlockPos centerGround, RandomSource random) {
        for (int i = 0; i < 70; i++) {
            int x = centerGround.getX() + random.nextInt(HALF_WIDTH * 2 - 4) - HALF_WIDTH + 2;
            int z = centerGround.getZ() + random.nextInt(HALF_DEPTH * 2 - 4) - HALF_DEPTH + 2;
            if (Math.abs(x - centerGround.getX()) <= 3 || Math.abs(z - centerGround.getZ()) <= 2) {
                continue;
            }

            BlockPos ground = groundAt(level, x, z);
            BlockPos above = ground.above();
            if (level.getBlockState(ground).is(SimpleDungeons.RESTLESS_GRAVE_SOIL.get())
                    || !level.getBlockState(above).isAir()) {
                continue;
            }

            float roll = random.nextFloat();
            if (roll < 0.36F) {
                level.setBlock(above, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            } else if (roll < 0.58F) {
                level.setBlock(above, Blocks.COBWEB.defaultBlockState(), 2);
            } else if (roll < 0.74F) {
                level.setBlock(above, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
            } else if (roll < 0.88F) {
                level.setBlock(above, Blocks.COBBLESTONE_WALL.defaultBlockState(), 2);
            } else {
                level.setBlock(above, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2);
            }
        }
    }

    private static void placeBrokenWall(ServerLevel level, int x, int z, RandomSource random) {
        BlockPos ground = groundAt(level, x, z);
        clearColumn(level, ground);

        if (random.nextFloat() < 0.16F) {
            return;
        }

        BlockState wall = random.nextFloat() < 0.45F
                ? Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState()
                : Blocks.COBBLESTONE_WALL.defaultBlockState();
        level.setBlock(ground.above(), wall, 2);
        if (random.nextFloat() < 0.18F) {
            level.setBlock(ground.above(2), wall, 2);
        }
    }

    private static void clearGate(ServerLevel level, int centerX, int z, int radius) {
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            BlockPos ground = groundAt(level, x, z);
            level.setBlock(ground.above(), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(ground.above(2), Blocks.AIR.defaultBlockState(), 2);
        }
    }

    private static void placePath(ServerLevel level, int x, int z, RandomSource random) {
        BlockPos ground = groundAt(level, x, z);
        level.setBlock(ground, random.nextFloat() < 0.72F
                ? Blocks.DIRT_PATH.defaultBlockState()
                : Blocks.COARSE_DIRT.defaultBlockState(), 2);
    }

    private static void shapeGroundColumn(ServerLevel level, BlockPos ground) {
        BlockPos surface = groundAt(level, ground.getX(), ground.getZ());
        if (surface.getY() < ground.getY()) {
            for (int y = surface.getY(); y < ground.getY(); y++) {
                level.setBlock(new BlockPos(ground.getX(), y, ground.getZ()), Blocks.DIRT.defaultBlockState(), 2);
            }
        } else if (surface.getY() > ground.getY()) {
            for (int y = ground.getY() + 1; y <= surface.getY(); y++) {
                level.setBlock(new BlockPos(ground.getX(), y, ground.getZ()), Blocks.AIR.defaultBlockState(), 2);
            }
        }

        clearColumn(level, ground);
    }

    private static void clearColumn(ServerLevel level, BlockPos ground) {
        for (int y = 1; y <= CLEAR_HEIGHT; y++) {
            BlockPos pos = ground.above(y);
            if (!level.getBlockState(pos).isAir()) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    private static BlockState agedBrick(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.18F) {
            return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        }

        if (roll < 0.4F) {
            return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        }

        return Blocks.STONE_BRICKS.defaultBlockState();
    }

    private static boolean hasCurrentBuildMarker(ServerLevel level, BlockPos centerGround) {
        BlockPos markerGround = buildMarkerGround(centerGround);
        return level.getBlockState(markerGround).is(Blocks.CHISELED_STONE_BRICKS)
                && level.getBlockState(markerGround.above()).is(Blocks.SOUL_LANTERN);
    }

    private static void placeCurrentBuildMarker(ServerLevel level, BlockPos centerGround) {
        BlockPos markerGround = buildMarkerGround(centerGround);
        level.setBlock(markerGround, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 2);
        level.setBlock(markerGround.above(), Blocks.SOUL_LANTERN.defaultBlockState(), 2);
    }

    private static BlockPos buildMarkerGround(BlockPos centerGround) {
        return centerGround.offset(MARKER_OFFSET_X, 0, MARKER_OFFSET_Z);
    }

    private static BlockPos groundAt(ServerLevel level, int x, int z) {
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).below();
    }
}
