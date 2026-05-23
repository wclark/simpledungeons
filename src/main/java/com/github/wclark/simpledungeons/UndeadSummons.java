package com.github.wclark.simpledungeons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public class UndeadSummons {
    private static final int EMERGE_TICKS = 42;
    private static final int PET_SCAN_TICKS = 20;
    private static final int PET_COMMAND_TICKS = 200;
    private static final int PET_SEARCH_RADIUS = 32;
    private static final String PLAYER_SUMMON_TAG = SimpleDungeons.MODID + "_player_summon";
    private static final String SUNPROOF_TAG = SimpleDungeons.MODID + "_sunproof";
    private static final String OWNER_UUID_TAG = SimpleDungeons.MODID + "_owner";
    private static final List<EmergingUndead> EMERGING_UNDEAD = new ArrayList<>();
    private static final Set<Mob> PET_GOALS_INSTALLED = Collections.newSetFromMap(new WeakHashMap<>());
    private static final Map<UUID, OrderedTarget> ORDERED_TARGETS = new HashMap<>();
    private int petScanTimer;

    public static boolean spawnNecromancerUndead(ServerLevel level, BlockPos preferredPos, boolean skeleton, RandomSource random) {
        Mob mob = createUndead(level, skeleton);
        if (mob == null) {
            return false;
        }

        return spawnEmerging(level, findEmergenceSpot(level, preferredPos), mob, MobSpawnType.MOB_SUMMONED, random, spawned -> {
            equipNecromancerSummon(level, spawned, random);
            spawned.setPersistenceRequired();
        });
    }

    public static boolean spawnPlayerPet(ServerLevel level, BlockPos preferredPos, boolean skeleton, ServerPlayer owner) {
        Mob mob = createUndead(level, skeleton);
        if (mob == null) {
            return false;
        }

        return spawnEmerging(level, findEmergenceSpot(level, preferredPos), mob, MobSpawnType.MOB_SUMMONED, level.random, spawned -> {
            markPlayerPet(spawned, owner);
            installPetGoals(spawned);
            equipPetSunproofHelmet(spawned);
            spawned.setPersistenceRequired();
        });
    }

    public static BlockPos findEmergenceSpot(ServerLevel level, BlockPos preferredPos) {
        for (int radius = 0; radius <= 3; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.abs(dx) != radius && Math.abs(dz) != radius) {
                        continue;
                    }

                    BlockPos column = preferredPos.offset(dx, 0, dz);
                    BlockPos spot = findEmergenceSpotInColumn(level, column);
                    if (spot != null) {
                        return spot;
                    }
                }
            }
        }

        return preferredPos;
    }

    public static boolean isPlayerPet(Entity entity) {
        return entity.getPersistentData().getBoolean(PLAYER_SUMMON_TAG);
    }

    private static boolean spawnEmerging(
            ServerLevel level,
            BlockPos exitPos,
            Mob mob,
            MobSpawnType spawnType,
            RandomSource random,
            SummonConfigurator configurator) {
        double endY = exitPos.getY();
        double startY = endY - 1.35D;
        float rotation = random.nextFloat() * 360.0F;

        mob.moveTo(exitPos.getX() + 0.5D, startY, exitPos.getZ() + 0.5D, rotation, 0.0F);
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(exitPos), spawnType, null);
        configurator.configure(mob);
        mob.setNoAi(true);
        mob.setNoGravity(true);
        mob.setSilent(true);

        level.addFreshEntity(mob);
        emergingUndeadEffect(level, exitPos, random);
        EMERGING_UNDEAD.add(new EmergingUndead(level, mob.getUUID(), startY, endY));
        return true;
    }

    @Nullable
    private static Mob createUndead(ServerLevel level, boolean skeleton) {
        return skeleton ? EntityType.SKELETON.create(level) : EntityType.ZOMBIE.create(level);
    }

    @Nullable
    private static BlockPos findEmergenceSpotInColumn(ServerLevel level, BlockPos column) {
        for (int y = column.getY() + 2; y >= column.getY() - 3; y--) {
            BlockPos feet = new BlockPos(column.getX(), y, column.getZ());
            BlockPos head = feet.above();
            BlockPos ground = feet.below();
            BlockState feetState = level.getBlockState(feet);
            BlockState headState = level.getBlockState(head);
            BlockState groundState = level.getBlockState(ground);

            if ((feetState.isAir() || feetState.getCollisionShape(level, feet).isEmpty())
                    && (headState.isAir() || headState.getCollisionShape(level, head).isEmpty())
                    && groundState.isFaceSturdy(level, ground, Direction.UP)) {
                return feet;
            }
        }

        return null;
    }

    private static void markPlayerPet(Mob mob, ServerPlayer owner) {
        CompoundTag data = mob.getPersistentData();
        data.putBoolean(PLAYER_SUMMON_TAG, true);
        data.putBoolean(SUNPROOF_TAG, true);
        data.putUUID(OWNER_UUID_TAG, owner.getUUID());
    }

    private static void installPetGoals(Mob mob) {
        if (PET_GOALS_INSTALLED.add(mob)) {
            mob.goalSelector.addGoal(4, new FollowSummonerGoal(mob, 1.1D, 7.0F, 2.5F));
        }
    }

    private static void equipPetSunproofHelmet(Mob mob) {
        ItemStack helmet = mob.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) {
            helmet = new ItemStack(Items.CHAINMAIL_HELMET);
            mob.setItemSlot(EquipmentSlot.HEAD, helmet);
            mob.setDropChance(EquipmentSlot.HEAD, 0.0F);
        }

        if (helmet.isDamageableItem()) {
            helmet.setDamageValue(0);
        }
    }

    private static void equipNecromancerSummon(ServerLevel level, Mob mob, RandomSource random) {
        boolean skeleton = mob instanceof Skeleton;
        ItemStack weapon = skeleton ? new ItemStack(Items.BOW) : new ItemStack(random.nextBoolean() ? Items.GOLDEN_SWORD : Items.IRON_SWORD);
        if (skeleton) {
            var enchantments = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            weapon.enchant(enchantments.getHolderOrThrow(Enchantments.POWER), 1 + random.nextInt(2));
        }

        damageAroundHalf(weapon, random);
        mob.setItemSlot(EquipmentSlot.MAINHAND, weapon);
        mob.setDropChance(EquipmentSlot.MAINHAND, 0.08F);

        EquipmentSlot armorSlot = switch (random.nextInt(4)) {
            case 0 -> EquipmentSlot.HEAD;
            case 1 -> EquipmentSlot.CHEST;
            case 2 -> EquipmentSlot.LEGS;
            default -> EquipmentSlot.FEET;
        };
        ItemStack armor = ironArmor(armorSlot);
        damageAroundHalf(armor, random);
        mob.setItemSlot(armorSlot, armor);
        mob.setDropChance(armorSlot, 0.06F);
    }

    private static ItemStack ironArmor(EquipmentSlot slot) {
        return new ItemStack(switch (slot) {
            case HEAD -> Items.IRON_HELMET;
            case CHEST -> Items.IRON_CHESTPLATE;
            case LEGS -> Items.IRON_LEGGINGS;
            case FEET -> Items.IRON_BOOTS;
            default -> Items.IRON_HELMET;
        });
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

    @SubscribeEvent
    public void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide && event.getEntity() instanceof Mob mob && isPlayerPet(mob)) {
            installPetGoals(mob);
            equipPetSunproofHelmet(mob);
        }
    }

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {
        tickEmergingUndead();

        petScanTimer++;
        if (petScanTimer < PET_SCAN_TICKS) {
            return;
        }
        petScanTimer = 0;
        long gameTime = event.getServer().overworld().getGameTime();
        ORDERED_TARGETS.entrySet().removeIf(entry -> entry.getValue().expiresAt() <= gameTime);

        for (ServerLevel level : event.getServer().getAllLevels()) {
            for (Entity entity : level.getAllEntities()) {
                if (entity instanceof Mob mob && isPlayerPet(mob)) {
                    installPetGoals(mob);
                    equipPetSunproofHelmet(mob);
                    mob.clearFire();
                    retargetPet(level, mob);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !isPlayerPet(mob)) {
            return;
        }

        LivingEntity target = event.getNewAboutToBeSetTarget();
        if (target != null && !canPetAttack(mob, target, isOrderedTarget(mob, target))) {
            event.setNewAboutToBeSetTarget(null);
        }
    }

    @SubscribeEvent
    public void onLivingDamage(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        Entity attacker = source.getEntity();
        LivingEntity damaged = event.getEntity();

        if (attacker instanceof ServerPlayer owner && canOwnerCommandTarget(owner, damaged)) {
            orderOwnedPets(owner, damaged);
        }

        if (damaged instanceof ServerPlayer owner && attacker instanceof LivingEntity threat && canOwnerCommandTarget(owner, threat)) {
            orderOwnedPets(owner, threat);
        }
    }

    private static boolean canOwnerCommandTarget(ServerPlayer owner, LivingEntity target) {
        return target.isAlive()
                && target != owner
                && !(target instanceof ServerPlayer)
                && !isPlayerPet(target);
    }

    private static void orderOwnedPets(ServerPlayer owner, LivingEntity target) {
        if (!(owner.level() instanceof ServerLevel level)) {
            return;
        }

        AABB search = owner.getBoundingBox().inflate(PET_SEARCH_RADIUS, 12.0D, PET_SEARCH_RADIUS);
        long expiresAt = level.getGameTime() + PET_COMMAND_TICKS;
        for (Mob pet : level.getEntitiesOfClass(Mob.class, search, mob -> isPlayerPet(mob) && isOwnedBy(mob, owner))) {
            if (!pet.isNoAi() && canPetAttack(pet, target, true)) {
                ORDERED_TARGETS.put(pet.getUUID(), new OrderedTarget(target.getUUID(), expiresAt));
                pet.setTarget(target);
            }
        }
    }

    private static void retargetPet(ServerLevel level, Mob pet) {
        LivingEntity currentTarget = pet.getTarget();
        if (currentTarget != null && currentTarget.isAlive() && canPetAttack(pet, currentTarget, isOrderedTarget(pet, currentTarget))) {
            return;
        }

        pet.setTarget(null);
        if (pet.isNoAi()) {
            return;
        }

        LivingEntity hostile = findNearestHostile(level, pet);
        if (hostile != null) {
            pet.setTarget(hostile);
        }
    }

    @Nullable
    private static LivingEntity findNearestHostile(ServerLevel level, Mob pet) {
        AABB search = pet.getBoundingBox().inflate(18.0D, 8.0D, 18.0D);
        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (LivingEntity candidate : level.getEntitiesOfClass(LivingEntity.class, search, entity -> canPetAttack(pet, entity, false))) {
            double distance = pet.distanceToSqr(candidate);
            if (distance < nearestDistance) {
                nearest = candidate;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    private static boolean canPetAttack(Mob pet, LivingEntity target, boolean ordered) {
        return target.isAlive()
                && target != pet
                && !(target instanceof ServerPlayer)
                && !isPlayerPet(target)
                && !isOwner(pet, target)
                && !hasSameOwner(pet, target)
                && (ordered || target instanceof Enemy || target instanceof NecromancerEntity);
    }

    private static boolean isOrderedTarget(Mob pet, LivingEntity target) {
        OrderedTarget ordered = ORDERED_TARGETS.get(pet.getUUID());
        return ordered != null && ordered.targetId().equals(target.getUUID());
    }

    private static boolean isOwnedBy(Entity entity, ServerPlayer owner) {
        CompoundTag data = entity.getPersistentData();
        return data.hasUUID(OWNER_UUID_TAG) && owner.getUUID().equals(data.getUUID(OWNER_UUID_TAG));
    }

    private static boolean isOwner(Entity pet, Entity possibleOwner) {
        CompoundTag data = pet.getPersistentData();
        return data.hasUUID(OWNER_UUID_TAG) && possibleOwner.getUUID().equals(data.getUUID(OWNER_UUID_TAG));
    }

    private static boolean hasSameOwner(Entity pet, Entity other) {
        CompoundTag petData = pet.getPersistentData();
        CompoundTag otherData = other.getPersistentData();
        return petData.hasUUID(OWNER_UUID_TAG)
                && otherData.hasUUID(OWNER_UUID_TAG)
                && petData.getUUID(OWNER_UUID_TAG).equals(otherData.getUUID(OWNER_UUID_TAG));
    }

    @Nullable
    private static ServerPlayer getOwner(Mob pet) {
        CompoundTag data = pet.getPersistentData();
        if (!data.hasUUID(OWNER_UUID_TAG) || !(pet.level() instanceof ServerLevel level)) {
            return null;
        }

        return level.getServer().getPlayerList().getPlayer(data.getUUID(OWNER_UUID_TAG));
    }

    private static void tickEmergingUndead() {
        Iterator<EmergingUndead> iterator = EMERGING_UNDEAD.iterator();
        while (iterator.hasNext()) {
            EmergingUndead emerging = iterator.next();
            if (emerging.tick()) {
                iterator.remove();
            }
        }
    }

    @FunctionalInterface
    private interface SummonConfigurator {
        void configure(Mob mob);
    }

    private record OrderedTarget(UUID targetId, long expiresAt) {
    }

    private static final class FollowSummonerGoal extends Goal {
        private final Mob mob;
        private final double speedModifier;
        private final float startDistance;
        private final float stopDistance;
        @Nullable
        private LivingEntity owner;
        private int timeToRecalculatePath;

        private FollowSummonerGoal(Mob mob, double speedModifier, float startDistance, float stopDistance) {
            this.mob = mob;
            this.speedModifier = speedModifier;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(java.util.EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            ServerPlayer owner = getOwner(mob);
            if (owner == null || !owner.isAlive() || mob.getTarget() != null) {
                return false;
            }

            this.owner = owner;
            return mob.distanceToSqr(owner) > startDistance * startDistance;
        }

        @Override
        public boolean canContinueToUse() {
            return owner != null
                    && owner.isAlive()
                    && mob.getTarget() == null
                    && !mob.getNavigation().isDone()
                    && mob.distanceToSqr(owner) > stopDistance * stopDistance;
        }

        @Override
        public void stop() {
            owner = null;
            mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (owner == null) {
                return;
            }

            mob.getLookControl().setLookAt(owner, 10.0F, mob.getMaxHeadXRot());
            if (--timeToRecalculatePath > 0) {
                return;
            }
            timeToRecalculatePath = 10;

            if (mob.distanceToSqr(owner) > 256.0D && mob.level() instanceof ServerLevel level) {
                teleportNearOwner(level, owner);
            } else {
                mob.getNavigation().moveTo(owner, speedModifier);
            }
        }

        private void teleportNearOwner(ServerLevel level, LivingEntity owner) {
            BlockPos ownerPos = owner.blockPosition();
            for (int attempt = 0; attempt < 10; attempt++) {
                int dx = mob.getRandom().nextInt(7) - 3;
                int dz = mob.getRandom().nextInt(7) - 3;
                if (Math.abs(dx) < 2 && Math.abs(dz) < 2) {
                    continue;
                }

                BlockPos spot = findEmergenceSpot(level, ownerPos.offset(dx, 0, dz));
                mob.moveTo(spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D, mob.getYRot(), mob.getXRot());
                mob.getNavigation().stop();
                return;
            }
        }
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
}
