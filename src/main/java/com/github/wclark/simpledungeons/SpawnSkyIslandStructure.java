package com.github.wclark.simpledungeons;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CopperBulbBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.AABB;

public final class SpawnSkyIslandStructure {
    private static final int GENERATION_VERSION = 10;
    private static final int SURFACE_Y = 198;
    private static final int RADIUS_X = 86;
    private static final int RADIUS_Z = 64;
    private static final int MAX_TOP_VARIATION = 3;
    private static final int MAX_THICKNESS = 36;

    private SpawnSkyIslandStructure() {
    }

    public static boolean ensureAtSpawn(ServerLevel level) {
        if (!Config.ENABLE_SKY_ISLAND_DUNGEON.getAsBoolean() || level.dimension() != Level.OVERWORLD) {
            return false;
        }

        SkyIslandData data = SkyIslandData.get(level);
        if (data.isCurrent()) {
            return false;
        }

        BlockPos spawn = level.getSharedSpawnPos();
        int islandY = Math.min(level.getMaxBuildHeight() - 84, Math.max(150, SURFACE_Y));
        BlockPos center = new BlockPos(spawn.getX(), islandY, spawn.getZ());
        RandomSource random = RandomSource.create(level.getSeed() ^ 0x51a7d15L);
        if (data.hasPlacedIsland()) {
            clearIslandVolume(level, center);
        }
        build(level, center, random);
        data.markPlaced(center);
        return true;
    }

    private static void build(ServerLevel level, BlockPos center, RandomSource random) {
        clearFactoryRobots(level, center);
        buildIslandTerrain(level, center, level.getSeed());
        placeFactory(level, center, random);
        placeTrees(level, center, random);
        placeVegetation(level, center, random);
    }

