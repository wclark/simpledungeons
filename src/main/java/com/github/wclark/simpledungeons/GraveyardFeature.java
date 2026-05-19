package com.github.wclark.simpledungeons;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class GraveyardFeature extends Feature<NoneFeatureConfiguration> {
    private static final int HALF_WIDTH = 18;
    private static final int HALF_DEPTH = 16;
    private static final int CLEAR_HEIGHT = 12;
    private static final int MAX_HEIGHT_VARIANCE = 2;
    private static final int MIN_SPAWN_DISTANCE_BLOCKS = 96;
    private static final int MAX_SPAWN_DISTANCE_BLOCKS = 256;
    private static final int SPAWN_GRAVEYARD_SCAN_RADIUS = 64;

    public GraveyardFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!Config.ENABLE_SURFACE_CRYPT.getAsBoolean()) {
            return false;
        }

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockPos centerGround = groundAt(level, origin.getX(), origin.getZ());

        if (!isNearButNotOnSpawn(level, centerGround)) {
            return false;
        }

        if (!isUsableArea(level, centerGround)) {
            return false;
        }

        return placeGraveyard(level, centerGround, context.random());
    }

    public static boolean ensureSpawnGraveyard(ServerLevel level) {
        if (!Config.ENABLE_SURFACE_CRYPT.getAsBoolean() || level.dimension() != Level.OVERWORLD) {
            return false;
        }

        BlockPos spawn = level.getSharedSpawnPos();
        BlockPos centerGround = groundAt(level, spawn.getX(), spawn.getZ());
        if (hasGraveyardMarkerNear(level, centerGround, SPAWN_GRAVEYARD_SCAN_RADIUS)) {
            return false;
        }

        return placeGraveyard(level, centerGround, level.random);
    }

    private static boolean placeGraveyard(WorldGenLevel level, BlockPos centerGround, RandomSource random) {
        prepareGround(level, centerGround, random);
        placeFence(level, centerGround);
        placeCryptEntrance(level, centerGround, random);
        placeGraves(level, centerGround, random);
        placeDecor(level, centerGround, random);
        return true;
    }

    private static boolean isNearButNotOnSpawn(WorldGenLevel level, BlockPos centerGround) {
        if (level.getLevel().dimension() != Level.OVERWORLD) {
            return false;
        }

        BlockPos spawn = level.getLevel().getSharedSpawnPos();
        int dx = centerGround.getX() - spawn.getX();
        int dz = centerGround.getZ() - spawn.getZ();
        int distanceSqr = dx * dx + dz * dz;
        return distanceSqr >= MIN_SPAWN_DISTANCE_BLOCKS * MIN_SPAWN_DISTANCE_BLOCKS
                && distanceSqr <= MAX_SPAWN_DISTANCE_BLOCKS * MAX_SPAWN_DISTANCE_BLOCKS;
    }

    private static boolean isUsableArea(WorldGenLevel level, BlockPos centerGround) {
        int minY = centerGround.getY();
        int maxY = centerGround.getY();

        for (int x = -HALF_WIDTH; x <= HALF_WIDTH; x++) {
            for (int z = -HALF_DEPTH; z <= HALF_DEPTH; z++) {
                BlockPos ground = groundAt(level, centerGround.getX() + x, centerGround.getZ() + z);
                BlockState groundState = level.getBlockState(ground);
                BlockState surfaceState = level.getBlockState(ground.above());

                if (!isStableGround(groundState) || groundState.getFluidState().getType() != Fluids.EMPTY) {
                    return false;
                }

                if (surfaceState.is(BlockTags.LOGS) || surfaceState.is(BlockTags.LEAVES) || !surfaceState.getFluidState().isEmpty()) {
                    return false;
                }

                minY = Math.min(minY, ground.getY());
                maxY = Math.max(maxY, ground.getY());
            }
        }

        return maxY - minY <= MAX_HEIGHT_VARIANCE;
    }

    private static boolean isStableGround(BlockState state) {
        return state.is(BlockTags.DIRT)
                || state.is(BlockTags.SAND)
                || state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(Blocks.SNOW)
                || state.is(Blocks.SNOW_BLOCK);
    }

    private static void prepareGround(WorldGenLevel level, BlockPos centerGround, RandomSource random) {
        for (int x = -HALF_WIDTH; x <= HALF_WIDTH; x++) {
            for (int z = -HALF_DEPTH; z <= HALF_DEPTH; z++) {
                BlockPos ground = new BlockPos(centerGround.getX() + x, centerGround.getY(), centerGround.getZ() + z);
                shapeGroundColumn(level, ground);

                BlockState groundState = random.nextFloat() < 0.22F
                        ? Blocks.COARSE_DIRT.defaultBlockState()
                        : Blocks.GRASS_BLOCK.defaultBlockState();
                if (Math.abs(x) <= 1 || Math.abs(z) <= 1 || (Math.abs(x) <= 4 && z <= -8)) {
                    groundState = random.nextFloat() < 0.7F ? Blocks.DIRT_PATH.defaultBlockState() : Blocks.COARSE_DIRT.defaultBlockState();
                }

                level.setBlock(ground, groundState, 2);
            }
        }
    }

    private static void placeFence(WorldGenLevel level, BlockPos centerGround) {
        BlockState wall = Blocks.COBBLESTONE_WALL.defaultBlockState();
        for (int x = -HALF_WIDTH; x <= HALF_WIDTH; x++) {
            placeWall(level, centerGround.getX() + x, centerGround.getZ() - HALF_DEPTH, wall);
            placeWall(level, centerGround.getX() + x, centerGround.getZ() + HALF_DEPTH, wall);
        }

        for (int z = -HALF_DEPTH; z <= HALF_DEPTH; z++) {
            placeWall(level, centerGround.getX() - HALF_WIDTH, centerGround.getZ() + z, wall);
            placeWall(level, centerGround.getX() + HALF_WIDTH, centerGround.getZ() + z, wall);
        }

        for (int x = -1; x <= 1; x++) {
            BlockPos gateGround = groundAt(level, centerGround.getX() + x, centerGround.getZ() + HALF_DEPTH);
            level.setBlock(gateGround.above(), Blocks.AIR.defaultBlockState(), 2);
        }
    }

    private static void placeCryptEntrance(WorldGenLevel level, BlockPos centerGround, RandomSource random) {
        BlockState stairDown = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
        BlockState topSlab = Blocks.STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP);

        for (int x = -4; x <= 4; x++) {
            for (int z = -14; z <= -9; z++) {
                BlockPos ground = groundAt(level, centerGround.getX() + x, centerGround.getZ() + z);
                clearColumn(level, ground);
                level.setBlock(ground, randomBrick(random), 2);

                boolean edge = x == -4 || x == 4 || z == -14 || z == -9;
                boolean doorway = z == -9 && Math.abs(x) <= 1;
                if (edge && !doorway) {
                    level.setBlock(ground.above(), randomBrick(random), 2);
                    level.setBlock(ground.above(2), randomBrick(random), 2);
                }

                if (edge && !doorway && random.nextFloat() < 0.18F) {
                    level.setBlock(ground.above(3), Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState(), 2);
                }

                if (Math.abs(x) <= 3 && z >= -13 && z <= -10) {
                    level.setBlock(ground.above(3), topSlab, 2);
                }
            }
        }

        for (int step = 0; step < 7; step++) {
            BlockPos surface = groundAt(level, centerGround.getX(), centerGround.getZ() - 8 + step);
            BlockPos stairPos = new BlockPos(surface.getX(), centerGround.getY() - step, surface.getZ());
            level.setBlock(stairPos, stairDown, 2);
            level.setBlock(stairPos.above(), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(stairPos.above(2), Blocks.AIR.defaultBlockState(), 2);
        }

        BlockPos sealedDoor = new BlockPos(centerGround.getX(), centerGround.getY() - 5, centerGround.getZ());
        level.setBlock(sealedDoor, Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 2);
        level.setBlock(sealedDoor.above(), Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 2);

        for (int x = -2; x <= 2; x += 4) {
            BlockPos torchGround = groundAt(level, centerGround.getX() + x, centerGround.getZ() - 8);
            level.setBlock(torchGround.above(), Blocks.SOUL_TORCH.defaultBlockState(), 2);
        }
    }

    private static void placeGraves(WorldGenLevel level, BlockPos centerGround, RandomSource random) {
        int[] xs = {-14, -10, -6, 6, 10, 14};
        int[] zs = {-5, -1, 3, 7, 11};

        for (int z : zs) {
            for (int x : xs) {
                if (random.nextFloat() < 0.1F) {
                    continue;
                }

                placeGrave(level, centerGround, x, z, random);
            }
        }
    }

    private static void placeGrave(WorldGenLevel level, BlockPos centerGround, int offsetX, int offsetZ, RandomSource random) {
        int worldX = centerGround.getX() + offsetX;
        int worldZ = centerGround.getZ() + offsetZ;
        BlockPos headGround = groundAt(level, worldX, worldZ - 1);
        BlockPos graveGround = groundAt(level, worldX, worldZ);
        BlockPos footGround = groundAt(level, worldX, worldZ + 1);

        clearColumn(level, headGround);
        clearColumn(level, graveGround);
        clearColumn(level, footGround);

        BlockState headstone = random.nextBoolean() ? Blocks.STONE_BRICK_WALL.defaultBlockState() : Blocks.COBBLESTONE_WALL.defaultBlockState();
        level.setBlock(headGround.above(), headstone, 2);
        if (random.nextFloat() < 0.45F) {
            level.setBlock(headGround.above(2), random.nextBoolean()
                    ? Blocks.STONE_BRICK_WALL.defaultBlockState()
                    : Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState(), 2);
        }

        level.setBlock(graveGround, Blocks.COARSE_DIRT.defaultBlockState(), 2);
        level.setBlock(footGround, SimpleDungeons.RESTLESS_GRAVE_SOIL.get().defaultBlockState(), 2);

        if (random.nextFloat() < 0.3F) {
            BlockPos sideGround = groundAt(level, worldX + (random.nextBoolean() ? 1 : -1), worldZ);
            level.setBlock(sideGround.above(), Blocks.DEAD_BUSH.defaultBlockState(), 2);
        }

        if (random.nextFloat() < 0.16F) {
            BlockPos candleGround = groundAt(level, worldX + (random.nextBoolean() ? 1 : -1), worldZ - 1);
            level.setBlock(candleGround.above(), Blocks.CANDLE.defaultBlockState(), 2);
        }
    }

    private static void placeDecor(WorldGenLevel level, BlockPos centerGround, RandomSource random) {
        for (int i = 0; i < 34; i++) {
            int x = centerGround.getX() + random.nextInt(HALF_WIDTH * 2 - 2) - HALF_WIDTH + 1;
            int z = centerGround.getZ() + random.nextInt(HALF_DEPTH * 2 - 2) - HALF_DEPTH + 1;
            if (Math.abs(x - centerGround.getX()) <= 2 || Math.abs(z - centerGround.getZ()) <= 2) {
                continue;
            }

            BlockPos ground = groundAt(level, x, z);
            BlockPos above = ground.above();
            if (level.getBlockState(ground).is(SimpleDungeons.RESTLESS_GRAVE_SOIL.get())
                    || !level.getBlockState(above).isAir()) {
                continue;
            }

            float roll = random.nextFloat();
            if (roll < 0.34F) {
                level.setBlock(above, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            } else if (roll < 0.62F) {
                level.setBlock(above, Blocks.COBWEB.defaultBlockState(), 2);
            } else if (roll < 0.8F) {
                level.setBlock(above, Blocks.COBBLESTONE_WALL.defaultBlockState(), 2);
            } else {
                level.setBlock(above, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2);
            }
        }
    }

    private static void placeWall(WorldGenLevel level, int x, int z, BlockState wall) {
        BlockPos ground = groundAt(level, x, z);
        clearColumn(level, ground);
        level.setBlock(ground.above(), wall, 2);
    }

    private static void shapeGroundColumn(WorldGenLevel level, BlockPos ground) {
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

    private static void clearColumn(WorldGenLevel level, BlockPos ground) {
        for (int y = 1; y <= CLEAR_HEIGHT; y++) {
            BlockPos pos = ground.above(y);
            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    private static BlockState randomBrick(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.18F) {
            return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        }

        if (roll < 0.34F) {
            return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        }

        return Blocks.STONE_BRICKS.defaultBlockState();
    }

    private static boolean hasGraveyardMarkerNear(ServerLevel level, BlockPos centerGround, int radius) {
        int radiusSqr = radius * radius;
        for (BlockPos pos : BlockPos.betweenClosed(
                centerGround.offset(-radius, -8, -radius),
                centerGround.offset(radius, 8, radius))) {
            int dx = pos.getX() - centerGround.getX();
            int dz = pos.getZ() - centerGround.getZ();
            if (dx * dx + dz * dz > radiusSqr) {
                continue;
            }

            BlockState state = level.getBlockState(pos);
            if (state.is(SimpleDungeons.RESTLESS_GRAVE_SOIL.get())
                    || state.is(Blocks.CRACKED_STONE_BRICKS)
                    || state.is(Blocks.MOSSY_STONE_BRICKS)) {
                return true;
            }
        }

        return false;
    }

    private static BlockPos groundAt(WorldGenLevel level, int x, int z) {
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).below();
    }
}
