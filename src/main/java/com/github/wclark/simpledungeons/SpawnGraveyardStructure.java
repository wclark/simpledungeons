package com.github.wclark.simpledungeons;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.Heightmap;

public final class SpawnGraveyardStructure {
    private static final int HALF_WIDTH = 30;
    private static final int HALF_DEPTH = 22;
    private static final int CLEAR_HEIGHT = 15;
    private static final int MARKER_X = HALF_WIDTH - 2;
    private static final int MARKER_Z = -HALF_DEPTH + 2;

    private SpawnGraveyardStructure() {
    }

    public static boolean ensureAtSpawn(ServerLevel level) {
        if (!Config.ENABLE_SURFACE_CRYPT.getAsBoolean() || level.dimension() != Level.OVERWORLD) {
            return false;
        }

        BlockPos spawn = level.getSharedSpawnPos();
        BlockPos centerGround = groundAt(level, spawn.getX(), spawn.getZ());
        if (hasMarker(level, centerGround)) {
            return false;
        }

        build(level, centerGround, level.random);
        placeMarker(level, centerGround);
        return true;
    }

    private static void build(ServerLevel level, BlockPos centerGround, RandomSource random) {
        prepareGround(level, centerGround, random);
        placePathNetwork(level, centerGround, random);
        placeFenceAndPillars(level, centerGround, random);
        placeFrontArch(level, centerGround);
        placeGravePlots(level, centerGround, random);
        placeWoodenFrames(level, centerGround);
        placeCryptShell(level, centerGround, random);
        placeDetails(level, centerGround, random);
    }

    private static void prepareGround(ServerLevel level, BlockPos centerGround, RandomSource random) {
        for (int x = -HALF_WIDTH; x <= HALF_WIDTH; x++) {
            for (int z = -HALF_DEPTH; z <= HALF_DEPTH; z++) {
                BlockPos ground = at(centerGround, x, z);
                shapeGroundColumn(level, ground);
                level.setBlock(ground, random.nextFloat() < 0.18F ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.GRASS_BLOCK.defaultBlockState(), 2);
            }
        }

        for (int z = HALF_DEPTH + 1; z <= HALF_DEPTH + 8; z++) {
            for (int x = -3; x <= 3; x++) {
                BlockPos ground = groundAt(level, centerGround.getX() + x, centerGround.getZ() + z);
                clearColumn(level, ground);
                level.setBlock(ground, pathBlock(random), 2);
            }
        }
    }

    private static void placePathNetwork(ServerLevel level, BlockPos centerGround, RandomSource random) {
        for (int z = -HALF_DEPTH + 1; z <= HALF_DEPTH + 8; z++) {
            for (int x = -3; x <= 3; x++) {
                setPath(level, centerGround, x, z, random);
            }
        }

        for (int x = -HALF_WIDTH + 2; x <= HALF_WIDTH - 2; x++) {
            for (int z = -2; z <= 2; z++) {
                setPath(level, centerGround, x, z, random);
            }
        }

        for (int x = -HALF_WIDTH + 3; x <= HALF_WIDTH - 3; x++) {
            setPath(level, centerGround, x, 13, random);
        }

        for (int z = -HALF_DEPTH + 3; z <= HALF_DEPTH - 3; z++) {
            setPath(level, centerGround, -23, z, random);
            setPath(level, centerGround, 23, z, random);
        }
    }

    private static void placeFenceAndPillars(ServerLevel level, BlockPos centerGround, RandomSource random) {
        int[] northSouthPillars = {-30, -22, -14, -6, 6, 14, 22, 30};
        int[] eastWestPillars = {-22, -14, -6, 2, 10, 18, 22};

        for (int x = -HALF_WIDTH; x <= HALF_WIDTH; x++) {
            if (isBetweenAny(x, northSouthPillars, 1)) {
                placeLampPillar(level, at(centerGround, x, -HALF_DEPTH), 3);
                placeLampPillar(level, at(centerGround, x, HALF_DEPTH), 3);
                continue;
            }

            if (Math.abs(x) > 4) {
                placeIronBars(level, at(centerGround, x, -HALF_DEPTH), true);
                placeIronBars(level, at(centerGround, x, HALF_DEPTH), true);
            }
        }

        for (int z = -HALF_DEPTH; z <= HALF_DEPTH; z++) {
            if (isBetweenAny(z, eastWestPillars, 1)) {
                placeLampPillar(level, at(centerGround, -HALF_WIDTH, z), 3);
                placeLampPillar(level, at(centerGround, HALF_WIDTH, z), 3);
                continue;
            }

            placeIronBars(level, at(centerGround, -HALF_WIDTH, z), false);
            placeIronBars(level, at(centerGround, HALF_WIDTH, z), false);
        }

        for (int x = -2; x <= 2; x++) {
            clearColumn(level, at(centerGround, x, HALF_DEPTH));
        }
    }

