package com.github.wclark.simpledungeons;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
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
    private static final int HALF_WIDTH = 13;
    private static final int HALF_DEPTH = 11;
    private static final int MAX_HEIGHT_VARIANCE = 2;
    private static final int MIN_SPAWN_DISTANCE_BLOCKS = 96;
    private static final int MAX_SPAWN_DISTANCE_BLOCKS = 256;

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

        prepareGround(level, centerGround, context.random());
        placeFence(level, centerGround);
        placeCryptEntrance(level, centerGround);
        placeGraves(level, centerGround, context.random());
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
                BlockPos ground = groundAt(level, centerGround.getX() + x, centerGround.getZ() + z);
                clearColumn(level, ground);

                BlockState groundState = random.nextFloat() < 0.22F
                        ? Blocks.COARSE_DIRT.defaultBlockState()
                        : Blocks.GRASS_BLOCK.defaultBlockState();
                if (Math.abs(x) <= 1 || Math.abs(z) <= 1) {
                    groundState = random.nextFloat() < 0.65F ? Blocks.DIRT_PATH.defaultBlockState() : Blocks.COARSE_DIRT.defaultBlockState();
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

    private static void placeCryptEntrance(WorldGenLevel level, BlockPos centerGround) {
        BlockState bricks = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState cracked = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        BlockState stairDown = Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.SOUTH);
        BlockState topSlab = Blocks.STONE_BRICK_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP);

        for (int x = -3; x <= 3; x++) {
            for (int z = -10; z <= -6; z++) {
                BlockPos ground = groundAt(level, centerGround.getX() + x, centerGround.getZ() + z);
                clearColumn(level, ground);
                level.setBlock(ground, bricks, 2);

                boolean edge = x == -3 || x == 3 || z == -10 || z == -6;
                boolean doorway = z == -6 && Math.abs(x) <= 1;
                if (edge && !doorway) {
                    level.setBlock(ground.above(), bricks, 2);
                    level.setBlock(ground.above(2), (x + z) % 3 == 0 ? cracked : bricks, 2);
                }

                if (Math.abs(x) <= 2 && z >= -9 && z <= -7) {
                    level.setBlock(ground.above(3), topSlab, 2);
                }
            }
        }

        for (int step = 0; step < 5; step++) {
            BlockPos surface = groundAt(level, centerGround.getX(), centerGround.getZ() - 5 + step);
            BlockPos stairPos = new BlockPos(surface.getX(), centerGround.getY() - step, surface.getZ());
            level.setBlock(stairPos, stairDown, 2);
            level.setBlock(stairPos.above(), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(stairPos.above(2), Blocks.AIR.defaultBlockState(), 2);
        }

        BlockPos sealedDoor = new BlockPos(centerGround.getX(), centerGround.getY() - 5, centerGround.getZ());
        level.setBlock(sealedDoor, cracked, 2);
        level.setBlock(sealedDoor.above(), cracked, 2);
    }

    private static void placeGraves(WorldGenLevel level, BlockPos centerGround, RandomSource random) {
        int[] xs = {-9, -5, -1, 3, 7};
        int[] zs = {-2, 2, 6};

        for (int z : zs) {
            for (int x : xs) {
                if (random.nextFloat() < 0.08F) {
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
        level.setBlock(graveGround, Blocks.COARSE_DIRT.defaultBlockState(), 2);
        level.setBlock(footGround, SimpleDungeons.RESTLESS_GRAVE_SOIL.get().defaultBlockState(), 2);

        if (random.nextFloat() < 0.35F) {
            BlockPos sideGround = groundAt(level, worldX + (random.nextBoolean() ? 1 : -1), worldZ);
            level.setBlock(sideGround.above(), Blocks.DEAD_BUSH.defaultBlockState(), 2);
        }
    }

    private static void placeWall(WorldGenLevel level, int x, int z, BlockState wall) {
        BlockPos ground = groundAt(level, x, z);
        clearColumn(level, ground);
        level.setBlock(ground.above(), wall, 2);
    }

    private static void clearColumn(WorldGenLevel level, BlockPos ground) {
        for (int y = 1; y <= 5; y++) {
            BlockPos pos = ground.above(y);
            BlockState state = level.getBlockState(pos);
            if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) {
                continue;
            }

            if (!state.isAir()) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
            }
        }
    }

    private static BlockPos groundAt(WorldGenLevel level, int x, int z) {
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).below();
    }
}