    private static void clearIslandVolume(ServerLevel level, BlockPos center) {
        int minY = center.getY() - MAX_THICKNESS - 24;
        int maxY = center.getY() + 48;
        for (int x = -RADIUS_X - 8; x <= RADIUS_X + 8; x++) {
            for (int z = -RADIUS_Z - 8; z <= RADIUS_Z + 8; z++) {
                double nx = x / (double) (RADIUS_X + 8);
                double nz = z / (double) (RADIUS_Z + 8);
                if (nx * nx + nz * nz > 1.18D) {
                    continue;
                }

                for (int y = minY; y <= maxY; y++) {
                    BlockPos pos = new BlockPos(center.getX() + x, y, center.getZ() + z);
                    if (!level.getBlockState(pos).isAir()) {
                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
    }

    private static void clearFactoryRobots(ServerLevel level, BlockPos center) {
        AABB area = new AABB(
                center.getX() - RADIUS_X - 16,
                center.getY() - MAX_THICKNESS - 32,
                center.getZ() - RADIUS_Z - 16,
                center.getX() + RADIUS_X + 16,
                center.getY() + 80,
                center.getZ() + RADIUS_Z + 16);
        for (FactoryRobotEntity robot : level.getEntitiesOfClass(FactoryRobotEntity.class, area)) {
            robot.discard();
        }
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

                double radial = Math.sqrt(distance);
                double centerWeight = Math.max(0.0D, 1.0D - radial);
                int topY = topY(center, radial, seed, x, z);
                int thickness = 10 + (int) Math.round(centerWeight * MAX_THICKNESS) + noise(seed, x * 3, z * 3, 4);
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

    private static int topY(BlockPos center, double radial, long seed, int x, int z) {
        if (radial < 0.9D) {
            return center.getY();
        }

        if (radial < 0.97D) {
            return center.getY() - 1;
        }

        int edgeDrop = 2 + (int) Math.round((radial - 0.97D) * 20.0D);
        return center.getY() - edgeDrop;
    }

    private static BlockState topBlock(long seed, int x, int z) {
        int pick = noise(seed ^ 0x71b5L, x, z, 14);
        if (pick == 0) {
            return Blocks.MOSS_BLOCK.defaultBlockState();
        }

        return Blocks.GRASS_BLOCK.defaultBlockState();
    }

    private static BlockState dirtBlock(long seed, int x, int z) {
        int pick = noise(seed ^ 0x9ad31L, x, z, 7);
        if (pick == 0) {
            return Blocks.ROOTED_DIRT.defaultBlockState();
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

    private static void placeFactory(ServerLevel level, BlockPos center, RandomSource random) {
        int floorY = center.getY() + 1;
        clearFactorySite(level, center, floorY);
        placeFactoryFloor(level, center, floorY, random);
        placeFactoryWalls(level, center, floorY);
        placeFactoryRoof(level, center, floorY);
        placeFactoryStacks(level, center, floorY);
        placeFactoryDetails(level, center, floorY);
    }

    private static void clearFactorySite(ServerLevel level, BlockPos center, int floorY) {
        for (int x = -66; x <= 66; x++) {
            for (int z = -34; z <= 34; z++) {
                for (int y = center.getY(); y <= center.getY() + 36; y++) {
                    BlockPos pos = new BlockPos(center.getX() + x, y, center.getZ() + z);
                    level.setBlock(pos, y == center.getY() ? Blocks.STONE_BRICKS.defaultBlockState() : Blocks.AIR.defaultBlockState(), 2);
                }

                level.setBlock(new BlockPos(center.getX() + x, floorY, center.getZ() + z), Blocks.POLISHED_ANDESITE.defaultBlockState(), 2);
            }
        }
    }

    private static void placeFactoryFloor(ServerLevel level, BlockPos center, int floorY, RandomSource random) {
        for (int x = -58; x <= 58; x++) {
            for (int z = -26; z <= 26; z++) {
                BlockState floor = random.nextInt(8) == 0 ? Blocks.SMOOTH_STONE.defaultBlockState() : Blocks.POLISHED_ANDESITE.defaultBlockState();
                level.setBlock(new BlockPos(center.getX() + x, floorY, center.getZ() + z), floor, 2);
            }
        }
    }

    private static void placeFactoryWalls(ServerLevel level, BlockPos center, int floorY) {
        int minX = -58;
        int maxX = 58;
        int minZ = -26;
        int maxZ = 26;
        int wallBottom = floorY + 1;
        int wallTop = floorY + 14;

        for (int x = minX; x <= maxX; x++) {
            for (int y = wallBottom; y <= wallTop; y++) {
                placeFactoryWallBlock(level, center, x, y, minZ);
                placeFactoryWallBlock(level, center, x, y, maxZ);
            }
        }

        for (int z = minZ; z <= maxZ; z++) {
            for (int y = wallBottom; y <= wallTop; y++) {
                placeFactoryWallBlock(level, center, minX, y, z);
                placeFactoryWallBlock(level, center, maxX, y, z);
            }
        }

        for (int x = -50; x <= 50; x += 10) {
            placeTallWindow(level, center, x, maxZ, true, floorY + 4);
            placeTallWindow(level, center, x, minZ, true, floorY + 4);
        }

        for (int z = -18; z <= 18; z += 12) {
            placeTallWindow(level, center, minX, z, false, floorY + 4);
            placeTallWindow(level, center, maxX, z, false, floorY + 4);
        }

        placeFactoryEntrance(level, center, floorY, maxZ);
    }

    private static void placeFactoryEntrance(ServerLevel level, BlockPos center, int floorY, int z) {
        int bottomY = floorY + 1;
        for (int x = -6; x <= 6; x++) {
            for (int y = bottomY; y <= bottomY + 8; y++) {
                boolean frame = Math.abs(x) == 6 || y == bottomY + 8;
                BlockState state = frame ? Blocks.DEEPSLATE_BRICKS.defaultBlockState() : Blocks.AIR.defaultBlockState();
                level.setBlock(new BlockPos(center.getX() + x, y, center.getZ() + z), state, 2);
            }
        }

        for (int x = -5; x <= 5; x++) {
            level.setBlock(new BlockPos(center.getX() + x, bottomY + 5, center.getZ() + z), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
        }

        for (int y = bottomY + 6; y <= bottomY + 7; y++) {
            for (int x = -5; x <= 5; x++) {
                BlockState shutter = Math.floorMod(x + y, 3) == 0
                        ? Blocks.WAXED_COPPER_GRATE.defaultBlockState()
                        : Blocks.WAXED_CUT_COPPER.defaultBlockState();
                level.setBlock(new BlockPos(center.getX() + x, y, center.getZ() + z), shutter, 2);
            }
        }

        for (int x = -6; x <= 6; x++) {
            level.setBlock(new BlockPos(center.getX() + x, floorY, center.getZ() + z + 1), Blocks.POLISHED_ANDESITE.defaultBlockState(), 2);
        }
    }

    private static void placeFactoryWallBlock(ServerLevel level, BlockPos center, int x, int y, int z) {
        boolean pillar = Math.floorMod(x, 10) == 0 || Math.floorMod(z, 10) == 0;
        BlockState state = pillar ? Blocks.DEEPSLATE_BRICKS.defaultBlockState() : Blocks.BRICKS.defaultBlockState();
        level.setBlock(new BlockPos(center.getX() + x, y, center.getZ() + z), state, 2);
    }

    private static void placeTallWindow(ServerLevel level, BlockPos center, int x, int z, boolean wideAlongX, int bottomY) {
        for (int a = -2; a <= 2; a++) {
            for (int y = bottomY; y <= bottomY + 5; y++) {
                int px = wideAlongX ? x + a : x;
                int pz = wideAlongX ? z : z + a;
                level.setBlock(new BlockPos(center.getX() + px, y, center.getZ() + pz), Blocks.LIGHT_BLUE_STAINED_GLASS.defaultBlockState(), 2);
            }
        }
    }

    private static void placeFactoryRoof(ServerLevel level, BlockPos center, int floorY) {
        int roofBase = floorY + 15;
        for (int x = -61; x <= 61; x++) {
            for (int z = -30; z <= 30; z++) {
                int roofY = roofBase + Math.max(0, 27 - Math.abs(z)) / 5;
                BlockState roof = Math.floorMod(x + z, 9) == 0
                        ? Blocks.WEATHERED_CUT_COPPER.defaultBlockState()
                        : Blocks.OXIDIZED_CUT_COPPER.defaultBlockState();
                level.setBlock(new BlockPos(center.getX() + x, roofY, center.getZ() + z), roof, 2);
            }
        }

        for (int x = -62; x <= 62; x++) {
            level.setBlock(new BlockPos(center.getX() + x, roofBase + 5, center.getZ()), Blocks.OXIDIZED_CHISELED_COPPER.defaultBlockState(), 2);
        }

        for (int x : new int[]{-58, 58}) {
            for (int z = -27; z <= 27; z++) {
                int roofY = roofBase + Math.max(0, 27 - Math.abs(z)) / 5;
                for (int y = floorY + 15; y < roofY; y++) {
                    level.setBlock(new BlockPos(center.getX() + x, y, center.getZ() + z), Blocks.BRICKS.defaultBlockState(), 2);
                }
            }
        }
    }

    private static void placeFactoryStacks(ServerLevel level, BlockPos center, int floorY) {
        placeSmokestack(level, center, -48, -18, floorY + 1, 40);
        placeSmokestack(level, center, -36, -18, floorY + 1, 44);
        placeSmokestack(level, center, -24, -18, floorY + 1, 38);
    }

    private static void placeSmokestack(ServerLevel level, BlockPos center, int x, int z, int baseY, int height) {
        for (int y = baseY; y <= baseY + height; y++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dz = -2; dz <= 2; dz++) {
                    boolean wall = Math.abs(dx) == 2 || Math.abs(dz) == 2;
                    if (!wall) {
                        level.setBlock(new BlockPos(center.getX() + x + dx, y, center.getZ() + z + dz), Blocks.AIR.defaultBlockState(), 2);
                        continue;
                    }

                    BlockState stack = y % 5 == 0 ? Blocks.DEEPSLATE_BRICKS.defaultBlockState() : Blocks.BRICKS.defaultBlockState();
                    level.setBlock(new BlockPos(center.getX() + x + dx, y, center.getZ() + z + dz), stack, 2);
                }
            }
        }

        int topY = baseY + height + 1;
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                if (Math.abs(dx) == 3 || Math.abs(dz) == 3) {
                    level.setBlock(new BlockPos(center.getX() + x + dx, topY, center.getZ() + z + dz), Blocks.DEEPSLATE_BRICKS.defaultBlockState(), 2);
                }
            }
        }
        level.setBlock(new BlockPos(center.getX() + x, topY, center.getZ() + z), Blocks.CAMPFIRE.defaultBlockState(), 2);
    }

    private static void placeFactoryDetails(ServerLevel level, BlockPos center, int floorY) {
        for (int x = -54; x <= 54; x += 12) {
            level.setBlock(new BlockPos(center.getX() + x, floorY + 1, center.getZ() + 24), Blocks.LANTERN.defaultBlockState(), 2);
            level.setBlock(new BlockPos(center.getX() + x, floorY + 1, center.getZ() - 24), Blocks.LANTERN.defaultBlockState(), 2);
        }

        for (int z = -18; z <= 18; z += 12) {
            level.setBlock(new BlockPos(center.getX() - 56, floorY + 1, center.getZ() + z), Blocks.LANTERN.defaultBlockState(), 2);
            level.setBlock(new BlockPos(center.getX() + 56, floorY + 1, center.getZ() + z), Blocks.LANTERN.defaultBlockState(), 2);
        }

        int beamY = floorY + 14;
        for (int z : new int[]{-6, 6, 18}) {
            for (int x = -56; x <= 56; x++) {
                level.setBlock(new BlockPos(center.getX() + x, beamY, center.getZ() + z), Blocks.OXIDIZED_CUT_COPPER.defaultBlockState(), 2);
            }

            for (int x = -50; x <= 50; x += 10) {
                placeCopperBulbFixture(level, center, x, z, floorY + 11);
            }
        }

        placeFloorLightGrid(level, center, floorY);
    }

    private static boolean isInsideSmokestackFootprint(int x, int z) {
        for (int stackX : new int[]{-48, -36, -24}) {
            if (Math.abs(x - stackX) <= 4 && Math.abs(z + 18) <= 4) {
                return true;
            }
        }

        return false;
    }

    private static void placeCopperBulbFixture(ServerLevel level, BlockPos center, int x, int z, int bulbY) {
        level.setBlock(new BlockPos(center.getX() + x, bulbY + 2, center.getZ() + z), Blocks.CHAIN.defaultBlockState(), 2);
        level.setBlock(new BlockPos(center.getX() + x, bulbY + 1, center.getZ() + z), Blocks.CHAIN.defaultBlockState(), 2);
        level.setBlock(new BlockPos(center.getX() + x, bulbY, center.getZ() + z), litCopperBulb(), 2);
    }

    private static void placeFloorLightGrid(ServerLevel level, BlockPos center, int floorY) {
        for (int x = -50; x <= 50; x += 10) {
            for (int z = -20; z <= 20; z += 10) {
                if (isInsideSmokestackFootprint(x, z)) {
                    continue;
                }

                level.setBlock(new BlockPos(center.getX() + x, floorY, center.getZ() + z), litCopperBulb(), 2);
            }
        }
    }

    private static BlockState litCopperBulb() {
        return Blocks.WAXED_COPPER_BULB.defaultBlockState()
                .setValue(CopperBulbBlock.LIT, Boolean.TRUE)
                .setValue(CopperBulbBlock.POWERED, Boolean.FALSE);
    }

    private static void placeTrees(ServerLevel level, BlockPos center, RandomSource random) {
        placeTreeWithRobot(level, center, -72, 34, 8, random);
        placeTreeWithRobot(level, center, -56, -48, 7, random);
        placeTreeWithRobot(level, center, 60, 42, 9, random);
        placeTreeWithRobot(level, center, 72, -28, 8, random);
        placeTreeWithRobot(level, center, -74, -10, 7, random);
        placeTreeWithRobot(level, center, 14, 54, 6, random);
    }

    private static void placeTreeWithRobot(ServerLevel level, BlockPos center, int x, int z, int height, RandomSource random) {
        BlockPos treeSurface = placeTree(level, center, x, z, height, random);
        if (treeSurface != null) {
            placeFactoryRobotNearTree(level, center, x, z, random);
        }
    }

    private static BlockPos placeTree(ServerLevel level, BlockPos center, int x, int z, int height, RandomSource random) {
        BlockPos surface = surfaceAt(level, center, x, z);
        if (surface == null) {
            return null;
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

        return surface;
    }

    private static void placeFactoryRobotNearTree(ServerLevel level, BlockPos center, int treeX, int treeZ, RandomSource random) {
        int[][] offsets = {
                {3, 0},
                {-3, 0},
                {0, 3},
                {0, -3},
                {3, 3},
                {-3, 3},
                {3, -3},
                {-3, -3}
        };
        int start = random.nextInt(offsets.length);
        for (int i = 0; i < offsets.length; i++) {
            int[] offset = offsets[(start + i) % offsets.length];
            int robotX = treeX + offset[0];
            int robotZ = treeZ + offset[1];
            BlockPos surface = surfaceAt(level, center, robotX, robotZ);
            if (surface == null || !hasRobotSpace(level, surface)) {
                continue;
            }

            FactoryRobotEntity robot = ModEntities.FACTORY_ROBOT.get().create(level);
            if (robot == null) {
                return;
            }

            double x = surface.getX() + 0.5D;
            double y = surface.getY() + 1.0D;
            double z = surface.getZ() + 0.5D;
            robot.moveTo(x, y, z, random.nextFloat() * 360.0F, 0.0F);
            robot.setNoAi(true);
            robot.setPersistenceRequired();
            level.addFreshEntity(robot);
            return;
        }
    }

    private static boolean hasRobotSpace(ServerLevel level, BlockPos surface) {
        return level.getBlockState(surface.above()).isAir()
                && level.getBlockState(surface.above(2)).isAir();
    }

    private static void placeVegetation(ServerLevel level, BlockPos center, RandomSource random) {
        for (int i = 0; i < 180; i++) {
            int x = random.nextInt(RADIUS_X * 2 + 1) - RADIUS_X;
            int z = random.nextInt(RADIUS_Z * 2 + 1) - RADIUS_Z;
            BlockPos surface = surfaceAt(level, center, x, z);
            if (surface == null || !level.getBlockState(surface.above()).isAir()) {
                continue;
            }

            BlockState ground = level.getBlockState(surface);
            if (!ground.is(Blocks.GRASS_BLOCK) && !ground.is(Blocks.MOSS_BLOCK)) {
                continue;
            }

            BlockState plant = switch (random.nextInt(6)) {
                case 0 -> Blocks.DANDELION.defaultBlockState();
                case 1 -> Blocks.POPPY.defaultBlockState();
                case 2 -> Blocks.BLUE_ORCHID.defaultBlockState();
                case 3 -> Blocks.AZURE_BLUET.defaultBlockState();
                case 4 -> Blocks.OXEYE_DAISY.defaultBlockState();
                default -> Blocks.CORNFLOWER.defaultBlockState();
            };
            level.setBlock(surface.above(), plant, 2);
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
        private int version;
        private BlockPos center;

        private SkyIslandData() {
        }

        private SkyIslandData(CompoundTag tag) {
            placed = tag.getBoolean("placed");
            version = tag.getInt("version");
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

        private boolean isCurrent() {
            return placed && version >= GENERATION_VERSION;
        }

        private boolean hasPlacedIsland() {
            return placed;
        }

        private void markPlaced(BlockPos center) {
            this.placed = true;
            this.version = GENERATION_VERSION;
            this.center = center.immutable();
            setDirty();
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            tag.putBoolean("placed", placed);
            tag.putInt("version", version);
            if (center != null) {
                tag.putLong("center", center.asLong());
            }
            return tag;
        }
    }
}