    private static void placeFrontArch(ServerLevel level, BlockPos centerGround) {
        placeLampPillar(level, at(centerGround, -5, HALF_DEPTH), 5);
        placeLampPillar(level, at(centerGround, 5, HALF_DEPTH), 5);

        for (int x = -4; x <= 4; x++) {
            BlockPos ground = at(centerGround, x, HALF_DEPTH);
            level.setBlock(ground.above(5), Blocks.POLISHED_DIORITE.defaultBlockState(), 2);
            if (Math.abs(x) <= 2) {
                level.setBlock(ground.above(4), Blocks.CALCITE.defaultBlockState(), 2);
            }
        }

        level.setBlock(at(centerGround, -3, HALF_DEPTH).above(2), Blocks.LANTERN.defaultBlockState(), 2);
        level.setBlock(at(centerGround, 3, HALF_DEPTH).above(2), Blocks.LANTERN.defaultBlockState(), 2);
    }

    private static void placeGravePlots(ServerLevel level, BlockPos centerGround, RandomSource random) {
        int[] xs = {-25, -20, -15, -10, 10, 15, 20, 25};
        int[] zs = {-14, -9, -4, 7, 12, 17};

        for (int z : zs) {
            for (int x : xs) {
                if (isInsideCrypt(x, z) || random.nextFloat() < 0.1F) {
                    continue;
                }

                placeGrave(level, centerGround, x, z, random);
            }
        }

        placeLargeMemorial(level, centerGround, -12, -15);
        placeLargeMemorial(level, centerGround, 14, 6);
    }

    private static void placeGrave(ServerLevel level, BlockPos centerGround, int x, int z, RandomSource random) {
        BlockPos head = at(centerGround, x, z - 1);
        BlockPos body = at(centerGround, x, z);
        BlockPos foot = at(centerGround, x, z + 1);

        clearColumn(level, head);
        clearColumn(level, body);
        clearColumn(level, foot);

        level.setBlock(body, random.nextBoolean() ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.PODZOL.defaultBlockState(), 2);
        level.setBlock(foot, random.nextBoolean() ? Blocks.COARSE_DIRT.defaultBlockState() : Blocks.ROOTED_DIRT.defaultBlockState(), 2);
        level.setBlock(head.above(), randomHeadstone(random), 2);
        if (random.nextFloat() < 0.35F) {
            level.setBlock(head.above(2), randomHeadstone(random), 2);
        }

        if (random.nextFloat() < 0.2F) {
            level.setBlock(body.above(), candle(), 2);
        }

        if (random.nextFloat() < 0.2F) {
            level.setBlock(at(centerGround, x + (random.nextBoolean() ? 1 : -1), z).above(), Blocks.BLUE_ORCHID.defaultBlockState(), 2);
        }
    }

    private static void placeLargeMemorial(ServerLevel level, BlockPos centerGround, int x, int z) {
        BlockPos base = at(centerGround, x, z);
        level.setBlock(base, Blocks.STONE_BRICKS.defaultBlockState(), 2);
        level.setBlock(base.above(), Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 2);
        level.setBlock(base.above(2), Blocks.STONE_BRICK_WALL.defaultBlockState(), 2);
        level.setBlock(base.above(3), endRod(), 2);
    }

    private static void placeWoodenFrames(ServerLevel level, BlockPos centerGround) {
        placeWoodenFrame(level, centerGround, -24, -7);
        placeWoodenFrame(level, centerGround, 25, -3);
        placeWoodenFrame(level, centerGround, 25, 14);
    }

