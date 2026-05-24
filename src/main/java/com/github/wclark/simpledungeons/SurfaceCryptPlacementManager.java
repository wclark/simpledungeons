package com.github.wclark.simpledungeons;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class SurfaceCryptPlacementManager {
    private static final int SCAN_INTERVAL_TICKS = 80;
    private static final int PLAYER_SCAN_RADIUS_CHUNKS = 14;
    private static final int MIN_DISTANCE_FROM_SPAWN = 256;
    private int scanTimer;

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        scanTimer++;
        if (scanTimer < SCAN_INTERVAL_TICKS) {
            return;
        }
        scanTimer = 0;

        ServerLevel level = event.getServer().overworld();
        if (!Config.ENABLE_SURFACE_CRYPT.getAsBoolean() || level.dimension() != Level.OVERWORLD) {
            return;
        }

        SurfaceCryptPlacementData data = SurfaceCryptPlacementData.get(level);
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (player.serverLevel() == level && scanAroundPlayer(level, player, data)) {
                return;
            }
        }
    }

    private static boolean scanAroundPlayer(ServerLevel level, ServerPlayer player, SurfaceCryptPlacementData data) {
        ChunkPos playerChunk = player.chunkPosition();
        for (int dz = -PLAYER_SCAN_RADIUS_CHUNKS; dz <= PLAYER_SCAN_RADIUS_CHUNKS; dz++) {
            for (int dx = -PLAYER_SCAN_RADIUS_CHUNKS; dx <= PLAYER_SCAN_RADIUS_CHUNKS; dx++) {
                int chunkX = playerChunk.x + dx;
                int chunkZ = playerChunk.z + dz;
                if (tryPlaceCandidate(level, chunkX, chunkZ, data)) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean tryPlaceCandidate(ServerLevel level, int chunkX, int chunkZ, SurfaceCryptPlacementData data) {
        int spacing = Config.SURFACE_CRYPT_AVERAGE_SPACING.get();
        int cellX = Math.floorDiv(chunkX, spacing);
        int cellZ = Math.floorDiv(chunkZ, spacing);
        long cellKey = cellKey(cellX, cellZ);
        if (data.isCellChecked(cellKey) || !isCandidateChunk(level, chunkX, chunkZ, cellX, cellZ, spacing)) {
            return false;
        }

        data.markCellChecked(cellKey);
        int centerX = chunkX * 16 + 8;
        int centerZ = chunkZ * 16 + 8;
        if (isTooCloseToSpawn(level, centerX, centerZ)) {
            return false;
        }

        BlockPos centerGround = SpawnGraveyardStructure.groundAt(level, centerX, centerZ);
        if (data.hasCenterNear(centerGround, spacing * 8) || !SpawnGraveyardStructure.canPlaceAt(level, centerGround)) {
            return false;
        }

        RandomSource random = RandomSource.create(mix(level.getSeed(), chunkX, chunkZ));
        SpawnGraveyardStructure.placeAt(level, centerGround, random);
        data.addCenter(centerGround);
        SimpleDungeons.LOGGER.info("Simple Dungeons placed a surface crypt graveyard at {} {} {}.", centerGround.getX(), centerGround.getY(), centerGround.getZ());
        return true;
    }

    private static boolean isCandidateChunk(ServerLevel level, int chunkX, int chunkZ, int cellX, int cellZ, int spacing) {
        long mixed = mix(level.getSeed(), cellX, cellZ);
        int candidateChunkX = cellX * spacing + (int) Math.floorMod(mixed, spacing);
        int candidateChunkZ = cellZ * spacing + (int) Math.floorMod(mix(mixed, cellZ, cellX), spacing);
        return chunkX == candidateChunkX && chunkZ == candidateChunkZ;
    }

    private static boolean isTooCloseToSpawn(ServerLevel level, int centerX, int centerZ) {
        BlockPos spawn = level.getSharedSpawnPos();
        long dx = centerX - spawn.getX();
        long dz = centerZ - spawn.getZ();
        return dx * dx + dz * dz < (long) MIN_DISTANCE_FROM_SPAWN * MIN_DISTANCE_FROM_SPAWN;
    }

    private static long cellKey(int cellX, int cellZ) {
        return ((long) cellX << 32) ^ (cellZ & 0xffffffffL);
    }

    private static long mix(long seed, int x, int z) {
        long mixed = seed ^ ((long) x * 341873128712L) ^ ((long) z * 132897987541L) ^ 0x9e3779b97f4a7c15L;
        mixed = (mixed ^ (mixed >>> 30)) * 0xbf58476d1ce4e5b9L;
        mixed = (mixed ^ (mixed >>> 27)) * 0x94d049bb133111ebL;
        return mixed ^ (mixed >>> 31);
    }
}
