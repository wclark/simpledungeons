package com.github.wclark.simpledungeons;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

public class FactoryConveyorSystem {
    static final String CONVEYOR_TAG = "simpledungeons_factory_conveyor";
    private static final String RAW_TAG = "simpledungeons_factory_conveyor_raw";
    private static final String SMELTED_TAG = "simpledungeons_factory_conveyor_smelted";
    private static final double BELT_Y_OFFSET = 2.23D;
    private static final double BELT_SPEED = 0.065D;
    private static final int SPAWN_INTERVAL_TICKS = 36;
    private static final int MAX_CONVEYOR_ITEMS = 22;
    private static final Item[] RAW_ITEMS = {
            Items.RAW_IRON,
            Items.RAW_COPPER,
            Items.RAW_GOLD
    };

    @SubscribeEvent
    public void onLevelTick(LevelTickEvent.Post event) {
        if (!(event.getLevel() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
            return;
        }

        BlockPos center = SpawnSkyIslandStructure.currentCenter(level);
        if (center == null) {
            return;
        }

        List<ItemEntity> conveyorItems = conveyorItems(level, center);
        if (level.getGameTime() % SPAWN_INTERVAL_TICKS == 0 && conveyorItems.size() < MAX_CONVEYOR_ITEMS) {
            spawnRawItem(level, center);
        }

        for (ItemEntity item : conveyorItems) {
            tickConveyorItem(level, center, item);
        }
    }

    private static List<ItemEntity> conveyorItems(ServerLevel level, BlockPos center) {
        int floorY = center.getY() + 1;
        AABB area = new AABB(
                center.getX() + SpawnSkyIslandStructure.CONVEYOR_END_X - 12,
                floorY,
                center.getZ() + SpawnSkyIslandStructure.CONVEYOR_Z - 8,
                center.getX() + SpawnSkyIslandStructure.CONVEYOR_START_X + 12,
                floorY + 6,
                center.getZ() + SpawnSkyIslandStructure.CONVEYOR_Z + 8);
        return level.getEntitiesOfClass(ItemEntity.class, area, FactoryConveyorSystem::isConveyorItem);
    }

    static boolean isConveyorItem(ItemEntity item) {
        return item.getTags().contains(CONVEYOR_TAG);
    }

    private static void spawnRawItem(ServerLevel level, BlockPos center) {
        int pick = Math.floorMod((int) (level.getGameTime() / SPAWN_INTERVAL_TICKS), RAW_ITEMS.length);
        ItemStack stack = new ItemStack(RAW_ITEMS[pick]);
        double lane = switch ((int) (level.getGameTime() / SPAWN_INTERVAL_TICKS) % 3) {
            case 0 -> -0.42D;
            case 1 -> 0.42D;
            default -> 0.0D;
        };
        spawnConveyorItem(level, center, stack, SpawnSkyIslandStructure.CONVEYOR_START_X + 0.35D, lane, true);
    }

    private static void tickConveyorItem(ServerLevel level, BlockPos center, ItemEntity item) {
        prepareConveyorItem(item);
        double relativeX = item.getX() - center.getX();
        double lane = item.getZ() - center.getZ() - SpawnSkyIslandStructure.CONVEYOR_Z;
        if (relativeX < SpawnSkyIslandStructure.CONVEYOR_END_X - 12 || relativeX > SpawnSkyIslandStructure.CONVEYOR_START_X + 14) {
            item.discard();
            return;
        }

        if (item.getTags().contains(RAW_TAG) && relativeX <= SpawnSkyIslandStructure.CONVEYOR_FURNACE_INPUT_X) {
            ItemStack smelted = smelt(item.getItem());
            item.discard();
            if (!smelted.isEmpty()) {
                spawnConveyorItem(level, center, smelted, SpawnSkyIslandStructure.CONVEYOR_FURNACE_OUTPUT_X - 0.35D, lane, false);
            }
            return;
        }

        if (item.getTags().contains(SMELTED_TAG) && relativeX <= SpawnSkyIslandStructure.CONVEYOR_COLLECTOR_X) {
            item.discard();
            return;
        }

        double targetY = center.getY() + 1 + BELT_Y_OFFSET;
        double targetZ = center.getZ() + SpawnSkyIslandStructure.CONVEYOR_Z + Math.max(-0.48D, Math.min(0.48D, lane));
        item.setPos(item.getX() - BELT_SPEED, targetY, targetZ);
        item.setDeltaMovement(0.0D, 0.0D, 0.0D);
    }

    private static ItemStack smelt(ItemStack stack) {
        if (stack.is(Items.RAW_IRON)) {
            return new ItemStack(Items.IRON_INGOT);
        }

        if (stack.is(Items.RAW_COPPER)) {
            return new ItemStack(Items.COPPER_INGOT);
        }

        if (stack.is(Items.RAW_GOLD)) {
            return new ItemStack(Items.GOLD_INGOT);
        }

        return ItemStack.EMPTY;
    }

    private static void spawnConveyorItem(ServerLevel level, BlockPos center, ItemStack stack, double relativeX, double lane, boolean raw) {
        double x = center.getX() + relativeX;
        double y = center.getY() + 1 + BELT_Y_OFFSET;
        double z = center.getZ() + SpawnSkyIslandStructure.CONVEYOR_Z + lane;
        ItemEntity item = new ItemEntity(level, x, y, z, stack, 0.0D, 0.0D, 0.0D);
        item.addTag(CONVEYOR_TAG);
        item.addTag(raw ? RAW_TAG : SMELTED_TAG);
        prepareConveyorItem(item);
        level.addFreshEntity(item);
    }

    private static void prepareConveyorItem(ItemEntity item) {
        item.setNeverPickUp();
        item.setUnlimitedLifetime();
        item.setNoGravity(true);
        item.noPhysics = true;
    }
}