    private static void placeWoodenFrame(ServerLevel level, BlockPos centerGround, int x, int z) {
        for (int dx = -1; dx <= 1; dx += 2) {
            for (int dz = -1; dz <= 1; dz += 2) {
                BlockPos post = at(centerGround, x + dx, z + dz);
                clearColumn(level, post);
                level.setBlock(post.above(), Blocks.OAK_FENCE.defaultBlockState(), 2);
                level.setBlock(post.above(2), Blocks.OAK_FENCE.defaultBlockState(), 2);
            }
        }

        for (int dx = -1; dx <= 1; dx++) {
            level.setBlock(at(centerGround, x + dx, z - 1).above(3), Blocks.OAK_PLANKS.defaultBlockState(), 2);
            level.setBlock(at(centerGround, x + dx, z + 1).above(3), Blocks.OAK_PLANKS.defaultBlockState(), 2);
        }

        for (int dz = -1; dz <= 1; dz++) {
            level.setBlock(at(centerGround, x - 1, z + dz).above(3), Blocks.OAK_PLANKS.defaultBlockState(), 2);
            level.setBlock(at(centerGround, x + 1, z + dz).above(3), Blocks.OAK_PLANKS.defaultBlockState(), 2);
        }
    }

    private static void placeCryptShell(ServerLevel level, BlockPos centerGround, RandomSource random) {
        for (int x = -7; x <= 7; x++) {
            for (int z = -20; z <= -10; z++) {
                BlockPos ground = at(centerGround, x, z);
                clearColumn(level, ground);
                level.setBlock(ground, agedStone(random), 2);

                boolean wall = x == -7 || x == 7 || z == -20 || z == -10;
                boolean doorway = z == -10 && Math.abs(x) <= 1;
                if (wall && !doorway) {
                    level.setBlock(ground.above(), agedStone(random), 2);
                    level.setBlock(ground.above(2), agedStone(random), 2);
                    level.setBlock(ground.above(3), agedStone(random), 2);
                }
            }
        }

        for (int x = -8; x <= 8; x++) {
            for (int z = -21; z <= -9; z++) {
                int roofY = Math.abs(x) <= 2 ? 6 : Math.abs(x) <= 5 ? 5 : 4;
                level.setBlock(at(centerGround, x, z).above(roofY), roofBlock(random), 2);
            }
        }

        for (int z = -21; z <= -9; z++) {
            level.setBlock(at(centerGround, 0, z).above(7), Blocks.DEEPSLATE_TILE_SLAB.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.TOP), 2);
        }

        for (int x = -1; x <= 1; x++) {
            level.setBlock(at(centerGround, x, -10).above(), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(at(centerGround, x, -10).above(2), Blocks.AIR.defaultBlockState(), 2);
        }

        level.setBlock(at(centerGround, 0, -11).above(), Blocks.BLACKSTONE.defaultBlockState(), 2);
        level.setBlock(at(centerGround, 0, -11).above(2), Blocks.BLACKSTONE.defaultBlockState(), 2);
        level.setBlock(at(centerGround, -3, -9).above(), Blocks.SOUL_LANTERN.defaultBlockState(), 2);
        level.setBlock(at(centerGround, 3, -9).above(), Blocks.SOUL_LANTERN.defaultBlockState(), 2);
    }

