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
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public final class SpawnGraveyardStructure {
    private static final int HALF_WIDTH = 30;
    private static final int HALF_DEPTH = 22;
    private static final int CLEAR_HEIGHT = 15;
    private static final int LOWER_CRYPT_FLOOR_Y = -14;
    private static final int LOWER_CRYPT_CEILING_Y = -4;

    private SpawnGraveyardStructure() {
    }

    public static boolean ensureAtSpawn(ServerLevel level) {
        if (!Config.ENABLE_SURFACE_CRYPT.getAsBoolean() || level.dimension() != Level.OVERWORLD) {
            return false;
        }

        BlockPos spawn = level.getSharedSpawnPos();
        BlockPos centerGround = groundAt(level, spawn.getX(), spawn.getZ());
        build(level, centerGround, level.random);
        return true;
    }

    private static void build(ServerLevel level, BlockPos centerGround, RandomSource random) {
        prepareGround(level, centerGround, random);
        placePathNetwork(level, centerGround, random);
        placeFenceAndPillars(level, centerGround, random);
        placeFrontArch(level, centerGround);
        placeGravePlots(level, centerGround, random);
        placeCryptShell(level, centerGround, random);
        placeCryptInterior(level, centerGround, random);
        placeLowerCrypt(level, centerGround, random);
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
                BlockPos ground = at(centerGround, x, z);
                shapeGroundColumn(level, ground);
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

            placeIronBars(level, at(centerGround, x, -HALF_DEPTH), true);
            if (Math.abs(x) > 4) {
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
            level.setBlock(ground.above(5), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
            if (Math.abs(x) <= 2) {
                level.setBlock(ground.above(4), Blocks.DEEPSLATE_TILES.defaultBlockState(), 2);
            }
        }

        placeEntranceLantern(level, at(centerGround, -3, HALF_DEPTH));
        placeEntranceLantern(level, at(centerGround, 3, HALF_DEPTH));
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

    private static void placeCryptShell(ServerLevel level, BlockPos centerGround, RandomSource random) {
        for (int x = -7; x <= 7; x++) {
            for (int z = -20; z <= -10; z++) {
                BlockPos ground = at(centerGround, x, z);
                clearColumn(level, ground);
                level.setBlock(ground, agedStone(random), 2);

                boolean wall = x == -7 || x == 7 || z == -20 || z == -10;
                boolean doorway = z == -10 && Math.abs(x) <= 1;
                if (wall && !doorway) {
                    for (int y = 1; y < cryptRoofY(x); y++) {
                        level.setBlock(ground.above(y), agedStone(random), 2);
                    }
                }
            }
        }

        for (int x = -8; x <= 8; x++) {
            for (int z = -21; z <= -9; z++) {
                level.setBlock(at(centerGround, x, z).above(cryptRoofY(x)), roofBlock(random), 2);
            }
        }

        for (int z = -21; z <= -9; z++) {
            level.setBlock(at(centerGround, 0, z).above(7), Blocks.DEEPSLATE_TILES.defaultBlockState(), 2);
        }

        for (int x = -1; x <= 1; x++) {
            level.setBlock(at(centerGround, x, -10).above(), Blocks.AIR.defaultBlockState(), 2);
            level.setBlock(at(centerGround, x, -10).above(2), Blocks.AIR.defaultBlockState(), 2);
        }

        level.setBlock(at(centerGround, -3, -9).above(), Blocks.SOUL_LANTERN.defaultBlockState(), 2);
        level.setBlock(at(centerGround, 3, -9).above(), Blocks.SOUL_LANTERN.defaultBlockState(), 2);
    }

    private static void placeCryptInterior(ServerLevel level, BlockPos centerGround, RandomSource random) {
        for (int x = -6; x <= 6; x++) {
            for (int z = -19; z <= -11; z++) {
                BlockPos floor = at(centerGround, x, z);
                level.setBlock(floor, cryptFloorBlock(random), 2);
                for (int y = 1; y < cryptRoofY(x); y++) {
                    level.setBlock(floor.above(y), Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }

        for (int x = -1; x <= 1; x++) {
            for (int y = 1; y <= 3; y++) {
                level.setBlock(at(centerGround, x, y, -10), Blocks.AIR.defaultBlockState(), 2);
            }
        }

        for (int x = -3; x <= 3; x++) {
            for (int z = -17; z <= -12; z++) {
                level.setBlock(at(centerGround, x, z), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
            }
        }

        placeWaterChute(level, centerGround);

        for (int x = -5; x <= 5; x += 10) {
            level.setBlock(at(centerGround, x, 1, -13), Blocks.AZALEA_LEAVES.defaultBlockState(), 2);
            level.setBlock(at(centerGround, x, 1, -14), Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState(), 2);
        }

        level.setBlock(at(centerGround, 0, 2, -20), Blocks.GLOWSTONE.defaultBlockState(), 2);
        level.setBlock(at(centerGround, -1, 2, -20), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
        level.setBlock(at(centerGround, 1, 2, -20), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
        level.setBlock(at(centerGround, 0, 3, -20), Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 2);
        level.setBlock(at(centerGround, -1, 1, -20), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);
        level.setBlock(at(centerGround, 1, 1, -20), Blocks.MOSSY_COBBLESTONE.defaultBlockState(), 2);

        level.setBlock(at(centerGround, -4, 1, -18), candle(), 2);
        level.setBlock(at(centerGround, 4, 1, -18), candle(), 2);
        level.setBlock(at(centerGround, -5, 1, -11), Blocks.COBWEB.defaultBlockState(), 2);
        level.setBlock(at(centerGround, 5, 2, -19), Blocks.COBWEB.defaultBlockState(), 2);
    }

    private static void placeWaterChute(ServerLevel level, BlockPos centerGround) {
        for (int y = LOWER_CRYPT_FLOOR_Y + 1; y <= 0; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -16; z <= -14; z++) {
                    level.setBlock(at(centerGround, x, y, z), Blocks.WATER.defaultBlockState(), 2);
                }
            }

            if (y >= LOWER_CRYPT_FLOOR_Y + 3 && y < 0) {
                for (int x = -2; x <= 2; x++) {
                    level.setBlock(at(centerGround, x, y, -17), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
                    level.setBlock(at(centerGround, x, y, -13), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
                }

                for (int z = -16; z <= -14; z++) {
                    level.setBlock(at(centerGround, -2, y, z), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
                    level.setBlock(at(centerGround, 2, y, z), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
                }
            }
        }

        for (int x = -3; x <= 3; x++) {
            for (int z = -18; z <= -12; z++) {
                if (Math.abs(x) <= 1 && z >= -16 && z <= -14) {
                    continue;
                }

                level.setBlock(at(centerGround, x, LOWER_CRYPT_FLOOR_Y, z), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
            }
        }
    }

    private static void placeLowerCrypt(ServerLevel level, BlockPos centerGround, RandomSource random) {
        int minX = -16;
        int maxX = 16;
        int minZ = -30;
        int maxZ = 0;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                for (int y = LOWER_CRYPT_FLOOR_Y; y <= LOWER_CRYPT_CEILING_Y; y++) {
                    boolean shell = x == minX || x == maxX || z == minZ || z == maxZ;
                    BlockPos pos = at(centerGround, x, y, z);

                    if (y == LOWER_CRYPT_FLOOR_Y) {
                        level.setBlock(pos, lowerFloorBlock(random), 2);
                    } else if (y == LOWER_CRYPT_CEILING_Y) {
                        level.setBlock(pos, lowerCeilingBlock(random), 2);
                    } else if (shell) {
                        level.setBlock(pos, lowerWallBlock(random), 2);
                    } else {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }

        placeWaterChute(level, centerGround);
        placeLowerCryptPillars(level, centerGround);
        placeLowerCryptArches(level, centerGround);
        placeLowerCryptShrine(level, centerGround);
        placeLowerCryptDetails(level, centerGround);
    }

    private static void placeLowerCryptPillars(ServerLevel level, BlockPos centerGround) {
        int[] zs = {-25, -18, -11, -4};
        for (int z : zs) {
            placeLowerPillar(level, centerGround, -11, z);
            placeLowerPillar(level, centerGround, 11, z);
        }

        placeLowerPillar(level, centerGround, -4, -24);
        placeLowerPillar(level, centerGround, 4, -24);
    }

    private static void placeLowerPillar(ServerLevel level, BlockPos centerGround, int x, int z) {
        BlockPos floor = at(centerGround, x, LOWER_CRYPT_FLOOR_Y, z);
        level.setBlock(floor, Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
        level.setBlock(floor.above(), Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 2);
        for (int y = LOWER_CRYPT_FLOOR_Y + 2; y <= LOWER_CRYPT_CEILING_Y - 2; y++) {
            level.setBlock(at(centerGround, x, y, z), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
        }
        level.setBlock(at(centerGround, x, LOWER_CRYPT_CEILING_Y - 1, z), Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 2);
    }

    private static void placeLowerCryptArches(ServerLevel level, BlockPos centerGround) {
        for (int x = -4; x <= 4; x++) {
            level.setBlock(at(centerGround, x, LOWER_CRYPT_FLOOR_Y + 1, -30), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
            level.setBlock(at(centerGround, x, LOWER_CRYPT_FLOOR_Y + 5, -30), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
        }

        for (int y = LOWER_CRYPT_FLOOR_Y + 1; y <= LOWER_CRYPT_FLOOR_Y + 5; y++) {
            level.setBlock(at(centerGround, -5, y, -30), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
            level.setBlock(at(centerGround, 5, y, -30), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
        }

        level.setBlock(at(centerGround, -4, LOWER_CRYPT_FLOOR_Y + 4, -29), stair(Direction.EAST), 2);
        level.setBlock(at(centerGround, 4, LOWER_CRYPT_FLOOR_Y + 4, -29), stair(Direction.WEST), 2);
        level.setBlock(at(centerGround, 0, LOWER_CRYPT_FLOOR_Y + 6, -29), Blocks.STONE_BRICK_SLAB.defaultBlockState(), 2);

        for (int z = -28; z <= -2; z += 8) {
            level.setBlock(at(centerGround, -16, LOWER_CRYPT_FLOOR_Y + 2, z), Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 2);
            level.setBlock(at(centerGround, 16, LOWER_CRYPT_FLOOR_Y + 2, z), Blocks.CHISELED_STONE_BRICKS.defaultBlockState(), 2);
        }
    }

    private static void placeLowerCryptShrine(ServerLevel level, BlockPos centerGround) {
        for (int x = -3; x <= 3; x++) {
            for (int z = -27; z <= -23; z++) {
                level.setBlock(at(centerGround, x, LOWER_CRYPT_FLOOR_Y + 1, z), Blocks.STONE_BRICKS.defaultBlockState(), 2);
            }
        }

        for (int x = -2; x <= 2; x++) {
            level.setBlock(at(centerGround, x, LOWER_CRYPT_FLOOR_Y + 2, -25), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
        }

        level.setBlock(at(centerGround, 0, LOWER_CRYPT_FLOOR_Y + 3, -25), Blocks.REDSTONE_BLOCK.defaultBlockState(), 2);
        level.setBlock(at(centerGround, -2, LOWER_CRYPT_FLOOR_Y + 2, -23), candle(), 2);
        level.setBlock(at(centerGround, 2, LOWER_CRYPT_FLOOR_Y + 2, -23), candle(), 2);
    }

    private static void placeLowerCryptDetails(ServerLevel level, BlockPos centerGround) {
        int floor = LOWER_CRYPT_FLOOR_Y;
        for (int z = -29; z <= -1; z++) {
            if (z % 5 == 0) {
                level.setBlock(at(centerGround, 0, floor, z), Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
                level.setBlock(at(centerGround, -1, floor, z), Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 2);
                level.setBlock(at(centerGround, 1, floor, z), Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), 2);
            }
        }

        level.setBlock(at(centerGround, -14, floor + 1, -28), Blocks.COBWEB.defaultBlockState(), 2);
        level.setBlock(at(centerGround, 14, floor + 2, -1), Blocks.COBWEB.defaultBlockState(), 2);
        level.setBlock(at(centerGround, -15, floor + 3, -6), Blocks.COBWEB.defaultBlockState(), 2);
        level.setBlock(at(centerGround, 15, floor + 1, -22), Blocks.COBWEB.defaultBlockState(), 2);

        for (int z = -26; z <= -4; z += 6) {
            level.setBlock(at(centerGround, -13, floor + 1, z), candle(), 2);
            level.setBlock(at(centerGround, 13, floor + 1, z), candle(), 2);
        }
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
        level.setBlock(ground, Blocks.COBBLED_DEEPSLATE.defaultBlockState(), 2);
        for (int y = 1; y <= height; y++) {
            level.setBlock(ground.above(y), y % 2 == 0 ? Blocks.DEEPSLATE_BRICKS.defaultBlockState() : Blocks.POLISHED_DEEPSLATE.defaultBlockState(), 2);
        }

        level.setBlock(ground.above(height + 1), endRod(), 2);
    }

    private static void placeEntranceLantern(ServerLevel level, BlockPos ground) {
        level.setBlock(ground.above(), Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState(), 2);
        level.setBlock(ground.above(2), Blocks.LANTERN.defaultBlockState(), 2);
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
        BlockPos ground = at(centerGround, x, z);
        shapeGroundColumn(level, ground);
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

        if (roll < 0.82F) {
            return Blocks.STONE_BRICKS.defaultBlockState();
        }

        return Blocks.POLISHED_ANDESITE.defaultBlockState();
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

    private static BlockState cryptFloorBlock(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.45F) {
            return Blocks.POLISHED_DEEPSLATE.defaultBlockState();
        }

        if (roll < 0.7F) {
            return Blocks.DEEPSLATE_BRICKS.defaultBlockState();
        }

        if (roll < 0.85F) {
            return Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState();
        }

        return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
    }

    private static BlockState lowerFloorBlock(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.38F) {
            return Blocks.POLISHED_DEEPSLATE.defaultBlockState();
        }

        if (roll < 0.66F) {
            return Blocks.DEEPSLATE_BRICKS.defaultBlockState();
        }

        if (roll < 0.84F) {
            return Blocks.STONE_BRICKS.defaultBlockState();
        }

        return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
    }

    private static BlockState lowerWallBlock(RandomSource random) {
        float roll = random.nextFloat();
        if (roll < 0.52F) {
            return Blocks.DEEPSLATE_BRICKS.defaultBlockState();
        }

        if (roll < 0.76F) {
            return Blocks.CRACKED_DEEPSLATE_BRICKS.defaultBlockState();
        }

        if (roll < 0.9F) {
            return Blocks.POLISHED_DEEPSLATE.defaultBlockState();
        }

        return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
    }

    private static BlockState lowerCeilingBlock(RandomSource random) {
        return random.nextFloat() < 0.75F
                ? Blocks.DEEPSLATE_TILES.defaultBlockState()
                : Blocks.COBBLED_DEEPSLATE.defaultBlockState();
    }

    private static BlockState stair(Direction facing) {
        return Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, facing);
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

    private static int cryptRoofY(int x) {
        return Math.abs(x) <= 2 ? 6 : Math.abs(x) <= 5 ? 5 : 4;
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

    private static BlockPos at(BlockPos centerGround, int x, int z) {
        return new BlockPos(centerGround.getX() + x, centerGround.getY(), centerGround.getZ() + z);
    }

    private static BlockPos at(BlockPos centerGround, int x, int y, int z) {
        return new BlockPos(centerGround.getX() + x, centerGround.getY() + y, centerGround.getZ() + z);
    }

    private static BlockPos groundAt(ServerLevel level, int x, int z) {
        return level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(x, 0, z)).below();
    }
}
