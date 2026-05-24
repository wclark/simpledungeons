package com.github.wclark.simpledungeons;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class GraveyardUndeadSpawner {
    private static final int TRIGGER_RADIUS = 7;
    private static final int RESPAWN_DAYS = 3;
    private static final int EMERGE_TICKS = 42;
    private static final int MAX_SPAWNS_PER_SCAN = 2;
    private static final int[] GRAVE_XS = {-25, -20, -15, -10, 10, 15, 20, 25};
    private static final int[] GRAVE_ZS = {-14, -9, -4, 7, 12, 17};
    private final List<EmergingUndead> emergingUndead = new ArrayList<>();
    private int scanTimer;

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        tickEmergingUndead();

        scanTimer++;
        if (scanTimer < 20) {
            return;
        }
        scanTimer = 0;

        ServerLevel level = event.getServer().overworld();
        if (!Config.ENABLE_SURFACE_CRYPT.getAsBoolean() || level.dimension() != Level.OVERWORLD) {
            return;
        }

        GraveyardSpawnData data = GraveyardSpawnData.get(level);
        List<BlockPos> graveyardCenters = SurfaceCryptPlacementData.get(level).centers();
        if (graveyardCenters.isEmpty()) {
            return;
        }

        long currentDay = level.getDayTime() / 24000L;
        int spawned = 0;

        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            if (player.serverLevel() != level) {
                continue;
            }

            for (BlockPos centerGround : graveyardCenters) {
                if (player.blockPosition().distSqr(centerGround) > 96 * 96) {
                    continue;
                }

                for (int z : GRAVE_ZS) {
                    for (int x : GRAVE_XS) {
                        if (isInsideCrypt(x, z)) {
                            continue;
                        }

                        BlockPos graveGround = at(centerGround, x, z);
                        if (player.blockPosition().distSqr(graveGround) > TRIGGER_RADIUS * TRIGGER_RADIUS) {
                            continue;
                        }

                        int worldX = graveGround.getX();
                        int worldZ = graveGround.getZ();
                        long graveKey = graveKey(worldX, worldZ);
                        if (!isSpawnCapableGrave(level, centerGround, x, z)
                                || !isChosenGrave(level, worldX, worldZ)
                                || !data.canSpawn(graveKey, currentDay)) {
                            continue;
                        }

                        if (spawnUndead(level, graveGround.above(), level.random)) {
                            data.markSpawned(graveKey, currentDay);
                            spawned++;
                        }

                        if (spawned >= MAX_SPAWNS_PER_SCAN) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private boolean spawnUndead(ServerLevel level, BlockPos graveExit, RandomSource random) {
        Mob mob = random.nextBoolean()
                ? net.minecraft.world.entity.EntityType.ZOMBIE.create(level)
                : net.minecraft.world.entity.EntityType.SKELETON.create(level);
        if (mob == null) {
            return false;
        }

        double endY = graveExit.getY();
        double startY = endY - 1.35D;
        float rotation = random.nextFloat() * 360.0F;

        mob.moveTo(graveExit.getX() + 0.5D, startY, graveExit.getZ() + 0.5D, rotation, 0.0F);
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(graveExit), MobSpawnType.TRIGGERED, null);
        equipUndead(level, mob, random);
        mob.setNoAi(true);
        mob.setNoGravity(true);
        mob.setSilent(true);
        mob.setPersistenceRequired();

        level.addFreshEntity(mob);
        emergingUndeadEffect(level, graveExit, random);
        emergingUndead.add(new EmergingUndead(level, mob.getUUID(), startY, endY));
        return true;
    }

    private static void emergingUndeadEffect(ServerLevel level, BlockPos pos, RandomSource random) {
        level.sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COARSE_DIRT.defaultBlockState()),
                pos.getX() + 0.5D,
                pos.getY() + 0.15D,
                pos.getZ() + 0.5D,
                28,
                0.35D,
                0.12D,
                0.35D,
                0.06D);
        level.playSound(null, pos, SoundEvents.WARDEN_DIG, SoundSource.HOSTILE, 0.75F, 1.25F + random.nextFloat() * 0.25F);
    }

    private static void equipUndead(ServerLevel level, Mob mob, RandomSource random) {
        boolean skeleton = mob instanceof Skeleton;
        ItemStack weapon = skeleton ? new ItemStack(Items.BOW) : new ItemStack(randomSword(random));
        if (skeleton) {
            var enchantments = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            weapon.enchant(enchantments.getHolderOrThrow(Enchantments.POWER), 1);
        }

        damageAroundHalf(weapon, random);
        mob.setItemSlot(EquipmentSlot.MAINHAND, weapon);
        mob.setDropChance(EquipmentSlot.MAINHAND, 0.1F);

        mob.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
        mob.setItemSlot(EquipmentSlot.CHEST, ItemStack.EMPTY);
        mob.setItemSlot(EquipmentSlot.LEGS, ItemStack.EMPTY);
        mob.setItemSlot(EquipmentSlot.FEET, ItemStack.EMPTY);
        equipArmor(mob, EquipmentSlot.HEAD, randomArmor(EquipmentSlot.HEAD, random), random);

        boolean chest = false;
        boolean legs = false;
        boolean feet = false;
        int extraPieces = 1 + random.nextInt(2);
        int equipped = 0;
        while (equipped < extraPieces) {
            EquipmentSlot slot = switch (random.nextInt(3)) {
                case 0 -> EquipmentSlot.CHEST;
                case 1 -> EquipmentSlot.LEGS;
                default -> EquipmentSlot.FEET;
            };

            if ((slot == EquipmentSlot.CHEST && chest)
                    || (slot == EquipmentSlot.LEGS && legs)
                    || (slot == EquipmentSlot.FEET && feet)) {
                continue;
            }

            equipArmor(mob, slot, randomArmor(slot, random), random);
            chest |= slot == EquipmentSlot.CHEST;
            legs |= slot == EquipmentSlot.LEGS;
            feet |= slot == EquipmentSlot.FEET;
            equipped++;
        }
    }

    private static void equipArmor(Mob mob, EquipmentSlot slot, ItemStack stack, RandomSource random) {
        damageAroundHalf(stack, random);
        mob.setItemSlot(slot, stack);
        mob.setDropChance(slot, 0.08F);
    }

    private static Item randomSword(RandomSource random) {
        return switch (random.nextInt(3)) {
            case 0 -> Items.WOODEN_SWORD;
            case 1 -> Items.STONE_SWORD;
            default -> Items.IRON_SWORD;
        };
    }

    private static ItemStack randomArmor(EquipmentSlot slot, RandomSource random) {
        int tier = random.nextInt(4);
        return switch (slot) {
            case HEAD -> new ItemStack(switch (tier) {
                case 0 -> Items.LEATHER_HELMET;
                case 1 -> Items.CHAINMAIL_HELMET;
                case 2 -> Items.GOLDEN_HELMET;
                default -> Items.IRON_HELMET;
            });
            case CHEST -> new ItemStack(switch (tier) {
                case 0 -> Items.LEATHER_CHESTPLATE;
                case 1 -> Items.CHAINMAIL_CHESTPLATE;
                case 2 -> Items.GOLDEN_CHESTPLATE;
                default -> Items.IRON_CHESTPLATE;
            });
            case LEGS -> new ItemStack(switch (tier) {
                case 0 -> Items.LEATHER_LEGGINGS;
                case 1 -> Items.CHAINMAIL_LEGGINGS;
                case 2 -> Items.GOLDEN_LEGGINGS;
                default -> Items.IRON_LEGGINGS;
            });
            case FEET -> new ItemStack(switch (tier) {
                case 0 -> Items.LEATHER_BOOTS;
                case 1 -> Items.CHAINMAIL_BOOTS;
                case 2 -> Items.GOLDEN_BOOTS;
                default -> Items.IRON_BOOTS;
            });
            default -> ItemStack.EMPTY;
        };
    }

    private static void damageAroundHalf(ItemStack stack, RandomSource random) {
        if (!stack.isDamageableItem()) {
            return;
        }

        int maxDamage = stack.getMaxDamage();
        int variance = Math.max(1, maxDamage / 8);
        int damage = maxDamage / 2 - variance / 2 + random.nextInt(variance + 1);
        stack.setDamageValue(Math.max(1, Math.min(maxDamage - 1, damage)));
    }

    private void tickEmergingUndead() {
        Iterator<EmergingUndead> iterator = emergingUndead.iterator();
        while (iterator.hasNext()) {
            EmergingUndead emerging = iterator.next();
            if (emerging.tick()) {
                iterator.remove();
            }
        }
    }

    private static boolean isSpawnCapableGrave(ServerLevel level, BlockPos centerGround, int x, int z) {
        BlockPos head = at(centerGround, x, z - 1);
        BlockPos body = at(centerGround, x, z);
        BlockState bodyState = level.getBlockState(body);
        return !level.getBlockState(head.above()).isAir()
                && (bodyState.is(Blocks.COARSE_DIRT) || bodyState.is(Blocks.PODZOL) || bodyState.is(Blocks.ROOTED_DIRT));
    }

    private static boolean isChosenGrave(ServerLevel level, int worldX, int worldZ) {
        long mixed = level.getSeed()
                ^ ((long) worldX * 341873128712L)
                ^ ((long) worldZ * 132897987541L);
        mixed ^= mixed >>> 33;
        mixed *= 0xff51afd7ed558ccdL;
        mixed ^= mixed >>> 33;
        return Math.floorMod(mixed, 100L) < 45L;
    }

    private static boolean isInsideCrypt(int x, int z) {
        return x >= -9 && x <= 9 && z >= -21 && z <= -8;
    }

    private static long graveKey(int worldX, int worldZ) {
        return ((long) worldX << 32) ^ (worldZ & 0xffffffffL);
    }

    private static BlockPos at(BlockPos centerGround, int x, int z) {
        return new BlockPos(centerGround.getX() + x, centerGround.getY(), centerGround.getZ() + z);
    }

    private static final class EmergingUndead {
        private final ServerLevel level;
        private final UUID mobId;
        private final double startY;
        private final double endY;
        private int age;

        private EmergingUndead(ServerLevel level, UUID mobId, double startY, double endY) {
            this.level = level;
            this.mobId = mobId;
            this.startY = startY;
            this.endY = endY;
        }

        private boolean tick() {
            Entity entity = level.getEntity(mobId);
            if (!(entity instanceof Mob mob) || !mob.isAlive()) {
                return true;
            }

            age++;
            double progress = Math.min(1.0D, age / (double) EMERGE_TICKS);
            mob.setDeltaMovement(0.0D, 0.0D, 0.0D);
            mob.setPos(mob.getX(), startY + (endY - startY) * progress, mob.getZ());

            if (age >= EMERGE_TICKS) {
                mob.setNoAi(false);
                mob.setNoGravity(false);
                mob.setSilent(false);
                return true;
            }

            return false;
        }
    }

    private static final class GraveyardSpawnData extends SavedData {
        private static final String NAME = SimpleDungeons.MODID + "_grave_spawns";
        private final Map<Long, Long> lastSpawnDays = new HashMap<>();

        private GraveyardSpawnData() {
        }

        private GraveyardSpawnData(CompoundTag tag) {
            CompoundTag graves = tag.getCompound("graves");
            for (String key : graves.getAllKeys()) {
                lastSpawnDays.put(Long.parseLong(key), graves.getLong(key));
            }
        }

        private static SavedData.Factory<GraveyardSpawnData> factory() {
            return new SavedData.Factory<>(GraveyardSpawnData::new, (tag, registries) -> new GraveyardSpawnData(tag));
        }

        private static GraveyardSpawnData get(ServerLevel level) {
            DimensionDataStorage storage = level.getDataStorage();
            return storage.computeIfAbsent(factory(), NAME);
        }

        private boolean canSpawn(long graveKey, long currentDay) {
            Long lastSpawnDay = lastSpawnDays.get(graveKey);
            return lastSpawnDay == null || currentDay - lastSpawnDay >= RESPAWN_DAYS;
        }

        private void markSpawned(long graveKey, long currentDay) {
            lastSpawnDays.put(graveKey, currentDay);
            setDirty();
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
            CompoundTag graves = new CompoundTag();
            for (Map.Entry<Long, Long> entry : lastSpawnDays.entrySet()) {
                graves.putLong(Long.toString(entry.getKey()), entry.getValue());
            }
            tag.put("graves", graves);
            return tag;
        }
    }
}