    private static void placeDetails(ServerLevel level, BlockPos centerGround, RandomSource random) {
        for (int i = 0; i < 85; i++) {
            int x = random.nextInt(HALF_WIDTH * 2 - 4) - HALF_WIDTH + 2;
            int z = random.nextInt(HALF_DEPTH * 2 - 4) - HALF_DEPTH + 2;
            if (Math.abs(x) <= 4 || Math.abs(z) <= 2 || isInsideCrypt(x, z)) {
                continue;
            }

            BlockPos ground = at(centerGround, x, z);
            BlockPos above = ground.above();
            if (!level.getBlockState(above).isAir()) {
                continue;
            }

            float roll = random.nextFloat();
            if (roll < 0.3F) {
                level.setBlock(above, Blocks.DEAD_BUSH.defaultBlockState(), 2);
            } else if (roll < 0.5F) {
                level.setBlock(above, Blocks.COBWEB.defaultBlockState(), 2);
            } else if (roll < 0.68F) {
                level.setBlock(above, Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
            } else if (roll < 0.84F) {
                level.setBlock(above, candle(), 2);
            } else {
                level.setBlock(above, Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2);
            }
        }
    }

    private static void placeLampPillar(ServerLevel level, BlockPos ground, int height) {
        clearColumn(level, ground);
        level.setBlock(ground, Blocks.COBBLESTONE.defaultBlockState(), 2);
        for (int y = 1; y <= height; y++) {
            level.setBlock(ground.above(y), y % 2 == 0 ? Blocks.CALCITE.defaultBlockState() : Blocks.POLISHED_DIORITE.defaultBlockState(), 2);
        }

        level.setBlock(ground.above(height + 1), Blocks.POLISHED_DIORITE_SLAB.defaultBlockState(), 2);
        level.setBlock(ground.above(height + 2), endRod(), 2);
    }

    private static void placeIronBars(ServerLevel level, BlockPos ground, boolean eastWest) {
        BlockState bars = Blocks.IRON_BARS.defaultBlockState()
                .setValue(CrossCollisionBlock.EAST, eastWest)
                .setValue(CrossCollisionBlock.WEST, eastWest)
                .setValue(CrossCollisionBlock.NORTH, !eastWest)
                .setValue(CrossCollisionBlock.SOUTH, !eastWest);
        level.setBlock(ground.above(), bars, 2);
        level.setBlock(ground.above(2), bars, 2);
    }

    private static void setPath(ServerLevel level, BlockPos centerGround, int x, int z, RandomSource random) {
        BlockPos ground = Math.abs(z) > HALF_DEPTH ? groundAt(level, centerGround.getX() + x, centerGround.getZ() + z) : at(centerGround, x, z);
        clearColumn(level, ground);
        level.setBlock(ground, pathBlock(random), 2);
    }

    private static BlockState pathBlock(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.42F) {
            return Blocks.COBBLESTONE.defaultBlockState();
        }

        if (roll < 0.62F) {
            return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        }

        if (roll < 0.8F) {
            return Blocks.STONE_BRICKS.defaultBlockState();
        }

        return Blocks.DIRT_PATH.defaultBlockState();
    }

    private static BlockState agedStone(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.25F) {
            return Blocks.COBBLESTONE.defaultBlockState();
        }

        if (roll < 0.5F) {
            return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        }

        if (roll < 0.7F) {
            return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        }

        return Blocks.STONE_BRICKS.defaultBlockState();
    }

    private static BlockState roofBlock(RandomSource random) {
        return random.nextBoolean() ? Blocks.DEEPSLATE_TILES.defaultBlockState() : Blocks.COBBLED_DEEPSLATE.defaultBlockState();
    }

    private static BlockState randomHeadstone(RandomSource random) {
        int pick = random.nextInt(4);
        if (pick == 0) {
            return Blocks.STONE_BRICK_WALL.defaultBlockState();
        }

        if (pick == 1) {
            return Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState();
        }

        if (pick == 2) {
            return Blocks.COBBLESTONE_WALL.defaultBlockState();
        }

        return Blocks.STONE_BRICKS.defaultBlockState();
    }

    private static BlockState candle() {
        return Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.LIT, true);
    }

    private static BlockState endRod() {
        return Blocks.END_ROD.defaultBlockState().setValue(DirectionalBlock.FACING, Direction.UP);
    }

    private static boolean isBetweenAny(int value, int[] anchors, int tolerance) {
        for (int anchor : anchors) {
            if (Math.abs(value - anchor) <= tolerance) {
                return true;
            }
        }

        return false;
    }

    private static boolean isInsideCrypt(int x, int z) {
        return x >= -9 && x <= 9 && z >= -21 && z <= -8;
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

    private static boolean hasMarker(ServerLevel level, BlockPos centerGround) {
        BlockPos marker = at(centerGround, MARKER_X, MARKER_Z);
        return level.getBlockState(marker).is(Blocks.CHISELED_STONE_BRICKS)
                && level.getBlockState(marker.above()).is(Blocks.END_ROD);
    }

    private static void placeMarker(ServerLevel level, BlockPos centerGround) {
        BlockPos marker = at(centerGround, MARKER_X, MARKER_Z);
        level.setBlock(marker, Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 2);
        level.setBlock(marker.above(), endRod(), 2);
    }

    private static BlockPos at(BlockPos centerGround, int x, int z) {
        return new BlockPos(centerGround.getX() + x, centerGround.getY(), centerGround.getZ() + z);
    }

    private static BlockPos groundAt(ServerLevel level, int x, int z) {
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).below();
    }
}
