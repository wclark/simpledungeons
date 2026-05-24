package com.github.wclark.simpledungeons;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public final class SpawnSkyIslandStructure {
    private static final int SURFACE_Y = 198;
    private static final int RADIUS_X = 86;
    private static final int RADIUS_Z = 64;
    private static final int MAX_TOP_VARIATION = 7;
    private static final int MAX_THICKNESS = 36;

    private SpawnSkyIslandStructure() {
    }

    public static boolean ensureAtSpawn(ServerLevel level) {
        if (!Config.ENABLE_SKY_ISLAND_DUNGEON.getAsBoolean() || level.dimension() != Level.OVERWORLD) {
            return false;
        }

        SkyIslandData data = SkyIslandData.get(level);
        if (data.isPlaced()) {
            return false;
        }

        BlockPos spawn = level.getSharedSpawnPos();
        int islandY = Math.min(level.getMaxBuildHeight() - 84, Math.max(150, SURFACE_Y));
        BlockPos center = new BlockPos(spawn.getX(), islandY, spawn.getZ());
        RandomSource random = RandomSource.create(level.getSeed() ^ 0x51a7d15L);
        build(level, center, random);
        data.markPlaced(center);
        return true;
    }

    private static void build(ServerLevel level, BlockPos center, RandomSource random) {
        buildIslandTerrain(level, center, level.getSeed());
        placePathNetwork(level, center, random);
        placePond(level, center, -24, 12, random);
        placeFoundations(level, center, random);
        placeTrees(level, center, random);
        placeLampPosts(level, center);
        placeVegetation(level, center, random);
        placeDanglingRoots(level, center, random);
    }

    private static void buildIslandTerrain(ServerLevel level, BlockPos center, long seed) {
        for (int x = -RADIUS_X; x <= RADIUS_X; x++) {
            for (int z = -RADIUS_Z; z <= RADIUS_Z; z++) {
                double nx = x / (double) RADIUS_X;
                double nz = z / (double) RADIUS_Z;
                double distance = nx * nx + nz * nz;
                double edgeNoise = (noise(seed, x / 5, z / 5, 9) - 4) * 0.012D;
                if (distance > 1.0D + edgeNoise) {
                    continue;
                }

                double centerWeight = Math.max(0.0D, 1.0D - distance);
                int topY = center.getY() + (int) Math.round(centerWeight * MAX_TOP_VARIATION) + noise(seed, x, z, 5) - 2;
                int thickness = 4 + (int) Math.round(centerWeight * MAX_THICKNESS) + noise(seed, x * 3, z * 3, 5);
                for (int y = topY - thickness; y <= topY; y++) {
                    BlockState state;
                    if (y == topY) {
                        state = topBlock(seed, x, z);
                    } else if (y >= topY - 4) {
                        state = dirtBlock(seed, x, z);
                    } else {
                        state = stoneCoreBlock(seed, x, z, y);
                    }

                    level.setBlock(new BlockPos(center.getX() + x, y, center.getZ() + z), state, 2);
                }
            }
        }
    }

    private static BlockState topBlock(long seed, int x, int z) {
        int pick = noise(seed ^ 0x71b5L, x, z, 12);
        if (pick == 0) {
            return Blocks.MOSS_BLOCK.defaultBlockState();
        }

        if (pick <= 2) {
            return Blocks.COARSE_DIRT.defaultBlockState();
        }

        return Blocks.GRASS_BLOCK.defaultBlockState();
    }

    private static BlockState dirtBlock(long seed, int x, int z) {
        int pick = noise(seed ^ 0x9ad31L, x, z, 7);
        if (pick == 0) {
            return Blocks.ROOTED_DIRT.defaultBlockState();
        }

        if (pick == 1) {
            return Blocks.COARSE_DIRT.defaultBlockState();
        }

        return Blocks.DIRT.defaultBlockState();
    }

    private static BlockState stoneCoreBlock(long seed, int x, int z, int y) {
        int pick = noise(seed ^ y, x, z, 10);
        if (pick <= 1) {
            return Blocks.ANDESITE.defaultBlockState();
        }

        if (pick <= 3) {
            return Blocks.COBBLESTONE.defaultBlockState();
        }

        return Blocks.STONE.defaultBlockState();
    }

    private static void placePathNetwork(ServerLevel level, BlockPos center, RandomSource random) {
        for (int x = -64; x <= 64; x++) {
            for (int z = -3; z <= 3; z++) {
                setSurfaceBlock(level, center, x, z, pathBlock(random));
            }
        }

        for (int z = -46; z <= 46; z++) {
            for (int x = -3; x <= 3; x++) {
                setSurfaceBlock(level, center, x, z, pathBlock(random));
            }
        }

        for (int x = -52; x <= 52; x++) {
            setSurfaceBlock(level, center, x, 28, pathBlock(random));
        }

        for (int x = -52; x <= 52; x++) {
            setSurfaceBlock(level, center, x, -30, pathBlock(random));
        }
    }

    private static BlockState pathBlock(RandomSource random) {
        int pick = random.nextInt(5);
        if (pick == 0) {
            return Blocks.GRAVEL.defaultBlockState();
        }

        if (pick == 1) {
            return Blocks.COARSE_DIRT.defaultBlockState();
        }

        return Blocks.DIRT_PATH.defaultBlockState();
    }

    private static void placePond(ServerLevel level, BlockPos center, int cx, int cz, RandomSource random) {
        for (int x = -9; x <= 9; x++) {
            for (int z = -6; z <= 6; z++) {
                double shape = (x * x) / 81.0D + (z * z) / 36.0D;
                if (shape > 1.0D) {
                    continue;
                }

                BlockPos surface = surfaceAt(level, center, cx + x, cz + z);
                if (surface == null) {
                    continue;
                }

                level.setBlock(surface, Blocks.WATER.defaultBlockState(), 2);
                level.setBlock(surface.below(), random.nextBoolean() ? Blocks.CLAY.defaultBlockState() : Blocks.STONE.defaultBlockState(), 2);
                level.setBlock(surface.above(), Blocks.AIR.defaultBlockState(), 2);
            }
        }

        for (int x = -11; x <= 11; x++) {
            for (int z = -8; z <= 8; z++) {
                double shape = (x * x) / 121.0D + (z * z) / 64.0D;
                if (shape < 0.9D || shape > 1.18D) {
                    continue;
                }

                setSurfaceBlock(level, center, cx + x, cz + z, random.nextBoolean() ? Blocks.MOSSY_COBBLESTONE.defaultBlockState() : Blocks.COBBLESTONE.defaultBlockState());
            }
        }
    }

    private static void placeFoundations(ServerLevel level, BlockPos center, RandomSource random) {
        placeFoundation(level, center, -46, -22, 18, 12, random);
        placeFoundation(level, center, 42, -26, 20, 14, random);
        placeFoundation(level, center, -48, 28, 16, 14, random);
        placeFoundation(level, center, 38, 30, 18, 12, random);
        placeFoundation(level, center, 10, -42, 24, 10, random);
        placeFoundation(level, center, -2, 42, 22, 12, random);
    }

    private static void placeFoundation(ServerLevel level, BlockPos center, int cx, int cz, int width, int depth, RandomSource random) {
        int halfWidth = width / 2;
        int halfDepth = depth / 2;
        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int z = -halfDepth; z <= halfDepth; z++) {
                BlockState floor = random.nextInt(5) == 0 ? Blocks.MOSSY_STONE_BRICKS.defaultBlockState() : Blocks.STONE_BRICKS.defaultBlockState();
                setSurfaceBlock(level, center, cx + x, cz + z, floor);
            }
        }

        for (int x = -halfWidth; x <= halfWidth; x += halfWidth * 2) {
            for (int z = -halfDepth; z <= halfDepth; z++) {
                placeLowWall(level, center, cx + x, cz + z, random);
            }
        }

        for (int z = -halfDepth; z <= halfDepth; z += halfDepth * 2) {
            for (int x = -halfWidth; x <= halfWidth; x++) {
                placeLowWall(level, center, cx + x, cz + z, random);
            }
        }
    }

    private static void placeLowWall(ServerLevel level, BlockPos center, int x, int z, RandomSource random) {
        if (random.nextInt(4) == 0) {
            return;
        }

        BlockPos surface = surfaceAt(level, center, x, z);
        if (surface == null) {
            return;
        }

        BlockState wall = random.nextBoolean() ? Blocks.COBBLESTONE_WALL.defaultBlockState() : Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState();
        level.setBlock(surface.above(), wall, 2);
        if (random.nextInt(5) == 0) {
            level.setBlock(surface.above(2), wall, 2);
        }
    }

    private static void placeTrees(ServerLevel level, BlockPos center, RandomSource random) {
        placeTree(level, center, -64, 18, 8, random);
        placeTree(level, center, -32, -42, 7, random);
        placeTree(level, center, 28, 42, 9, random);
        placeTree(level, center, 58, -12, 8, random);
        placeTree(level, center, 18, 18, 7, random);
        placeTree(level, center, -12, 52, 6, random);
    }

    private static void placeTree(ServerLevel level, BlockPos center, int x, int z, int height, RandomSource random) {
        BlockPos surface = surfaceAt(level, center, x, z);
        if (surface == null) {
            return;
        }

        BlockState log = random.nextBoolean() ? Blocks.OAK_LOG.defaultBlockState() : Blocks.SPRUCE_LOG.defaultBlockState();
        BlockState leaves = (random.nextBoolean() ? Blocks.OAK_LEAVES : Blocks.SPRUCE_LEAVES)
                .defaultBlockState()
                .setValue(LeavesBlock.PERSISTENT, true);

        for (int y = 1; y <= height; y++) {
            level.setBlock(surface.above(y), log, 2);
        }

        BlockPos leafCenter = surface.above(height);
        for (int dx = -4; dx <= 4; dx++) {
            for (int dy = -3; dy <= 3; dy++) {
                for (int dz = -4; dz <= 4; dz++) {
                    double shape = (dx * dx) / 14.0D + (dy * dy) / 8.0D + (dz * dz) / 14.0D;
                    if (shape > 1.0D || random.nextInt(18) == 0) {
                        continue;
                    }

                    BlockPos leafPos = leafCenter.offset(dx, dy, dz);
                    if (level.getBlockState(leafPos).isAir()) {
                        level.setBlock(leafPos, leaves, 2);
                    }
                }
            }
        }

        for (int root = 0; root < 4; root++) {
            int rx = x + (root == 0 ? 1 : root == 1 ? -1 : 0);
            int rz = z + (root == 2 ? 1 : root == 3 ? -1 : 0);
            BlockPos rootSurface = surfaceAt(level, center, rx, rz);
            if (rootSurface != null) {
                level.setBlock(rootSurface, Blocks.ROOTED_DIRT.defaultBlockState(), 2);
            }
        }
    }

    private static void placeLampPosts(ServerLevel level, BlockPos center) {
        int[][] posts = {
                {-26, 0},
                {26, 0},
                {0, -26},
                {0, 26},
                {-48, -2},
                {48, -2},
                {-4, 44},
                {4, -44}
        };

        for (int[] post : posts) {
            BlockPos surface = surfaceAt(level, center, post[0], post[1]);
            if (surface == null) {
                continue;
            }

            for (int y = 1; y <= 4; y++) {
                level.setBlock(surface.above(y), Blocks.OAK_FENCE.defaultBlockState(), 2);
            }
            level.setBlock(surface.above(5), Blocks.LANTERN.defaultBlockState(), 2);
        }
    }

    private static void placeVegetation(ServerLevel level, BlockPos center, RandomSource random) {
        for (int i = 0; i < 520; i++) {
            int x = random.nextInt(RADIUS_X * 2 + 1) - RADIUS_X;
            int z = random.nextInt(RADIUS_Z * 2 + 1) - RADIUS_Z;
            BlockPos surface = surfaceAt(level, center, x, z);
            if (surface == null || !level.getBlockState(surface.above()).isAir()) {
                continue;
            }

            BlockState ground = level.getBlockState(surface);
            if (!ground.is(Blocks.GRASS_BLOCK) && !ground.is(Blocks.MOSS_BLOCK) && !ground.is(Blocks.COARSE_DIRT)) {
                continue;
            }

            BlockState plant = switch (random.nextInt(8)) {
                case 0 -> Blocks.DANDELION.defaultBlockState();
                case 1 -> Blocks.POPPY.defaultBlockState();
                case 2 -> Blocks.FERN.defaultBlockState();
                case 3 -> Blocks.MOSS_CARPET.defaultBlockState();
                default -> Blocks.SHORT_GRASS.defaultBlockState();
            };
            level.setBlock(surface.above(), plant, 2);
        }
    }

    private static void placeDanglingRoots(ServerLevel level, BlockPos center, RandomSource random) {
        for (int i = 0; i < 180; i++) {
            int x = random.nextInt(RADIUS_X * 2 + 1) - RADIUS_X;
            int z = random.nextInt(RADIUS_Z * 2 + 1) - RADIUS_Z;
            double nx = x / (double) RADIUS_X;
            double nz = z / (double) RADIUS_Z;
            double distance = nx * nx + nz * nz;
            if (distance < 0.45D || distance > 1.08D) {
                continue;
            }

            BlockPos underside = undersideAt(level, center, x, z);
            if (underside == null) {
                continue;
            }

            int length = 3 + random.nextInt(8);
            for (int y = 1; y <= length; y++) {
                BlockPos pos = underside.below(y);
                if (!level.getBlockState(pos).isAir()) {
                    break;
                }
                level.setBlock(pos, random.nextInt(4) == 0 ? Blocks.MANGROVE_ROOTS.defaultBlockState() : Blocks.OAK_FENCE.defaultBlockState(), 2);
            }
        }
    }

    private static void setSurfaceBlock(ServerLevel level, BlockPos center, int x, int z, BlockState state) {
        BlockPos surface = surfaceAt(level, center, x, z);
        if (surface != null) {
            level.setBlock(surface, state, 2);
            level.setBlock(surface.above(), Blocks.AIR.defaultBlockState(), 2);
        }
    }

    private static BlockPos surfaceAt(ServerLevel level, BlockPos center, int x, int z) {
        int worldX = center.getX() + x;
        int worldZ = center.getZ() + z;
        for (int y = center.getY() + MAX_TOP_VARIATION + 4; y >= center.getY() - 8; y--) {
            BlockPos pos = new BlockPos(worldX, y, worldZ);
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && state.getFluidState().isEmpty()) {
                return pos;
            }
        }

        return null;
    }

    private static BlockPos undersideAt(ServerLevel level, BlockPos center, int x, int z) {
        int worldX = center.getX() + x;
        int worldZ = center.getZ() + z;
        for (int y = center.getY() - MAX_THICKNESS - 8; y <= center.getY() + MAX_TOP_VARIATION + 4; y++) {
            BlockPos pos = new BlockPos(worldX, y, worldZ);
            BlockState state = level.getBlockState(pos);
            if (!state.isAir() && state.getFluidState().isEmpty()) {
                return pos;
            }
        }

        return null;
    }

    private static int noise(long seed, int x, int z, int bound) {
        return (int) Math.floorMod(mix(seed, x, z), bound);
    }

    private static long mix(long seed, int x, int z) {
        long mixed = seed ^ ((long) x * 341873128712L) ^ ((long) z * 132897987541L) ^ 0x632be59bd9b4e019L;
        mixed = (mixed ^ (mixed >>> 30)) * 0xbf58476d1ce4e5b9L;
        mixed = (mixed ^ (mixed >>> 27)) * 0x94d049bb133111ebL;
        return mixed ^ (mixed >>> 31);
    }

    private static final class SkyIslandData extends SavedData {
        private static final String NAME = SimpleDungeons.MODID + "_spawn_sky_island";
        private boolean placed;
        private BlockPos center;

        private SkyIslandData() {
        }

        private SkyIslandData(CompoundTag tag) {
            placed = tag.getBoolean("placed");
            if (tag.contains("center")) {
                center = BlockPos.of(tag.getLong("center"));
            }
        }

        private static SavedData.Factory<SkyIslandData> factory() {
            return new SavedData.Factory<>(SkyIslandData::new, (tag, registries) -> new SkyIslandData(tag));
        }

        private static SkyIslandData get(ServerLevel level) {
            DimensionDataStorage storage = level.getDataStorage();
            return storage.computeIfAbsent(factory(), NAME);
        }

        private boolean isPlaced() {
            return placed;
        }

        private void markPlaced(BlockPos center) {
            this.placed = true;
            this.center = center.immutable();
            setDirty();
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            tag.putBoolean("placed", placed);
            if (center != null) {
                tag.putLong("center", center.asLong());
            }
            return tag;
        }
    }
}
