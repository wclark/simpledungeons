package com.github.wclark.simpledungeons;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class NecromancerEntity extends Monster implements RangedAttackMob {
    private static final int ATTACK_INTERVAL_TICKS = 20;
    private static final float ATTACK_RANGE_BLOCKS = 24.0F;
    private static final float ORB_SPEED = 0.25F;
    private static final int SUMMON_CAST_TICKS = 40;
    private static final int SUMMON_RELEASE_TICK = 18;
    private static final EntityDataAccessor<Boolean> DATA_SUMMONING = SynchedEntityData.defineId(
            NecromancerEntity.class,
            EntityDataSerializers.BOOLEAN);
    private int summonCooldownTicks;
    private int summonCastTicks;
    private boolean summonReleased;

    public NecromancerEntity(EntityType<? extends NecromancerEntity> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.23D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SUMMONING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, ATTACK_INTERVAL_TICKS, ATTACK_RANGE_BLOCKS));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        LivingEntity target = this.getTarget();
        this.setAggressive(target != null && target.isAlive());
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            this.tickSummoning(serverLevel, target);
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        super.populateDefaultEquipmentSlots(random, difficulty);
        this.equipStaff();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        this.equipStaff();
        return result;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float velocityScale) {
        if (this.isSummoning() || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        this.swing(InteractionHand.MAIN_HAND);
        BlueOrbEntity orb = new BlueOrbEntity(serverLevel, this);
        orb.setPos(this.getX(), this.getEyeY() - 0.15D, this.getZ());
        Vec3 targetEye = target.getEyePosition();
        double distance = orb.position().distanceTo(targetEye);
        double leadTicks = Math.min(18.0D, distance / ORB_SPEED * 0.35D);
        Vec3 predictedTarget = targetEye.add(target.getDeltaMovement().scale(leadTicks));
        Vec3 aim = predictedTarget.subtract(orb.position());
        orb.shoot(aim.x, aim.y, aim.z, ORB_SPEED, 0.0F);
        serverLevel.addFreshEntity(orb);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.HOSTILE, 0.8F, 1.2F);
    }

    public boolean isSummoning() {
        return this.entityData.get(DATA_SUMMONING);
    }

    private void setSummoning(boolean summoning) {
        this.entityData.set(DATA_SUMMONING, summoning);
    }

    private void tickSummoning(ServerLevel level, @Nullable LivingEntity target) {
        if (summonCooldownTicks > 0) {
            summonCooldownTicks--;
        }

        if (summonCastTicks > 0) {
            setSummoning(true);
            this.getNavigation().stop();
            if (target != null) {
                this.getLookControl().setLookAt(target, 30.0F, 30.0F);
            }

            if (summonCastTicks % 2 == 0) {
                level.sendParticles(
                        ParticleTypes.ENCHANT,
                        this.getX(),
                        this.getY() + 1.15D,
                        this.getZ(),
                        12,
                        0.7D,
                        0.8D,
                        0.7D,
                        0.35D);
            }

            summonCastTicks--;
            if (!summonReleased && summonCastTicks <= SUMMON_RELEASE_TICK) {
                summonReleased = true;
                summonUndead(level, target);
            }

            if (summonCastTicks <= 0) {
                setSummoning(false);
                summonReleased = false;
                summonCooldownTicks = 100 + this.getRandom().nextInt(101);
            }
            return;
        }

        setSummoning(false);
        if (target != null && target.isAlive() && summonCooldownTicks <= 0) {
            startSummoning(level);
        }
    }

    private void startSummoning(ServerLevel level) {
        summonCastTicks = SUMMON_CAST_TICKS;
        summonReleased = false;
        setSummoning(true);
        this.swing(InteractionHand.MAIN_HAND);
        level.playSound(null, this.blockPosition(), SoundEvents.EVOKER_PREPARE_SUMMON, SoundSource.HOSTILE, 0.9F, 0.85F);
    }

    private void summonUndead(ServerLevel level, @Nullable LivingEntity target) {
        BlockPos focus = target == null ? this.blockPosition() : target.blockPosition();
        for (int i = 0; i < 2; i++) {
            int dx = this.getRandom().nextInt(7) - 3;
            int dz = this.getRandom().nextInt(7) - 3;
            if (Math.abs(dx) < 2 && Math.abs(dz) < 2) {
                dx += dx < 0 ? -2 : 2;
            }

            UndeadSummons.spawnNecromancerUndead(
                    level,
                    focus.offset(dx, 0, dz),
                    this.getRandom().nextBoolean(),
                    this.getRandom());
        }

        level.playSound(null, this.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.HOSTILE, 0.9F, 1.0F);
    }

    private void equipStaff() {
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.SUMMONERS_STAFF.get()));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }
}
