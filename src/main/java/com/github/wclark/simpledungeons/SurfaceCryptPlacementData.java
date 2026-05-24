package com.github.wclark.simpledungeons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

final class SurfaceCryptPlacementData extends SavedData {
    private static final String NAME = SimpleDungeons.MODID + "_surface_crypts";
    private final Set<Long> checkedCells = new HashSet<>();
    private final List<BlockPos> centers = new ArrayList<>();

    private SurfaceCryptPlacementData() {
    }

    private SurfaceCryptPlacementData(CompoundTag tag) {
        CompoundTag checkedTag = tag.getCompound("checkedCells");
        for (String key : checkedTag.getAllKeys()) {
            checkedCells.add(Long.parseLong(key));
        }

        CompoundTag centersTag = tag.getCompound("centers");
        for (String key : centersTag.getAllKeys()) {
            centers.add(BlockPos.of(centersTag.getLong(key)));
        }
    }

    private static SavedData.Factory<SurfaceCryptPlacementData> factory() {
        return new SavedData.Factory<>(SurfaceCryptPlacementData::new, (tag, registries) -> new SurfaceCryptPlacementData(tag));
    }

    static SurfaceCryptPlacementData get(ServerLevel level) {
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(factory(), NAME);
    }

    boolean isCellChecked(long cellKey) {
        return checkedCells.contains(cellKey);
    }

    void markCellChecked(long cellKey) {
        if (checkedCells.add(cellKey)) {
            setDirty();
        }
    }

    void addCenter(BlockPos center) {
        BlockPos immutableCenter = center.immutable();
        if (!centers.contains(immutableCenter)) {
            centers.add(immutableCenter);
            setDirty();
        }
    }

    boolean hasCenterNear(BlockPos center, int radius) {
        int radiusSqr = radius * radius;
        for (BlockPos existing : centers) {
            int dx = existing.getX() - center.getX();
            int dz = existing.getZ() - center.getZ();
            if (dx * dx + dz * dz <= radiusSqr) {
                return true;
            }
        }

        return false;
    }

    List<BlockPos> centers() {
        return Collections.unmodifiableList(centers);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag checkedTag = new CompoundTag();
        for (Long checkedCell : checkedCells) {
            checkedTag.putBoolean(Long.toString(checkedCell), true);
        }
        tag.put("checkedCells", checkedTag);

        CompoundTag centersTag = new CompoundTag();
        for (int i = 0; i < centers.size(); i++) {
            centersTag.putLong(Integer.toString(i), centers.get(i).asLong());
        }
        tag.put("centers", centersTag);
        return tag;
    }
}
